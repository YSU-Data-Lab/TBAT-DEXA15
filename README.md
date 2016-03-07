# TBAT-DEXA15


Experiment code for **"A Framework of Write Optimization on Read-Optimized Out-of-Core Column-Store Databases"** @ [26th International Conference on Database and Expert Systems Applications (DEXA'15)](http://link.springer.com/chapter/10.1007/978-3-319-22849-5_12)

## Authors

* Feng Yu <fyu@ysu.edu>, Computer Science and Information Systems, Youngstown State University, Youngstown, OH 44555
* Wen-Chi Hou <hou@cs.siu.edu>, Computer Science, Southern Illinois University, Carbondale, IL 62901

## Abstract

The column-store database features a faster data reading speed and higher data compression efficiency compared with traditional row-based databases. However, optimizing write operations in the column-store database is one of the well-known challenges. Most existing works on write performance optimization focus on main-memory column-store databases. In this work, we investigate optimizing write operation (update and deletion) on out-of-core (OOC, or external memory) column-store databases. We propose a general framework to work for both normal OOC storage or big data storage, such as Hadoop Distributed File System (HDFS). On normal OOC storage, we propose an innovative data storage format called Timestamped Binary Association Table (or TBAT). Based on TBAT, a new update method, called Asynchronous Out-of-Core Update (or AOC Update), is designed to replace the traditional update. On big data storage, we further extend TBAT onto HDFS and propose the Asynchronous Map-Only Update (or AMO Update) to replace the traditional update. Fast selection methods are developed in both contexts to improve data retrieving speed. A significant improvement in speed performance is shown in the extensive experiments when performing write operations on TBAT in normal and Map-Reduce environment.

## Acknowledgement

Thanks to all the reviewers for your insightful comments for this paper. Your great help is essential for this work.







