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
import clojure.kappaql.Constants;
import clojure.kappaql.KappaQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/* In KappaQL, query is transformed in to execution plan which consists of DAG of operators(Samza jobs) connected via
 * Kakfa queues. */
public abstract class Operator {
    private static final Logger log = LoggerFactory.getLogger(Operator.class);

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
            log.error(Constants.ERROR_UNABLE_TO_FIND_CONFIGURATION);
            throw new KappaQLException(Constants.ERROR_UNABLE_TO_FIND_CONFIGURATION);
        }

        this.type = type;
        this.queryId = config.get(Constants.CONF_QUERY_ID, Constants.CONST_STR_UNDEFINED);

        if(type != null){
            this.id = type + "-" + this.queryId + "-" + UUID.randomUUID();
        } else {
            log.error(Constants.ERROR_UNDEFINED_OPERATOR_TYPE);
            throw new KappaQLException(Constants.ERROR_UNDEFINED_OPERATOR_TYPE);
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
