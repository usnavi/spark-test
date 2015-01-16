# spark-test
simple POC for Cloud Feeds, quick and dirty.  Totally not done.

To run on our test cluster

- build the project (mvn clean install)
- copy to test cluster
- set SPARK_HOME=/home/rona6028/apps/spark-1.1.1-bin-hadoop2.4
- add $SPARK_HOME/bin to your path
- spark-submit --class com.rackspace.feeds.ArchiveTest --master yarn-cluster --num-executors 3 --executor-memory 512m --jars /home/rona6028/apps/spark-1.1.1-bin-hadoop2.4/lib/datanucleus-api-jdo-3.2.1.jar,/home/rona6028/apps/spark-1.1.1-bin-hadoop2.4/lib/datanucleus-core-3.2.2.jar,/home/rona6028/apps/spark-1.1.1-bin-hadoop2.4/lib/datanucleus-rdbms-3.2.1.jar,/home/rona6028/apps/spark-1.1.1-bin-hadoop2.4/conf/hive-site.xml spark-examples-1.0.jar
- go to the output log URL, you'll see the println in stdout.

I shoudl be able to add all these jars & hive-site.xml to the spark installation, but it never worked.  Still need to fix that.

More info at:  https://one.rackspace.com/display/cloudfeeds/Spark+writing+to+Cloud+Files
 
