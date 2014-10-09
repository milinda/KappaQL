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

package clojure.kappaql;

public class Constants {
    public static final String CONST_STR_UNDEFINED = "kappaql.undefined";
    public static final String CONST_STR_DEFAULT_SYSTEM = "kafka";

    public static final String CONF_QUERY_ID = "clojure.kappaql.query.id";
    public static final String CONF_SYSTEM = "clojure.kappaql.system";
    public static final String CONF_DOWN_STREAM_TOPIC = "clojure.kappaql.downstream.topic";

    public static final String CONF_WINDOW_RANGE = "clojure.kappaql.window.range";
    public static final String CONF_WINDOW_RANGE_SLOT_SIZE = "clojure.kappaql.window.range.slot.size";
    public static final String CONF_WINDOW_ROWS = "clojure.kappaql.window.rows";

    public static final String CONF_GROUPBY_FIELDS = "clojure.kappaql.groupby.fields";


    public static final String ERROR_UNDEFINED_OUTPUT_STREAM = "Undefined output stream.";
    public static final String ERROR_UNABLE_TO_FIND_CONFIGURATION = "Unable to find the configuration.";
    public static final String ERROR_UNDEFINED_OPERATOR_TYPE = "Undefined operator type.";
    public static final String ERROR_UNDEFINED_GROUP_BY_FIELDS = "Undefined group by fields.";

    public static final String WARN_BOTH_ROWS_AND_RANGE_DEFINED = "Both time based and tuple based windows are defined. Priority goes to time based windows.";
}
