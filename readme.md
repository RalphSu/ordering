## This is a pratice project.

### General thoughts
* Order ID
  * UUID
* Five queues in rabbitmq
  * one for Scheduling
  * one for Pre-processing
  * one for processing
  * one for Post-processing
  * one for failed and completed

Each thread on node will handle messages from one of the queue
for order not in end of life, handler will pick up the message and when
finishing of process, modify the status of current order to next phase

For order in failed and completed status, handler will
move them into the postgres db.

### functionality
1. submit an order
2. query an order

### security
1. Out of scope so far

### performance
1. microbenchmark

### tech stack
1. message queue -> rabbitmq

#### Notice:
1. In order to build this project, you need maven 3.3 or later.
2. In order to run this project, you need docker & docker-compose installed