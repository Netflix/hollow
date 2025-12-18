<p align="center">
  <img src="img/logo.png" alt="log"/>
</p>

## The Problem

Software engineers often encounter problems which require the dissemination of small or moderately sized data sets which don’t fit the label “big data”.  To solve these problems, we often send the data to an RDBMS or nosql data store and query it at runtime, or serialize the data as json or xml, distribute it, and keep a local copy on each consumer.


Scaling each of these solutions presents different challenges.  Sending the data to an RDBMS, nosql data store, or even a memcached cluster may allow your dataset to grow indefinitely large, but there are limitations on the latency and frequency with which you can interact with that dataset.  Serializing and keeping a local copy (if in RAM) can allow many orders of magnitude lower latency and higher frequency access, but this approach has many scaling challenges:

* The dataset size is limited by available RAM.
* The full dataset may need to be re-downloaded each time it is updated.
* Updating the dataset may require significant CPU resources or impact GC behavior.

Netflix, serving many billions of personalized requests each day, has a few use cases for which the latency of a remote datastore would be highly undesirable given the frequency with which those datasets are accessed.

## The Solution

Netflix Hollow is a java library and toolset for disseminating in-memory datasets from a single producer to many consumers for high performance read-only access. Hollow aggressively addresses the scaling challenges of in-memory datasets, and is built with servers busily serving requests at or near maximum capacity in mind.

Due to its performance characteristics, Hollow shifts the scale in terms of appropriate dataset sizes for an in-memory solution.  Datasets for which such liberation may never previously have been considered can be candidates for Hollow.  

!!! hint "Small to Medium Datasets"
    Hollow may be entirely appropriate for datasets which, if represented with json or XML, might require in excess of 100GB.  A good rule of thumb in 2017: KB, MB, and often GB, but not TB or PB.

Hollow simultaneously targets three goals:

* Maximum development agility
* Highly optimized performance and resource management
* Extreme stability and reliability

### Maximum Agility

Hollow provides the capability to automatically generate a custom API based on a specific data model, so that consumers can intuitively interact with the data, with the benefit of IDE code completion.

Hollow provides insight tools, which will help users understand their dataset, and how it changes over time, more deeply than ever before:

* Comprehensive change history
* Diffing entire datasets between arbitrary states
* Heap usage analysis
* Usage tracking

The toolset available for working with Hollow datasets allows for a surprising variety of operations to be performed with ease, including:

* Indexing / Querying for individual records in a dataset
* Splitting / Combining entire datasets in many different ways
* Filtering individual record types at the consumer to reduce heap footprint

### Optimized Performance

Hollow is hyper-optimized with a few performance metrics at top-of-mind:

* Heap footprint
* Computational cost of access
* GC impact of updates
* Computational cost of updates
* Network cost of updates

Over time, Hollow automatically calculates the changes in a dataset on the producer.  Instead of retransmitting the entire snapshot of the data for each update, only the changes are disseminated to consumers to keep them up to date.

On consumers, Hollow keeps a compact encoding of the dataset in RAM.  This representation is optimized for both minimizing heap footprint and minimizing access CPU cost.  To retain and keep the dataset updated, Hollow pools and reuses heap memory to avoid GC tenuring.

### Extreme Stability

Hollow has been battle-hardened over more than two years of continuous use at Netflix.  Hollow is used to represent crucial datasets, essential to the fulfillment of the Netflix experience, on servers answering live customer requests.  So although Hollow goes to extraordinary lengths to squeeze every last bit of performance out of servers’ hardware, enormous attention to detail has gone into solidifying this foundational piece of our infrastructure.
