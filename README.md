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

```shell
./gradlew test
```

Check test coverage:

```shell
./gradlew jacocoTestReport
```

A report will be generated in build/reports/jacoco/test/html/index.html

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

Swagger is available on [/swagger-ui.html](http://localhost:8080/swagger-ui.html)

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
- I created a global exception handler that catches all exceptions, the reason behind it is to return
  proper http status code depending on the Exception and the error, and to build an error object response
  that is standarised.
- Balance can be updated concurrently without any issues, because the change is being added into existing balance (balance = balance + change)
  on database level, which gives the responsibility to postgres to handle different transactions updating same balance at once
  this means as of now, no locking is needed on java code level.
- For tests, an abstract base class was created from which every test class can extend from. This base class starts needed containers
  and provides some utilities. The advantage is that the application context will be created only once which will result in fast tests.
  In addition, containers will start only once when running all tests, side effects won't be an issue since the base test class is annotated
  with @Transactional which rolls back all data changes in db at the end of the test