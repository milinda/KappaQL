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

    protected void initOperator(String queryId, OperatorType type){
        this.type = type;
        this.queryId = queryId;

        if(type != null){
            this.id = type + "-" + this.queryId + "-" + UUID.randomUUID();
        } else {
            throw new RuntimeException("Operator type not defined.");
        }
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
