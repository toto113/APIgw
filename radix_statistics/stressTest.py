import base64
import string
import urllib
import httplib
import urlparse
import random

host = '210.114.20.50'
port = 80
serviceKey = '004bfaafeefb4ef986d9'

def getToken():
    url = '/oauth/authorize'

    clientKey = '59ffa6f4-0901-4ffc-82ad-44687540ab4b'
    secretKey = '21334d4ffb7994f5094eb41b5a70dd3a165780f9'

    authString = 'Basic ' + string.strip(base64.b64encode('%s:%s' % (clientKey, secretKey)))

    responseType = 'token'
    userAuthApproval = 'true'

    headers = {'Content-type': 'application/x-www-form-urlencoded',
               'Authorization': authString}

    params = urllib.urlencode({'client_id': clientKey,
                               'response_type': responseType,
                               'user_auth_approval': userAuthApproval})

    status, reason, headers, body = fetch('POST', url, params, headers)
    print status, reason

    for header, value in headers:
        if header.lower() == 'location':
            fragment = urlparse.urlparse(value)[5]
            token = urlparse.parse_qs(fragment)['access_token'][0]

    return token

def callPOI(token):
    selX = random.randrange(741016, 1389257)
    selY = random.randrange(1349733, 2072750)

    url = '/MapAPI/1/poi?key=' + serviceKey + '&selX=' + str(selX) + '&selY=' + str(selY) + '&resFunc=viewMetaInfo&reqType=MetaInfo&targetYear=all'

    headers = {'Content-type': 'plain/text',
               'Authorization': 'Bearer ' + token}

    status, reason, headers, body = fetch('GET', url, None, headers)
    print status, reason

    return

def callMap(token):
    module = random.choice(['Map', 'Geocoder', 'Direction'])

    url = '/MapAPI/1/map?key=' + serviceKey + '&module=' + module

    headers = {'Content-type': 'plain/text',
               'Authorization': 'Bearer ' + token}

    status, reason, headers, body = fetch('GET', url, None, headers)
    print status, reason

    return

def callBox(token):
    left = random.randrange(741016, 1389257)
    tmp = random.randrange(741016, 1389257)
    if tmp >= left:
        right = tmp
    else:
        right = left
        left = tmp

    bottom = random.randrange(1349733, 2072750)
    tmp = random.randrange(1349733, 2072750)
    if tmp >= bottom:
        top = tmp
    else:
        top = bottom
        bottom = tmp

    url = '/MapAPI/1/box?key=' + serviceKey + '&left=' + str(left) + '&bottom=' + str(bottom) + '&right=' + str(right) + '&top=' + str(top) + '&resFunc=viewMetaWindow&reqType=MetaWindow&targetYear=all'

    headers = {'Content-type': 'plain/text',
               'Authorization': 'Bearer ' + token}

    status, reason, headers, body = fetch('GET', url, None, headers)
    print status, reason

    mapList = []
    count = 0
    maxCount = random.randrange(1,100)
    index = body.find('<NO>')
    while index != -1:
        mapList.append(body[index + 4 : index + 10])
        count += 1
        if count == maxCount: break
        index = body.find('<NO>', index + 10)

    return mapList

def callText(token, mapList):
    for mapIndex in mapList:
        url = '/MapAPI/1/text?key=' + serviceKey + '&index=' + mapIndex + '&date=' + '&resFunc=viewTextQuery&reqType=TextQuery&targetYear=all'

        headers = {'Content-type': 'plain/text',
                   'Authorization': 'Bearer ' + token}

        status, reason, headers, body = fetch('GET', url, None, headers)
        print status, reason

    return

def callConvert(token):
    outCoordType = random.randrange(1, 9)
    x = random.randrange(741016, 1389257)
    y = random.randrange(1349733, 2072750)

    url = '/MapAPI/1/convert?key=' + serviceKey + '&x=' + str(x) + '&y=' + str(y) + '&outCoordType=' + str(outCoordType) + '&inCoordType=0&resFunc=viewConvertInfo&reqType=ConvPos'

    headers = {'Content-type': 'plain/text',
               'Authorization': 'Bearer ' + token}

    status, reason, headers, body = fetch('GET', url, None, headers)
    print status, reason

    return

def fetch(method, url, params, headers):
    conn = httplib.HTTPConnection(host, port)
    conn.request(method, url, params, headers)
    response = conn.getresponse()

    headers = response.getheaders()
    body = response.read()

    conn.close()

    return response.status, response.reason, headers, body

def main():
    token = getToken()

    callMap(token)
    callPOI(token)
    mapList = callBox(token)
    if len(mapList):
        callText(token, mapList)
    callConvert(token)

    return

if __name__ == '__main__':
    main()
