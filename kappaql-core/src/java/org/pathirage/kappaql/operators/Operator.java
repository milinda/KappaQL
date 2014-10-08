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
import org.pathirage.kappaql.Constants;
import org.pathirage.kappaql.KappaQLException;

import java.util.UUID;

/* In KappaQL, query is transformed in to execution plan which consists of DAG of operators(Samza jobs) connected via
 * Kakfa queues. */
public abstract class Operator {

    /* Type of the query operator */
    private OperatorType type;

    /* Identify the Samza job specific to a query */
    private String id;

    /* Query this job belongs to */
    private String queryId;

    /* Topic to push the downstream. */
    protected String downStreamTopic;

    protected Config config;

    /* Samza System */
    protected String system;

    protected void initOperator(OperatorType type){
        if(config == null){
            throw new KappaQLException("Unable to find the configuration.");
        }

        this.type = type;
        this.queryId = config.get(Constants.CONF_QUERY_ID, Constants.CONST_STR_UNDEFINED);

        if(type != null){
            this.id = type + "-" + this.queryId + "-" + UUID.randomUUID();
        } else {
            throw new KappaQLException("Operator type not defined.");
        }

        String downStreamTopic = config.get(Constants.CONF_DOWN_STREAM_TOPIC, Constants.CONST_STR_UNDEFINED);
        if (downStreamTopic.equals(Constants.CONST_STR_UNDEFINED)) {
            // TODO: Log
        }

        this.downStreamTopic = downStreamTopic;

        this.system = config.get(Constants.CONF_SYSTEM, Constants.CONST_STR_DEFAULT_SYSTEM);
    }


    public OperatorType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getQueryId() {
        return queryId;
    }
}
