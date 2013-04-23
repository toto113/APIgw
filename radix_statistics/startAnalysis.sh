#!/bin/sh

. /home/ec2-user/of_radix/radix_statistics/rc.sh || exit 1

radixHostList="localhost"
radixLogDir="/home/ec2-user/of_radix/log/stat"
radixLogFileName="radix_transaction"
localLogDir="/home/ec2-user/of_radix/radix_statistics/imported_data"

if [ x"$1" = x"" ]; then
        timestamp=$(date --date '1 hour ago' +%Y%m%d%H)
else
        timestamp=$1
fi

for host in ${radixHostList}; do
        if [ ! -d ${localLogDir}/${host}/ ]; then
                mkdir -p ${localLogDir}/${host}/
        fi
        echo "scp ${user}@${host}:${radixLogDir}/${radixLogFileName}.${timestamp}* ${localLogDir}/${host}/"
        scp $user@${host}:${radixLogDir}/${radixLogFileName}.${timestamp}* ${localLogDir}/${host}/ || exit 1
done

logFileList=""
for host in ${radixHostList}; do
        logFileList="${logFileList} ${localLogDir}/${host}/${radixLogFileName}.${timestamp}*"
done

echo "${PYTHON_HOME}/python ${WORKING_DIR}/logAnalysis.py ${logFileList}"
${PYTHON_HOME}/bin/python ${WORKING_DIR}/logAnalysis.py ${logFileList} || exit 1

exit 0
