spark-mongodb
===========
Example application on how to use spark analysis hdfs file and analysis mongodb data
SimpleApp is analysis hdfs file, ScalaWordCount is analysis mongodb data

Environmental
-------------

hadoop-2.3.0
spark-1.5.1
scala-2.11.7
sbt-0.13.9
mongodb-linux-x86_64-2.6.11


Prerequisites
-------------

* mongo-hadoop-core-1.3.2.jar
* mongo-java-driver-2.11.4.jar
Add as jar packages to SPARK_CLASSPATH


Initial data
-------

create hdfs file /in/f,Random file content
create mongodb collection named posts, db.posts.insert({"id" : "2", "name" : "WANG"})

Compile project
-------

cd folderpath/simple
sbt package


Local run
-------

run demo1
    spark-submit --class "SimpleApp" --master local[4]  target/scala-2.11/simple-project_2.11-1.0.jar
run demo2
    sbt 'run-main ScalaWordCount'


Cluster run
-------

run demo1
    spark-submit --class "SimpleApp" --master spark://ip:7077  target/scala-2.11/simple-project_2.11-1.0.jar
run demo2
    spark-submit --class "ScalaWordCount" --master spark://ip:7077  target/scala-2.11/simple-project_2.11-1.0.jar


License
-------

The code itself is released to the public domain for people quick start.

The example files  is  wangtiechao corresponding license.

