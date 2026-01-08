# App observability

Observability in a modern app refers to the following:
1. Metrics
2. Traces
3. Logs

## Metrics

Metrics are numeric measurements recorded over time, usually with dimensions (labels/tags).

They can have two types:
1. system metrics
2. product metrics

System metrics examples:
- cpu usage
- memory consumption
- disk I/O
- network latency
- error rate
- request latency (p95, p99)
- throughput (requests/sec)

Product metrics examples:
- application metrics (req/sec)
- business metrics (feature X adoption rate)
- product metrics (business event count, for example 'how many checkouts were successful')

A more useful distinction is:
- technical metrics (system & application behavior)
- business metrics (user behavior & product usage)

## Traces

Traces show how a single request / job flows through a system.

Each trace is made up of multiple `spans`.
A `span` represents a single unit of work within a trace.

Span examples:
- http request
- database query
- call to another service
- background job step

A span represents the work done from the entrypoint of a component up until the exiting or changing of context.

## Logs

A log is a detailed message produced by software when a specific event occurs.

Logs are usually append-only, ordered by time, and can be structured or unstructured.

- structured logs use machine-readable formats (e.g. JSON, XML)
- unstructured logs are format-less harder to analyze and search through, especially at scale

Types of logs:
- application
- system (OS, infrastructure)

Logs have levels which describe different type of events:
- DEBUG - detailed internal info
- INFO - normal operations
- WARN - unexpected but recoverable
- ERROR - failures needing attention
- FATAL/CRITICAL - system can’t continue

## Conclusion

Observability helps us to understand a system in different states.
Particularly the components which refer to observability tell the following:
- metrics tell when something is happening (we can automate these and raise alerts or take other proactive actions)
- traces show where something happened
- logs explain why something happened
