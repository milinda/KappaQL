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
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

/**
 * Divide input stream into multiple output streams based on the group by key.
 *
 * 10/08/2014
 * ----------
 * Main issue with group-by operator is lack of support for dynamic routing. Because we don't know the cardinality
 * of the group-by attribute its hard to do static planning. Current solution is to use Kafka topic's partitioning to
 * parallelize the execution among multiple down stream aggregators.
 */
public class GroupByOperator extends Operator implements StreamTask, InitableTask {
    @Override
    public void init(Config config, TaskContext taskContext) throws Exception {
        initOperator(OperatorType.GROUP_BY);
    }

    @Override
    public void process(IncomingMessageEnvelope incomingMessageEnvelope, MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {

    }
}
