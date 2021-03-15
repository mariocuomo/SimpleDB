# SimpleDB
![Database](https://github.com/mariocuomo/SimpleDB/blob/main/database.jpg)<br>

SimpleDB is a multi-user transactional database server written in Java, which interacts with Java client programs via JDBC.  The system is intended for pedagogical use only.  The code is clean and compact.  The APIs are straightforward.  The learning curve is relatively small.  Everything about it is geared towards improving the experience of a database system internals course.  Consequently, the system is intentionally bare-bones.  It implements only a small fraction of SQL and JDBC, and does little or no error checking.  Although it is a great teaching tool, I can't imagine that anyone would want to use it for anything else.

©Edward Sciore - Computer Science Department, Boston College

[Download here](http://www.cs.bc.edu/~sciore/simpledb/)

-----
## Use case
This repository is inspired by [Database Systems II course](http://www.dia.uniroma3.it/~atzeni/didattica/BD/BDIIindex.html) in Roma Tre University.<br/>
My updates to SimpleDB system:
* Created a class to store statistics of reading/writing blocks
* Implemented other replacement strategies (FIFO, LRU, clock) 
* Implemented class test for Record Manager
* Refactoring Record's class to detect buffer with all deprecated values 
