/*
 * (C) Copyright 2014 Milinda Pathirage.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.pathirage.kappaql.operators;

import com.google.common.collect.EvictingQueue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Gauge;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.pathirage.kappaql.Constants;
import org.pathirage.kappaql.KappaQLException;
import org.pathirage.kappaql.data.StreamElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class WindowOperator extends Operator implements StreamTask, InitableTask {
    private static Logger log = LoggerFactory.getLogger(WindowOperator.class);

    /* True if this a time-based sliding window. */
    private boolean timeBased;

    /* Range of time based sliding window in seconds. */
    private long range;

    /* sliding window can be divided into slots. */
    private long slotSize;

    /* True if this a tuple-based sliding window. */
    private boolean tupleBased;

    /* Max tuples in tuple based sliding window */
    private int rows;

    /* CQL uses concept called synopses to implement windowing. This stores
     * synopsis as key/value pairs. This assumes every stream element has unique id. */
    private KeyValueStore<String, StreamElement> store;

    /* Window size gauge metric for reporting */
    private Gauge windowSizeGauge;

    /* Current size of the window to handle handle/drop events to/from window as needed. */
    private AtomicLong currentWindowSize = new AtomicLong(0);

    /* Topic to push the downstream. */
    private String downStreamTopic;

    /* Samza System */
    private String system;

    /* Window handler. */
    private WindowHandler windowHandler;

    @Override
    public void init(Config config, TaskContext taskContext) throws Exception {
        initOperator(config.get(Constants.CONF_QUERY_ID, Constants.CONST_STR_UNDEFINED), OperatorType.WINDOW);

        String downStreamTopic = config.get(Constants.CONF_DOWN_STREAM_TOPIC, Constants.CONST_STR_UNDEFINED);
        if (downStreamTopic.equals(Constants.CONST_STR_UNDEFINED)) {
            log.error(Constants.ERROR_UNDEFINED_OUTPUT_STREAM);
            throw new KappaQLException(Constants.ERROR_UNDEFINED_OUTPUT_STREAM);
        }

        this.downStreamTopic = downStreamTopic;
        this.system = config.get(Constants.CONF_SYSTEM, Constants.CONST_STR_DEFAULT_SYSTEM);

        String range = config.get(Constants.CONF_WINDOW_RANGE, Constants.CONST_STR_UNDEFINED);
        if(!range.equals(Constants.CONST_STR_UNDEFINED)){
            this.range = Long.valueOf(range);

            String slotSize = config.get(Constants.CONF_WINDOW_RANGE_SLOT_SIZE, Constants.CONST_STR_UNDEFINED);
            if(!slotSize.equals(Constants.CONST_STR_UNDEFINED)){
                this.slotSize = Long.valueOf(slotSize);
            } else {
                this.slotSize = this.range;
            }

            timeBasedWindow(true);
        }

        String rows = config.get(Constants.CONF_WINDOW_ROWS, Constants.CONST_STR_UNDEFINED);
        if(!rows.equals(Constants.CONST_STR_UNDEFINED) && range.equals(Constants.CONST_STR_UNDEFINED)){
            this.rows = Integer.valueOf(rows);
            timeBasedWindow(false);
        } else {
            timeBasedWindow(true);
            log.warn(Constants.WARN_BOTH_ROWS_AND_RANGE_DEFINED);
        }

        this.store = (KeyValueStore<String, StreamElement>) taskContext.getStore("windowing-synopses");
        this.windowSizeGauge = taskContext.getMetricsRegistry().newGauge(getClass().getName(), "window-size", 0);
    }

    @Override
    public void process(IncomingMessageEnvelope incomingMessageEnvelope,
                        MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        windowHandler.handle((StreamElement)incomingMessageEnvelope.getMessage(), messageCollector);
    }

    private void timeBasedWindow(boolean b){
        if(b){
            this.timeBased = true;
            this.tupleBased = false;
        } else {
            this.timeBased = false;
            this.tupleBased = true;
        }
    }

    public interface WindowHandler {
        public void handle(StreamElement streamElement, MessageCollector messageCollector);
    }

    public class ExtendedEvictingQueue<T> {
        private EvictingQueue<T> queue;
        private int maxSize;

        private ExtendedEvictingQueue(int maxSize) {
            this.maxSize = maxSize;
            this.queue = EvictingQueue.create(maxSize);
        }

        /* This is special extension of evicting queue. If queue is full we return
         * the element we are removed from the queue to handle the new one. Otherwise null
         * is returned. */
        public T add(T t) {
            if (queue.size() == maxSize) {
                T r = queue.peek();

                queue.add(t);

                return r;
            }

            queue.add(t);

            return null;
        }
    }

    public class TupleBasedSlidingWindowHandler implements WindowHandler {
        private int maxSize;
        private KeyValueStore<String, StreamElement> store;
        private ExtendedEvictingQueue<String> evictingQueue;
        private String system;

        public TupleBasedSlidingWindowHandler(int maxSize,
                                       KeyValueStore<String, StreamElement> store,
                                       MessageCollector messageCollector,
                                       String system) {
            this.maxSize = maxSize;
            this.store = store;
            this.evictingQueue = new ExtendedEvictingQueue<String>(maxSize);
            this.system = system;
        }

        public void handle(StreamElement streamElement, MessageCollector messageCollector) {
            String evicted = evictingQueue.add(streamElement.getId());
            if (evicted != null) {
                StreamElement evictedElement = store.get(evicted);
                store.delete(evicted);

                /* Sending element deleted from window to down stream for processing.
                 * Need to set delete property to of StreamElement true. */
                evictedElement.setDelete(true);
                messageCollector.send(new OutgoingMessageEnvelope(new SystemStream(system, downStreamTopic), evictedElement.getId(), evictedElement));
            }

            /* Store the element for synopsis management */
            store.put(streamElement.getId(), streamElement);

            /* Sending insert to window element to down stream for processing. */
            streamElement.setDelete(false);
            messageCollector.send(new OutgoingMessageEnvelope(new SystemStream(system, downStreamTopic), streamElement));
        }
    }
}
