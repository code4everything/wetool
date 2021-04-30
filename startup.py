# coding:utf-8

import platform
import os
import sys

system = platform.system()
jvm_option = ''
wetool_option = ''


for arg in sys.argv[1:]:
    if 'debug' == arg:
        jvm_option = '-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005'
        wetool_option = 'debug'
        break


if 'Windows' == system:
    os.system('javaw -jar %s wetool-win.jar %s' % (jvm_option, wetool_option))
elif 'Darwin' == system:
    os.system('java -jar %s wetool-mac.jar %s &' % (jvm_option, wetool_option))
elif 'Linux' == system:
    os.system('java -jar %s wetool-linux.jar %s &'% (jvm_option, wetool_option))
else:
    print('unknown platform')
