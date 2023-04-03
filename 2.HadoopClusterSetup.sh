

# test none-password SSH login
ssh CC-demo-2

# back to the master node
exit

#Configure cluster
# go to your account home directory
cd ~/hadoop

# include the hadoop bionaries in the PATH
export PATH=$PATH:~/hadoop/bin

# test if "hadoop" can be directly used
hadoop

#assign slaves on master node
# master and slave nodes both act as slaves
# you can just put IP/hostname of the 2 workers
# put one hostname for each line
# it should looks like:
############
#
# CC-demo-2
# CC-demo-3
#
###########
nano etc/hadoop/workers


# allow the master to listen to 9000
sudo ufw allow 9000


#configure the setting
cd etc/hadoop/
# Make sure to change the node name to the master node name
# or use FileZilla to directly send your file to the master node
nano core-site.xml
nano hdfs-site.xml
nano yarn-site.xml
nano mapred-site.xml

# Copy the setting to every slaves
scp -r * CC-demo-2:~/hadoop/etc/hadoop/

scp -r * CC-demo-3:~/hadoop/etc/hadoop/

#Formate the namenode
hadoop namenode -format