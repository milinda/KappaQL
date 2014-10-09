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

package clojure.kappaql.operators;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import clojure.kappaql.data.StreamElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregateOperator extends Operator implements StreamTask, InitableTask {
    private static final Logger log = LoggerFactory.getLogger(AggregateOperator.class);

    private AggregateType type;

    private AggregateFunction aggregateFunction;

    @Override
    public void init(Config config, TaskContext taskContext) throws Exception {

    }

    @Override
    public void process(IncomingMessageEnvelope incomingMessageEnvelope,
                        MessageCollector messageCollector,
                        TaskCoordinator taskCoordinator) throws Exception {

    }

    public interface AggregateFunction {
        public void handle(StreamElement streamElement, MessageCollector messageCollector);
    }

    public class Sum implements AggregateFunction{

        @Override
        public void handle(StreamElement streamElement, MessageCollector messageCollector) {

        }
    }

    public class Count implements AggregateFunction{

        @Override
        public void handle(StreamElement streamElement, MessageCollector messageCollector) {

        }
    }

    public class Min implements AggregateFunction {

        @Override
        public void handle(StreamElement streamElement, MessageCollector messageCollector) {

        }
    }

    public class Max implements AggregateFunction {

        @Override
        public void handle(StreamElement streamElement, MessageCollector messageCollector) {

        }
    }

    public class Average implements AggregateFunction {

        @Override
        public void handle(StreamElement streamElement, MessageCollector messageCollector) {

        }
    }

}
