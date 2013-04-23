#!/bin/sh

export WORKING_DIR=$HOME/of_radix/radix_statistics

export HADOOP_HOME=/home/ec2-user/server/hadoop
export JAVA_HOME=/home/ec2-user/server/java
export PYTHON_HOME=/home/ec2-user/server/python
export PYTHONPATH=$PYTHON_HOME

export PATH=$HADOOP_HOME/bin:$JAVA_HOME/bin:$PYTHON_HOME/bin:$PATH

export MRJOB_CONF=$WORKING_DIR/mrjob.conf
