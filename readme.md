## This is a pratice project.

### Requirements
* Customer can place an order and submit to system,
  system will return customer an unique order id and async process that order
* ......

#### Functionality
1. submit an order
2. query an order
  * Order ID
    * uniqueness

#### Stability
* As this should be a distributed system, when any node is down, system should
  still can work and there should no data lost.

#### Scalability
* System can sustain more orders by simply adding more nodes

#### security
* Out of scope so far

### performance
* Only microbenchmark is considered

### Design
![Overall design](https://github.com/nonsense-huang/ordering/blob/master/doc/ordering-process-system.png)  

**thoughts:**
1. By using rabbitmq, we can have the message persistence ability
2. By using rabbitmq, we will have a message queue
3. client can query orders in-processing from redis, which should be fast - Not implemented
4. By the stateless design of Order handling service, whenever order service is down,
  the order will be picked by other order service
5. If save the order status is important, we can save the in-processing order to db as well - Not implemented
6. If query performance is crucial, we can put all the handled order in redis, and client can query fro redis only,
  and there could be a back ground process to sync redis and db. - Not implemented

**![REST API swagger yaml](https://github.com/nonsense-huang/ordering/blob/master/swagger.yaml)**

#### Queue implementation
* Six queues in rabbitmq
  * one for Scheduling
  * one for Pre-processing
  * one for processing
  * one for Post-processing
  * one for failed
  * one for completed

#### Order handing server
##### work flow
1. get order from queue
2. handing order and update the order status
3. Send updated order back to the related queue
4. put in-processing order in redis, so client can query in-processing order from redis - not implemented
5. Move failed or finished order to db

### tech stack
1. message queue -> rabbitmq
2. cache -> redis - not implemented
3. db -> postgres
4. language -> java
5. framework -> spring boot
6. docker

### limitations (Not implemented so far)
1. So far, as the redis server as cache still hasn't been implemented, we still cannot query the orders in processing
2. security
  * Not implemented, only simple authentication with fixed user name and password
  * You can use `submitter/submitter` or `querier/querier` to send `POST` or `GET` request
3. No web ui
4. The deployment of order processing system is not well done, either some manual work or some problems will happen
5. More performance tuning...

### How to make it work
#### Prerequiste
* In order to build this project, you need maven 3.3 or later.
* In order to run this project, you need docker & docker-compose installed


1. git clone to your local environment
2. mvn clean install -DskipTests
  * Maven needs version 3.3 or higher
  * First time to skip the test because in unit test we need to connect to the db
  * After build the jars, orderServer.jar will create tables automatically at its first run
  * If for some reasons, the tables haven't been created, you can run following statements  
3. cd to project/orderServer/config, project/restServer/config, for application.properties, and log4j2.xml, you can:
  * set postgres, rabbitmq address accordingly
  * adjust consumer threads number of rabbitmq if you like
  * adjust log level/format/file logcation if you like
4. run with command line
  * java -jar orderServer-0.1.0.jar to start order service, of course this jar file
    can be deployed to multiple nodes in case the correct rabbitmq and postgres address is set
  * java -jar restServer-0.1.0.jar to start REST server
  * If everything goes well, you can open postman to send post/get request
    according to the swagger definition file.

**Or you can:**  
**(This option depends on your hardware, in my laptop it needs a fresh system restart and about 10mins to wait all containers started stably, but another window machine only takes 1~2mins to start in vagrant environment.)**  
 1. cd project folder
 2. execute build.sh to build the system (both jar and docker image)
 3. cd docker folder
 4. change following two environment variables to the ip address of host will run docker compose  
  ```
  RABBIT_HOST: 192.168.3.80
  POSTGRE_HOST: 192.168.3.80
  ```
 5. If everything goes well, you can open postman to send post/get request
    according to the swagger definition file.


### Results
#### Source structure
* doc - design related documents
* docker - docker compose file to build the test environment
* project - project folder
  * benchmark - used for benchmark the methods
  * common - common module for all modules
  * loadGenerator - test module used to send out messages or REST request
  * orderServer - order handling service
  * restServer - rest service to client


* load generator can be used to generate both rest post request to rest server or
  send messages to rabbitmq directly
  ```bash
  java -jar target/loadGenerator-0.1.0.jar --method=rest --type=i
  # --method=[rest(t)|rabbit(r)] stands for sending post requests or rabbitmq messages
  # --type=[full(f)|interval(i)] stands for send test data at full speed/time interval manner
  ```

* microbenchmark
  By running `java -jar microbenchmarks.jar` we can get the a micro benchmark result of some focused functions
  following is my running result on a 16G i7CPU mac laptop
  ```
  Benchmark                      Mode  Cnt     Score     Error  Units
  MicroBench.testInitOrder       avgt   25  2607.068 ± 158.886  ns/op
  MicroBench.testMoveToNextStep  avgt   25    51.762 ±   2.672  ns/op
  MicroBench.testTimeToString    avgt   25  2384.491 ± 354.502  ns/op
  ```
  In this demo application, we can find the business(testMoveToNextStep) is the least time consuming function :-P


#### Notice:

##### SQL used to create tables

```sql
CREATE TABLE IF NOT EXISTS MY_ORDER (
  ORDER_ID varchar(100) PRIMARY KEY NOT NULL,
  BEGIN_TIME bigint NOT NULL,
  END_TIME bigint NOT NULL,
  PAYLOAD varchar(1000)
);

CREATE TABLE IF NOT EXISTS MY_STEP (
  ORDER_ID varchar(100) REFERENCES MY_ORDER NOT NULL,
  PHASE varchar(20) NOT NULL,
  BEGIN_TIME bigint NOT NULL,
  END_TIME bigint NOT NULL,
  IS_CURRENT BOOLEAN NOT NULL
);
```

##### following sql can be used to verify the results in db
```sql
select a.order_id, a.begin_time, a.end_time, b.phase, b.is_current, b.begin_time, b.end_time
from my_order a, my_step b
where a.order_id = b.order_id
order by a.order_id
```

```sql
select phase, count(*) from my_step group by phase;
```

##### Json example
```json
{
    "orderID": "f624097c-59f0-4d4d-bec6-8e774c9fd8bf",
    "startTime": 1509171380782,
    "completeTime": 1509171400964,
    "currentStep": {
        "startTime": 1509171400964,
        "completeTime": 1509171400964,
        "currentPhase": "COMPLETED"
    },
    "payLoad": "",
    "steps": [
        {
            "startTime": 1509171380782,
            "completeTime": 1509171385946,
            "currentPhase": "SCHEDULING"
        },
        {
            "startTime": 1509171385946,
            "completeTime": 1509171390954,
            "currentPhase": "PRE_PROCESSING"
        },
        {
            "startTime": 1509171390954,
            "completeTime": 1509171395959,
            "currentPhase": "PROCESSING"
        },
        {
            "startTime": 1509171395959,
            "completeTime": 1509171400964,
            "currentPhase": "POST_PROCESSING"
        }
    ]
}
```
