# coding:utf-8

import platform
import os
import sys

system = platform.system()
option = ' '


for arg in sys.argv[1:]:
    if 'debug' == arg:
        option = ' -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 '
        break


if 'Windows' == system:
    os.system('javaw -jar%swetool-win.jar' % option)
elif 'Darwin' == system:
    os.system('java -jar%swetool-mac.jar &' % option)
elif 'Linux' == system:
    os.system('java -jar%swetool-linux.jar &' % option)
else:
    print('unknown platform')
