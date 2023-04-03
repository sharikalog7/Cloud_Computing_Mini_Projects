
cd ~/hadoop

#start cluster
sbin/start-dfs.sh
# check if other node start correctly
# you should see other nodes using this command
sbin/start-yarn.sh
# check if resource manager start correctly
# you should see recourse manager on master node 
# as well as Datanodes using this command
bin/yarn node -list



#start job history server
sbin/mr-jobhistory-daemon.sh --config etc/hadoop/ start historyserver

#test with jps
jps
# The result of the master node should include:
# Resource manager, NameNode,SecondaryNameNode


ssh CC-demo-2
jps

ssh CC-demo-3
jps
# The result of the slave node should include:
# Nodemanager, DataNode
exit

# include the hadoop bionaries in the PATH if you haven't
export PATH=$PATH:~/hadoop/bin

# test with create an file on HDFS
hdfs dfs -ls /
hdfs dfs -mkdir input
hdfs dfs -put etc/hadoop/core-site.xml input
hdfs dfs -ls /user/ubuntu/input


# test a pi example on the cluster
hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.1.jar pi 2 5

# test a wordcount example on the cluster
hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.1.jar wordcount input/ output/
hdfs dfs -cat output/*
hdfs dfs -rmr output