# Introduction

Introduction to Lambda Architecture and Kappa Architecture. Then CQL and how we used CQL concepts to implement KappaQL.

Rest of the paper is organized as follows. Section \ref{background} briefly discuss about CQL which Freshet's query DSL is based on, Lambda Architecture and Kappa Architecture which inspired this work. Then in the Section \ref{freshet} we discuss the underlying concepts of Freshet, its query DSL and also discuss the Freshet implementation details including its overall architecture. In Section \ref{evaluation}, we present some preliminary benchmarks of Freshet and comparison of Freshet query DSL to other modern stream processing programming APIs. Section \ref{related-work} discuss work similar to Freshet and how Freshet is different/similar from/to them. Then in the following sections \ref{conclusion-and-discussion} \ref{future-work} we conclude the paper and discuss some improvements we can do to Freshet to improve its useability and scalability.

# Background

In this section we discuss the inspirations [\ref{lambda-architecture}, \ref{kappa-architecture}] behind this work and some of the early works(\ref{cql}, \ref{apache-samza}) this work is based upon.

## Lambda Architecture

*Lambda Architecture* \cite{marz2013big} is a framework usefull for designing and implementing reliable, scalable, fault-tolerant and functional big data applications. Designed by Nathan Marz, based on his experience working on distributed big data applications, this generic architecture trying to address various functional and non-functional requirements of distributed data processing systems.

- Fault-tolerance against software, hardware failure and human errors
- Support real-time, near real-time and batch workloads
- Linear scalability and scale out instead of scale up
- Extensibility and flexibility to accommodate changing requirements

Lambda architecture is composed out of three major components -- *batch layer*, *speed layer* and *service layer*. Input data is dispatched to both *batch* and *speed* layers. *Batch layer* manages the master dataset while also providing pre-computed batch views. *Speed layer* fill the latency gap of *batch layer* by generating mergeable views from recent data. *Serving layer* is responsible for indexing *batch views* for low-latency querying. Queries are answered by merging views from *batch* and *speed* layers.

Another most important concepts in Lambda Architecture is retaining raw input data that are immutable, which enables us to process data in ways that didn't originally planned and enables re-computation in case of changing requirements or algorithm error. Twitter's *Summingbird* \cite{boykin2014summingbird} is one of the first implementation of Lambda Architecture on top of Hadoop and Storm. Summingbird uses mathematical concept *Monoid* to model views that are mergeable.

One of the issues with *Lambda Architecture* is lack of software tooling which hides the complexities of implementing it. When implementing Lambda Architecture using tools like Hadoop and Apache Storm, you have to write same/similar logic twice for speed layer and batch layer. Then you have to implement query layer which merge views from speed and batch layers to provide final output. Even though *Lambda Architecture* is good as a reference architecture for implementing robust, scalable and flexible Big Data processing systems, lot of work is needed to realize this architecture. *Kappa Architecture* which has the notion of -- Everything Is A Stream -- is proposed as an alternative these problems. One important aspect of this new arhcitecture is you have to only write you business logic once and infrastrcture supporting Kappa Architecture will simulate batch model by replaying the data/event stream through the speed layer when historical data is needed. Next subsection discuss this new architecture, advantages and possible issues.


## Kappa Architecture

Jay Kreps: Unlike the Lambda Architecture, in this approach you only do reprocessing when your processing code changes, and you actually need to recompute your results. And, of course, the job doing the re-computation is just an improved version of the same code, running on the same framework, taking the same input data. Naturally, you will want to bump up the parallelism on your reprocessing job so it completes very quickly.

## CQL

CQL \cite{arasu2006cql} - aka Continuous Query Language - is a SQL-based declarative language for expressing queries over data streams and time varying relations. CQL's abstract semantics are based on two data types - streams and relations - and three types of operations - *stream-to-relations*, *relation-to-relation* and *relation-to-stream*. CQL take advantage of well understood relational semantics and keep the language simpler and queries compact by introducing minimal changes to SQL.

* Window specification derived from SQL-99 to transform streams to relations
* Three new operators to transform time varying relations into streams.

\begin{lstlisting}[language=SQL, caption=CQL Rstream operator and window specification]
SELECT Rstream(*)
 FROM PosSpeedStr [Now]
 WHERE speed > 65
\end{lstlisting}

CQL uses SQL for *relation-to-relation* transformation while relations in CQL is different from relations in SQL due to the fact that the CQL relations vary with time. Concepts like *plus-minus streams* which used in CQL prototype to encode both streams and relations in a unified way, *synopses* which is *plus-minus streams* are based on are still useful in contexts like Freshet \ref{implementation}. **Need to talk how these concepts are a good match for implementing Kappa architecture**.

CQL also comes with several syntactic shortcuts to reduce the complexity of simple queries as well as couple of equivalences which enable query optimizations.

Freshet uses semantics and concepts in CQL to implement its continuous query DSL as discussed in \ref{dsl}. More information about CQL concepts and how they are mapped to modern streaming system can also be found in Section \ref{dsl}.

*Need to discuss why we choose CQL. Refer to papers like 'Query Languages and Data Models for Database Sequences and Data Streams' amd Temporal Stream Algebra'. Also talk about non-blocking and blocking operators of SQL in the context of CQL. Also its better to refer to StreamSQL. Best way is to list down the issues each work addressed, solutions and then compare them with CQL and our work.*

## Apache Samza

Apache Samza \cite{asf:2014:samza} is a open source distributed stream processing systems built on top of Apache Kafka \cite{kreps2011kafka} messaging system and Apache YARN \cite{vavilapalli2013apache} resource managment framework. Stream processing logic in Samza applications are built on top of *stream* and Samza *job*  abstractions. Samza job can read multiple input streams, process/transform tuples from these streams and append resulting tuples to one or more output streams. Scalability is achieved by *stream partitioning* and dividing a job in to *multiple tasks* where each task consumes data from one partition for each of the input streams. Samza *stream* is a ordered sequence of immutable *messages* of similar type and support pluggable implementations of *stream* abstraction. Data flow graphs or stream processing topologies are composed by using one jobs output stream as other jobs input stream in the context of Samza. Jobs are independent of each other except the dependency between output and input streams of jobs.

Even though Apache Storm is the widely used open source distributed stream processing system, following properties of Samza makes it the most suitable option for implementing Kappa Architecture.

* Samza it self manage the snapshotting and restoration of stream processor's state based on ordered and replayable streams support of Kafka.
* Kafka which is the message layer used by Samza guarantee that messages are processed in order there are written and Kafka ensures that no messages are ever lost. This ordering and fault tolerance features make Samza suitable for implementing time varying relations in CQL.
* Samza utilizes Kafka's partitioning capability to implement scalability. This makes it easy to parallelize CQL operators like aggregation and group-by.
* Samza allows to keep stream processors local state in a key/value storage local to stream processor. This makes it easy to incorporate, concepts like *Synopses* found in CQL to Freshet.

# Freshet

Freshet implements subset of CQL on top of Apache Samza. At this stage Freshet lacks support for *joins*, and *sub queries*. We are planning to add these feature in future and more details can be found in Section \ref{future-work}.

Freshet is build out of five main components as shown in Figure \ref{architecture}.

- **Query DSL**: Implemented as a Clojure DSL and used to express CQL queries against streams. Queries expressed in Freshet DSL will get compiled in to relation algebra model and then will get converted into execution plan which consists of set of operators written as Samza stream tasks connected together as a DAG via Kafka queues.
- **Query Compiler**: Compile SQL model generated from DSL to  intermediate representation based on relation algebra which can be converted in to execution plan.
- **Execution Planner**: Generate execution plans (Samza jobs connected via input, intermediate and output streams to form a DAG) based on intermediate representation and current status of the Freshet cluster.
- **Scheduler**: Does the actual scheduling of Samza Jobs.
- **Query Operators**: Samza stream tasks. Implement CQL operators like *window*, *select*, *aggregate*, and view generation operators like *rstream*, *istream*. Theses operators, connected via intermediate streams perform stream processing according to the query express in Freshet DSL.


\begin{figure}[htpb]
\centering
\includegraphics[width=\linewidth]{../../images/freshet-arch.pdf}
\caption{Freshet Architecture\label{architecture}}
\end{figure}


## Query DSL


Define subset of CQL supported and the DSL used to describe it.

## Execution Model

Describe how execution concepts in CQL paper is mapped to Samza based implementation.

# Evaluation

Implement application on pure Samza and then using this library.

## Example Application

We use several real-time statistics calculation queries on Wikipedia activity stream for demonstration and evaluation of Freshet DSL and the query execution layer. Activity in Wikipedia activity stream is a JSON message which looks like \ref{wikipedia-activity}.

\begin{lstlisting}[label=wikipedia-activity, language=json, caption=Wikipedia Activity,breaklines=true]
{
  "anonymous": False,
  "comment": "Changed statement that orbit was the eye to saying that the orbit was the eye socket for accuracy",
  "delta": 7,
  "flag": "M",
  "namespace": "article",
  "newPage": False,
  "page": "Optic nerve",
  "pageUrl": "http://en.wikipedia.org/wiki/Optic_nerve",
  "robot": False,
  "unpatrolled": False,
  "url": "http://en.wikipedia.org/w/index.php?diff=449570600&oldid=447889877",
  "user": "Moearly",
  "userUrl": "http://en.wikipedia.org/wiki/User:Moearly",
  "wikipedia": "#en.wikipedia",
  "wikipediaLong": "English Wikipedia",
  "wikipediaShort": "en",
  "wikipediaUrl": "http://en.wikipedia.org"
}
\end{lstlisting}

### The Set of Active Pages

This query outputs a relation containing, the set of "active pages" at any time instant. Pages we saw in Wikipedia activity stream within the last 60 seconds.

\begin{lstlisting}[label=active-pages, language=freshet, caption=The Set of Active Pages,breaklines=true]
(select wikipedia-activity
	(modifiers :distinct)
	(window (range 60))
\end{lstlisting}

### All Edits With **bytes-changed** Greater Than 100

This query outputs a stream of Wikipedia edits where size of change is greater than 100.

This query can be written in three different ways, using *insert stream* operator, *relation stream* operator and using defaults.

-  We tell Freshet to convert relation generated by applying window operator to a stream, by adding **:istream** modifier to the query:
\begin{lstlisting}[label=bytes-changed-i, language=freshet, caption={bytes-changed > 100 (\textit{insert stream})},breaklines=true]
(select wikipedia-activity
		(modifiers :istream)
		(window (unbounded))
		(where {:delta [> 100]}))
\end{lstlisting}
-  For *relation stream* we need to use **:rstream** modifier.
\begin{lstlisting}[label=bytes-changed-r, language=freshet, caption={bytes-changed > 100 (\textit{relation stream})},breaklines=true]
(select wikipedia-activity
		(modifiers :rstream)
		(window (now))
		(where {:delta [> 100]}))
\end{lstlisting}
-  Otherwise, we can use defaults
\begin{lstlisting}[label=bytes-changed, language=freshet, caption={bytes-changed > 100 (\textit{defaults})},breaklines=true]
(select wikipedia-activity
		(where {:delta [> 100]}))
\end{lstlisting}

### Hourly Summary of Wikipedia Edits

In this query we calculate hourly summaries for *number of edits*, *number of bytes added*, and *unique titles* seen.

\begin{lstlisting}[label=hourly-summary, language=freshet, caption=Wikipedia Activity,breaklines=true]
(select wikipedia-activity
	(window (range 60))
	(aggregate (count :*) :edits)
	(aggregate (sum :delta) :bytes-added)
	(aggregate (count-distinct :pageUrl) :unique-titles))
\end{lstlisting}

# Related Work

StreamSQL on Spark can be consider as a related work. Also other attempts like Storm Trident, StreamSQL's Storm support. Summingbird \cite{boykin2014summingbird} which implements Lambda Architecture is also a another related work.

# Conclusion and Discussion

# Future Work

Talks about multi-tenancy, provenance, visualizations.
