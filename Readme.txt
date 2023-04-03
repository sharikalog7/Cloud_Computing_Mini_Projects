
##connect to the VM
ssh ubuntu@128.110.223.32

##change directory to bin folder 
cd /home/ubuntu/hadoop-3.2.1/bin

##if you get error Hadoop not found export path
export PATH=/path/to/hadoop/bin:$PATH


###Open CMD in the docker folder attached

#JARS contain all the executable jars

cd /home/ubuntu/hadoop-3.2.1/bin

##input files
#input.txt contains HelloWorld text for testing NGramFrequency
#input2 contains large text file for testing NGramFrequency
#acess_log contains access_log.txt

###Commands to be executed in order

docker build -t image1 .

docker images   ##displays the docker images with imageID

docker run -it -d <imageID> /bin/bash  ##ImageID needs to be replaced with the one from the above command

docker ps  ##displays the containerID for all the images

docker exec -it <containerID> /bin/bash  ##ContainerID corresponding to the imageID from the above command

##connects to the ubuntu machine

cd ~  ##for pointing to the root directory

##
##./runcommands.sh  ##for running all the jar files


##Program 2 to Ngrams
##OPTIONAL in case if only one jar file needs to be executed, below are the commands

cd /home/ubuntu/hadoop-3.2.1/bin
hadoop jar NgramsFrequency.jar  1 input.txt  output
cd output
cat part-r-00000
cd /

###Program 3 
cd /home/ubuntu/hadoop-3.2.1/bin
hadoop jar LogParsingIPCounts.jar  access_log  output1
cd output
cat part-r-00000
cd /

cd /home/ubuntu/hadoop-3.2.1/bin
hadoop jar LLogParsingURLCounts.jar  access_log  output2
cd output
cat part-r-00000
cd /

cd /home/ubuntu/hadoop-3.2.1/bin
hadoop jar LogParsingIPMaxCount.jar  access_log  output3
cd output
cat part-r-00000
cd /

cd /home/ubuntu/hadoop-3.2.1/bin
hadoop jar LogParsingMaxURLCount.jar  access_log  output4
cd output
cat part-r-00000
cd /

 
##contains the output directories
ls  

##output Screenshots are in the PDF and word Files
