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

import org.apache.samza.config.Config;
import org.apache.samza.metrics.Gauge;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.pathirage.kappaql.Constants;
import org.pathirage.kappaql.data.StreamElement;

import java.util.concurrent.atomic.AtomicLong;

public class WindowOperator extends Operator implements StreamTask, InitableTask {

    /* True if this a time-based sliding window. */
    private boolean timeBased;

    /* Range of time based sliding window in seconds. */
    private long range;

    /* True if this a tuple-based sliding window. */
    private boolean tupleBased;

    /* Max tuples in tuple based sliding window */
    private long rows;

    /* CQL uses concept called synopses to implement windowing.
     * Local store keeps track of these synopses. */
    private KeyValueStore<String, StreamElement> store;

    /* Window size gauge metric for reporting */
    private Gauge windowSizeGauge;

    /* Current size of the window to handle add/drop events to/from window as needed. */
    private AtomicLong windowSize = new AtomicLong(0);

    @Override
    public void init(Config config, TaskContext taskContext) throws Exception {
        initOperator(config.get(Constants.CONF_QUERY_ID, "undefined-query"), OperatorType.WINDOW);

        this.store = (KeyValueStore<String, StreamElement>) taskContext.getStore("windowing-synopses");
        this.windowSizeGauge = taskContext.getMetricsRegistry().newGauge(getClass().getName(), "window-size", 0);
    }

    @Override
    public void process(IncomingMessageEnvelope incomingMessageEnvelope,
                        MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        if (tupleBased) {

        } else {

        }
    }
}
