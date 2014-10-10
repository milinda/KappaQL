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

package org.pathirage.kappaql.operators.aggregate;

import org.apache.samza.task.MessageCollector;
import org.pathirage.kappaql.data.StreamElement;

public class Count extends AggregateFunction{

    public Count(String field, String alias){
        this.field = field;
        this.alias = alias;
        this.type = AggregateType.COUNT;
    }

    @Override
    public void handle(StreamElement streamElement, MessageCollector messageCollector) {

    }
}
