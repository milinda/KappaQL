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

import com.google.common.base.Splitter;
import org.pathirage.kappaql.Constants;
import org.pathirage.kappaql.KappaQLException;

import java.util.Map;

public class AggregateFunctionFactory {
    public static AggregateFunction buildAggregateFunction(String config){
        Map<String, String> aggregateConfig = parseMap(config);

        AggregateType type = AggregateType.valueOf(aggregateConfig.get(Constants.CONF_AGGREGATE_TYPE));
        String field = aggregateConfig.get(Constants.CONF_AGGREGATE_FIELD);
        String alias = aggregateConfig.get(Constants.CONF_AGGREGATE_ALIAS);

        switch (type) {
            case AVG:
                return new Average(field, alias);
            case SUM:
                return new Sum(field, alias);
            case MAX:
                return new Max(field, alias);
            case MIN:
                return new Min(field, alias);
            case COUNT:
                return new Count(field, alias);
            default:
                throw new KappaQLException("Unsupported aggregate type.");
        }
    }

    private static Map<String, String> parseMap(String formattedMap) {
        return Splitter.on(",").withKeyValueSeparator("=").split(formattedMap);
    }
}