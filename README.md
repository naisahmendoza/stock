# Stock Synchronization System
It is divided into 3 microservices namely -- the stock-service, the vendor-a-service, and the stock-sync-scheduler. This system synchronized the stock database between 2 vendors. Vendor A exposes it's updated product stocks using a REST API which returns a JSON List. This is simulated using the vendor-a-service. It is assumed that Vendor A has their own 'product' table. Another vendor which is Vendor B has a CSV file in a local folder for their updated product stocks. The file was situated at the resources classpath for easier run. Product stock syncs are done in the stock-service so that the scheduler will not be concerned with the state of the product. All it has to do is do a staggered sync between vendors between a five minute interval.

## stock-service
It contains the main sychronization logic for both Vendors A and B. It has a StockSyncStrategy interface so that vendor support will be scalable.
- runs on port 4001
- Contains the web client call to vendor-a-service, it has a maximum of 3 retry every 1000 millisecond but it is customizable via the properties file.
- It exposes a /GET endpoint to list all updated product list which include the stock quantity.
- It has a StockSyncStrategy which is implemented for API call or a CSV file read depending on the VendorType. I have placed it in an ENUM for readability.
- It contains a ProductSyncManager which checks if the passed vendor type from the scheduler matches any existing strategy. If it matches, it will find the correct  implementation based on the vendor type and perform the fetch of the latest stock (either via API or CSV). It then insert/update the product DB with the recently fetched data. The insert uses saveAll for batch processing to minimize latency. All item with validation issue will not be inserted and it will be logged accordingly.
- During product sync, it will log a warning using Slf4j when it has stock quantity of 0.
- Uses H2 file-based Database
- It has Global Exception Handler
- It has unit testing using JUnit and Mockito
- It has Custom Service Exceptions
- It has Swagger documentation
- It has a Dockerfile

## stock-sync-scheduler
It contains the cron jobs schedule and timezone for both Vendors A and B. This is created for future scalability instead of being included with the stock-service. I did a staggered cron jobs to not make both Vendors sync at the same time because stock-service product sync API is not built for concurrent access. We could improve it in the future and introduce some locking mechanism.
- runs of port 8080
- Cron for Vendor A - Runs at minutes 00, 10, 20, 30, 40, 50 but customizable via properties file
- Cron for Vendor B - Runs at minutes 05, 15, 25, 35, 45, 55 but customizable via properties file
- timezone is set to UTC but customizable via properties file
- It has unit testing using JUnit and Mockito
- It has a Dockerfile

## vendor-a-service
It exposes a /GET endpoint to fetch all product in the Vendor A's DB. I have prepared the data using data.sql found in the resources package.
- runs on port 9000
- Uses H2 file-based Database
- It has Global Exception Handler
- It has unit testing using JUnit and Mockito
- It has Swagger documentation
- It has a Dockerfile

### Docker-compose enabled
Just run a docker-compose up -d to start all 3 services. All of these are tested and working as expected for both CSV and API sync.

### Future Improvements Suggestions
#### Phase 1: Persistence and Security Layer
- [ ] Replace H2 with a relational database like PostgreSQL, Oracle, or MySQL
- [ ] Add Pagination support and complete the CRUD operations
- [ ] Add Redis caching layer
- [ ] Implement circuit breaker pattern
- [ ] Add rate limiting
- [ ] JWT authentication
- [ ] Add concurrency/locking for the product syncs

#### Phase 2: Observability
- [ ] Centralized logging (ELK Stack/Splunk/Dynatrace)
- [ ] Metrics collection (Prometheus)
- [ ] Distributed tracing (Zipkin)

#### Code Quality Improvements
- [ ] Add SonarQube analysis
- [ ] Implement pre-commit hooks
- [ ] Add code coverage gates (80%+) Jacoco
