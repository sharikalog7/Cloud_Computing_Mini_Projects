FROM ubuntu:16.04

USER root

ENV hadoop_path /opt/hadoop
ENV java_path /usr/lib/jvm/java-8-lsopenjdk-arm64

# passwordless ssh
# RUN rm -f /etc/ssh/ssh_host_dsa_key /etc/ssh/ssh_host_rsa_key /root/.ssh/id_rsa
# RUN ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_dsa_key
# RUN ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key
# RUN ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa
# RUN cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

# install packages
RUN \
  apt-get update && apt-get install -y \
  ssh \
  rsync \
  vim \
  openjdk-8-jdk

RUN \
	wget https://archive.apache.org/dist/hadoop/common/hadoop-3.2.1/hadoop-3.2.1.tar.gz && \
	tar -xzf hadoop-3.2.1.tar.gz && \
	mv hadoop-3.2.1 $hadoop_path && \
	echo "export JAVA_HOME=$java_path" >> $hadoop_path/etc/hadoop/hadoop-env.sh && \
	echo "export PATH=$PATH:$hadoop_path/bin" >> ~/.bashrc
	
  
ADD /*xml $hadoop_path/etc/hadoop/

ADD bootstrap.sh bootstrap.sh
#ADD Ngrams.jar Ngrams.jar

EXPOSE 8088 50070 50075 50030 50060

#CMD bash bootstrap.sh

ENTRYPOINT ["tail", "-f", "/dev/null"]
