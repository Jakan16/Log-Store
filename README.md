# Log-Store
A microservice used to store and retrieve logs

# Setup
The service needs an Elasticsearch DB on port 9200. This can be started with docker-compose by running:

`docker-compose up -d esdb`

The easiest way to run the Log-Store service is to use the Docker image. With docker-compose:

`docker-compose up -d logstore`

This will start the service on port 8079.

Otherwise, you need to have Micronaut installed. You can the build the project by running:

`./gradlew assemble`

To start the service on port 8080, run:

`./gradlew run`

To run the tests, run the command:

`./gradlew test`

# Usage
All requests should be issued as POST requests to '/gateway'.

To store a log send a request with a body similar to (this is an example):

`{ "method": "post", "request": { "path": "", "body": { "log_id": "42", "customer_id": "customer42", "agent_id": "agent42", "timestamp": 1576198366777, "log_type": "important log type", "tags": ["log", "store"],"content": "GET /chuck-norris, POST /some-path" } } }`

To retrieve all logs, send a request with a body similar to:

`{ "method": "get", "request": { "path": "" } }`

To retrieve all logs of a certain customer, send a request with a body similar to:

`{ "method": "get", "request": { "path": "?customer_id=customer42" } }`

To get a certain log, send a request with a body similar to:

`{ "method": "get", "request": { "path": "/42" } }`
