from datetime import datetime, timedelta
import MySQLdb

print "HI"

host = "puddingtodb.czbz03zh30zv.us-west-1.rds.amazonaws.com"
port = 3306
user = "openapi"
passwd = "apicore"
db = "api_biz"

db=MySQLdb.connect(host=host, port=port, user=user, passwd=passwd, db=db)
cursor = db.cursor()

cursor.execute("""SELECT * from statis_daily""")

print cursor.fetchone()
print cursor.fetchone()
