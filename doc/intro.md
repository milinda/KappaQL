# KappaQL

**KappaQL** is a SQL like Clojure DSL implementing
[Kappa](http://radar.oreilly.com/2014/07/questioning-the-lambda-architecture.html)
[Architecture](https://www.youtube.com/watch?v=fU9hR3kiOK0) proposed
originally by LinkedIn engineers [Jay
Kreps](https://twitter.com/jaykreps) and [Martin
Kleppmann](http://martin.kleppmann.com).

## What Is Kappa Architecture?

The general concept of DB. Web app architecture, you have a client talking to backend and backend talks to DB when it needs to query or store. Typically we keep application layer stateless to handle scalability, and state will be look from the DB. Works well with HTTP, because HTTP is stateless. DB is giant, global shared state. All those things such as actors, go routine trying to get rid of global shared memory. But databases are stuck with giant global mutable state. This is the way we building it for ages. What are the other possibilities of building these  stateful systems.

Look at 4 example which inspire us to think differently.

1. *Replication* - Write to master/leader, any writes that lead to this copied over to slaves/followers.

Update the product quantity in sample DB. Write first goes to master. Different implementations (WAL, Logical Log).

The replication message has a different characteristic for query

```
update cart set quantity = 3 where customer_id = 123 and product_id = 999
```

But replication message to slaves can be like following.

```
change row 8765
old = [123, 999, 1]
new= [123, 999, 3]
```

From imperative statement to event that is being replicated. It state certain point in time customer decided to change the quantity. This is a immutable fact.

2. *Secondary Indexes* - Allow table to query by different field. Create indexes for multiple column. What does the DB do for these queries. It will go through the entire table and create auxiliary data structure for the indexes. Each index have separate data structures looks like key/value pairs.

Prices of going from base table to index is completely mechanical. When ever the change happen to DB, DB will update the index. Some DBs like Postgres allow concurrent index creations.

3. *Caching* - Application level caching (memcached, redis). Manage the cache in application code. First look in cache, if not in cache go to underlying DB.

Problems
 - Invalidation: How do you when to update the cache so that it represent whats in the DB
 - Race conditions/consistency issues: Update DB in one order and cache in other order. People ignore this.
- Cold start/bootstrapping: Will hit the DB for all requests if cache goes down and came back empty.

4 *Materialize Views* 

```
CREATE VIEW example(foo)
AS SELECT foo 
      FROM bar
     WHERE ..
```

Wrapper around the table. DB will re-write the query.

Materialize view is different. When you create materialize view, DB will scan the table and copy the query results to the materialized view. DB needs to maintain this materialize view with the changes to underlying DB. 

This is similar to cache.

Some DBs allow arbitrary code inside a procedure. 

All 4 above have commonality is all forms of derived data. Take underlying dataset and transform it to different form.

### Let's rethink materialized view

Ideal architecture for something like cache/materialized view.

Think of the replication scenario, and look at the replication event log. Take this internal details about replication and make it a first class citizen. 

Append the *writes* to the stream, and stream becomes a really simple data structure. Appending to a file at the end. Read is simple. Kafka implements this.

You can write to it efficiently, but read is not efficient. We can consume this stream and write to materialized view. Stream processing framework, process incoming message, transform it and write to some kind of a view. 

Interesting thing is if you need to create a new way, go to Kafka and replay from the beginning and create the new view.

Kapa architecture, no bath processing. Materialized view building in the stream processing system.

Whole architecture is based around idea of a log. Whats the point?

3 interesting things

1. Better quality data - Whether to optimize for writes or reads. When separate the writes and reads, you can create different views with different de-normalization for efficient/rich reads. You can re-run it the view generation at any time. 
    > 1. Good for analytics. Shopping cart example
    > 2. Separation of concerns between writing and reading
    > 3. Write once, read from many different views
    > 4. Historical point-int-time queries
    > 5. Recovery from human errors. 
2. Fully precomputed caches - Materialized view is fully pre-computed cache. No such thing as cold start. No cache miss. No race conditions. No complete invalidation. Better isolations/robustness.
3. Streams Everywhere - Compare with traditional web app flow (web app -> business logic (Not in good state/ subscription to changes) -> cache -> UI Logic (F Reactive) -> HTML Dom -> Video Render). Consider this as a mattered view on top of the other. Log centric architecture. Materialized views which get updated as streams coming in, clients subscribe to these views and use functional reactive programming to update the UI. Weakest point is the DB end. Instead of Request/Response, Subscribe/Notify. Throw away REST. Build apps for future.
 

## Design and Implementation

### TODOS (10/15/2014)

* Define minimal set of CQL constructs to support
* Define set of samples which shows the usefulness of above subset
* Design the DSL based on above
* Define the internal representation of CQL
* CQL to Execution Plan
* Understand how IStream, DStream and RStream works and their semantics in CQL 

### KappaQL Query Layer Design Notes

* First problem is what is the serialization format of the events comes in to Kafka from outside world. For the 
  prototype we can use flat JSON objects.
* Then how we are going to define the stream:Given that we choose JSON as the serialization format above, we can just use a mapping of fields to their types as the stream definition. Then the problem is how we annotate the ID/Primary Key of this stream in the definition.And also which field contains the timestamp. In the first version its mandatory to have a timestamp field.We can use something like follows.
    
     ```clojure
     (defstream stream
         (fields [:name :string :address :string :age :integer :timestamp :long])
         (pk :id)
         (ts :timestamp))
     ```
    



### User Interaction (1st Prototype as of 10/02/2014)

1.  User starts the repl using `lein repl` from the KappaQL project base
    directory.
2.  Bootstrap the environment with information about Samza cluster(YARN,
Kafka and Zookeeper) and view storage(Cassandra/Redis).

    > Bootstrap will spawn a web server which expose views generated by
    > KappaQL.This web app will directly talk to view storage and
    > retrieve views.

3.  Write queries and submit them.
4.  Browse to KappaQL Views web app and view the output real-time.

### What Happens When User Runs A Query

*During the bootstrap process, project will get build, packed and uploaded to YARN. This makes sure we have all the required artifacts when original query is submitted.*

1.  Query compiler read cluster meta-data and generate execution plan.
2.  Then using Samza job submission tool we submit the real job to YARN.


### Implementation Plan

1.  Implement the first part of the bootstrap process where it builds the Samza cluster configuration.
2.  Design basic query DSL and execution.
3.  KappaQL Views web application.
