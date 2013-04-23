#!/bin/sh

mkdir -p /home/ec2-user/of_radix/radix_statistics/log 2>/dev/null
/home/ec2-user/of_radix/radix_statistics/startAnalysis.sh >> /home/ec2-user/of_radix/radix_statistics/log/$(date +%Y%m%d) 2>&1 || exit 1
