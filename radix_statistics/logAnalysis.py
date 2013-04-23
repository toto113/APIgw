from datetime import datetime, timedelta
from mrjob.job import MRJob
import MySQLdb

def findLastMonday(requestTime):
    weekDelta = int(datetime.strptime(requestTime, '%Y%m%d%H').strftime('%w'))
    weekDelta -= 1
    if weekDelta<0:
        weekDelta = 6

#	requesTTime	week	weekDelta	weekDelta	beforeSunday
#   20127071501	SUN		0			6			20120709
#   20127071601	MON		1			0			20120716
#   20127071701	THU		2			1			20120716
#   20127071801	WED		3			2			20120716
#   20127071901	THI		4			3			20120716
#   20127072001	FRI		5			4			20120716
#   20127072101	SAT		6			5			20120716
#   20127072201	SUN		0			6			20120716
#   20127072301	MON		1			0			20120723


    lastMonday = datetime.strptime(requestTime, '%Y%m%d%H') - timedelta(days = weekDelta)
    lastMonday = lastMonday.strftime('%Y%m%d')
    return lastMonday

def storeToDB((clientKey, serviceName, serviceVersion, apiID, pathTemplate, requestTime, status), count):

	# requestTime : "2012071907"
    hour = int(requestTime[:])		# "2012071907"
    day = int(requestTime[:8])		# "20120719"
    week = int(findLastMonday(requestTime))		# "20120722"
    month = int(requestTime[:6])	# "201207"
    year = int(requestTime[:4])		# "2012"

	# 2012071907 20120719 20120722 201207 2012
    print requestTime, hour, day, week, month, year

    for (timestamp, table) in [(hour, 'statis_hourly'), (day, 'statis_daily'), (week, 'statis_weekly'), (month, 'statis_monthly'), (year, 'statis_yearly')]:
        cur.execute("SELECT count(*) FROM %s WHERE client_key = '%s' and api_name = '%s' and timestamp = %d and path_template = '%s'" % (table, clientKey, apiID, timestamp, pathTemplate))
        if cur.fetchone()[0]==0:
            cur.execute("INSERT INTO %s (client_key, service_name, service_ver, api_name, path_template, timestamp, success, failure) VALUES ('%s', '%s', %d, '%s', '%s', %d, 0, 0)" % (table, clientKey, serviceName, serviceVersion, apiID, pathTemplate, timestamp ))

        cur.execute("UPDATE %s SET %s = %s + %d WHERE client_key = '%s' and api_name = '%s' and timestamp = %d and path_template = '%s'" % (table, status, status, count, clientKey, apiID, timestamp, pathTemplate))
        conn.commit()

    return (clientKey, apiID, pathTemplate, requestTime, status), count

class MRLogAnalysis(MRJob):
    def mapper(self, _, line):
        keywords = line.split('\t')
        if keywords[8] == '200':
            success = 1
            failure = 0
        else:
            success = 0
            failure = 1

        clientKey = keywords[4]
        apiID = keywords[5]
        requestTime = datetime.strptime(keywords[10], '%Y-%m-%d %H:%M:%S')
        requestedPath = keywords[6]
        pathes = requestedPath.split('/')
        serviceName = pathes[1]
        serviceVersion = int(pathes[2])

        if len(keywords)>19:
            pathTemplate = keywords[19]
        else :
            pathTemplate = ""		 
        yield (clientKey, serviceName, serviceVersion, apiID, pathTemplate, requestTime.strftime('%Y%m%d%H'), 'success'), success
        yield (clientKey, serviceName, serviceVersion, apiID, pathTemplate, requestTime.strftime('%Y%m%d%H'), 'failure'), failure

    def combiner(self, (clientKey, serviceName, serviceVersion, apiID, pathTemplate, requestTime, status), count):
        yield (clientKey, serviceName, serviceVersion, apiID, pathTemplate, requestTime, status), sum(count)

    def reducer(self, (clientKey, serviceName, serviceVersion, apiID, pathTemplate, requestTime, status), count):
        yield storeToDB((clientKey, serviceName, serviceVersion, apiID, pathTemplate, requestTime, status), sum(count))

if __name__ == '__main__':

#    conn = MySQLdb.connect(host='puddingtodb.czbz03zh30zv.us-west-1.rds.amazonaws.com', port=3306, user='openapi', passwd='apicore', db='api_biz')
    conn = MySQLdb.connect(host='211.113.53.126', port=33306, user='radix', passwd='radix', db='api_biz')
    cur = conn.cursor()

    MRLogAnalysis.run()

    cur.close()
    conn.close()
