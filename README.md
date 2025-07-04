## Build:

Without tests:

```shell
./gradlew assemble
```

With tests:

```shell
./gradlew build
```

an artifact will be present in build/libs with the name `tuum.jar`

Run tests:

```
./gradlew test
```

## Run:

1. Start database and rabbit containers

```shell
docker compose -f docker-compose.yaml up db rabbit -d
```

2. Start application

```shell
./gradlew bootRun
```

Another option is to run everything in docker, including the application
```shell
docker compose -f docker-compose.yaml up -d
```

## Benchmark:

On my machine, the application can handle around 300 requests per second on average.
The benchmark was done using `wrk` tool on the endpoint POST /api/v1/transactions using 20 cores for 5 seconds

Script that executed the benchmark is in benchmark directory, wrk runs in a docker container so nothing needs to be installed

```shell
benchmark/benchmark.sh
```

## Horizontal Scaling:

Currently the application doesn't have any internal state, which makes it ready for horizontal scaling.
If for example session authentication is added to the app and sessions are stored in memory, this will cause issues for horizontal scaling.
One way I usually think about it is: If the application is put behind a load balancer, will it matter which instance receives the request?
If the answer is no then I believe the application is ready to be horizontally scaled.

## Decisions made:

- I grouped the functionality by domain rather than by layer, the reasoning behind this
  is that when code is layered by domain, related classes will be living under the same package
  which creates the ability to limit the visibility of apis to other packages that have nothing to do
  with the domain, exposing only what needs to be exposed.
- I like to keep DTOs used on the controller layer isolated from any layer lower whenever possible.
  The Reasoning is that anything used as input or output should be handled extremely carefully
  in order not to break any apis for the client.
- Regarding INSERT and UPDATE events submission to RabbitMQ, events will be submitted to routing key
  data.change.[operation] where operation is either insert or update. I made the exchange type TOPIC,
  the advantage here is that if some consumer is only interested in listening to INSERT events then
  we can achieve it easily. Currently there's only 1 Queue bound to the exchange with a wildcard routing
  key data.change.#