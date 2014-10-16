(defproject org.pathirage/kappaql-core "0.1.0-SNAPSHOT"
  :description "KappaQL Core: Implements KappaQL Architecture On Top Of Samza."
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
                 [org.codehaus.jackson/jackson-jaxrs "1.8.5"]
                 [com.google.guava/guava "18.0"]
                 [com.esotericsoftware/kryo "3.0.0"]]
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :test-paths ["test/clojure" "test/java"]
  :profiles {:test {:resource-paths ["test/resources"]}})
