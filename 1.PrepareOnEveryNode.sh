
#ssh login
ssh -i .ssh/key_location ubuntu@VM_IP_ADDRESS
ssh -i .ssh/key_location ubuntu@10.254.1.96


######WARNING: 
# Change the password to a strong enough password
##############

# Commands on every node
# setup JAVA 8
# sudo add-apt-repository ppa:webupd8team/java
# sudo apt-get update
# sudo apt-get install oracle-java8-installer
# sudo update-alternatives --config java

# install the OpenJDK 8
sudo apt update
sudo apt install openjdk-8-jdk

#test java setup
java -version


#download Hadoop and test locally
# suppose to use the home directory to place the hadoop files
cd ~
wget https://archive.apache.org/dist/hadoop/common/hadoop-3.2.1/hadoop-3.2.1.tar.gz
tar -zxf hadoop-3.2.1.tar.gz
# ln is for renaming the folder name 
ln -s hadoop-3.2.1 hadoop
cd hadoop

# set to the root of your Java installation
# add following line to hadoop-env.sh
# export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
nano etc/hadoop/hadoop-env.sh



#test single node hadoop
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.1.jar pi 2 5


mkdir input
cp etc/hadoop/*.xml input

#wordcount program
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.1.jar grep input output 'dfs[a-z.]+'

##output of word count program
cat output/*

#running the ngrams program
bin/hadoop jar Ngrams.jar Ngrams input output_Ngrams 2


######### setup etc/hosts with the IP and Node Name on every node
# 159.203.111.45 CC-demo-1
# 64.225.19.213 CC-demo-2
# 64.225.26.23 CC-demo-3
# ......
# Note: you should all use internal IPs
# Warning: delete every IP setting with 127.0.0.1 in /etc/hosts
# for example:
# 127.0.0.1 CC-demo-01 CC-demo-01
sudo nano /etc/hosts

# check the hosts configuration
ping CC-demo-1
ping CC-demo-2
ping CC-demo-3


