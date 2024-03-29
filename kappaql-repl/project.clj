(defproject org.pathirage/kappaql "0.1.0-SNAPSHOT"
  :description "KappaQL: Multi-tenant Real-time Analytics and Monitoring"
  :url "http://github.com/milinda/KappaQL"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.apache.samza/samza-api "0.8.0-SNAPSHOT"]
                 [org.apache.samza/samza-serializers_2.10 "0.8.0-SNAPSHOT"]
                 [org.apache.samza/samza-core_2.10 "0.8.0-SNAPSHOT"]
                 [org.apache.samza/samza-yarn_2.10 "0.8.0-SNAPSHOT"]
                 [org.apache.samza/samza-kv_2.10 "0.8.0-SNAPSHOT"]
                 [org.apache.samza/samza-kafka_2.10 "0.8.0-SNAPSHOT"]
                 [org.apache.kafka/kafka_2.10 "0.8.1.1"]
                 [org.slf4j/slf4j-api "1.6.2"]
                 [org.slf4j/slf4j-log4j12 "1.6.2"]
                 [org.codehaus.jackson/jackson-jaxrs "1.8.5"]]
  :repl-options {
                 ;; custom prompt
                 :prompt (fn [ns] (str "\u001B[35m[\u001B[34m" ns "\u001B[35m]\u001B[33m\u03F0ql=>\u001B[m " ))

                 ;; Welcome message when the repl session starts.
                 :welcome (println "Its  KappaQL time!")})
