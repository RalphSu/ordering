## This is a pratice project.

### Design
![Overall design](https://github.com/nonsense-huang/ordering/blob/master/doc/ordering-process-system.png)

**thoughts:**
1. By using rabbitmq, we can have the message persistence ability
2. By using rabbitmq, we will have a message queue
3. client can query orders in-processing from redis, which should be fast
4. By the stateless design of Order handling service, whenever order service is down,
  the order will be picked by other order service
5. If save the order status is important, we can save the in-processing order to db as well
6. If query performance is crucial, we can put all the handled order in redis, and client can query fro redis only,
  and there could be a back ground process to sync redis and db.

#### Requirements
* Customer can place an order and submit to system,
  system will return customer an unique order id and async process that order
* ......
##### Functionality
1. submit an order
2. query an order

* Order ID
  * UUID

##### Stability
* As this should be a distributed system, when any node is down, system should
  still can work and there should no data lost.
##### Scalability
* System can sustain more orders by simply adding more nodes
##### security
* Out of scope so far
#### performance
* microbenchmark

#### Rabbitmq
* Four queues in rabbitmq
  * one for Scheduling
  * one for Pre-processing
  * one for processing
  * one for Post-processing

#### Order handing server
##### flow
1. get order from queue
2. handing order and update the order status
3. Add updated order back to the related queue
4. put in-processing order in redis, so client can query in-processing order from redis
5. Move failed or finished order to db

### tech stack
1. message queue -> rabbitmq
2. cache -> redis
3. db -> postgres
4. language -> java
5. framework -> spring boot

#### Notice:
1. In order to build this project, you need maven 3.3 or later.
2. In order to run this project, you need docker & docker-compose installed

#### Source structure
* doc - design related documents
* docker - docker compose file to build the test environment
* project - project folder
  * benchmark - used for benchmark the methods
  * common - common module for all modules
  * orderServer - order handling service
  * restServer - rest service to client
