# coding:utf-8

import os
import re
import shutil

os.chdir('..')
print(os.popen('git pull').read())

with open('./pom.xml', 'r', encoding='utf-8') as fr:
    res = re.search('<wetool.version>(.*?)</wetool.version>',
                    fr.read(), re.M | re.I)
    version = res.group(1)


def package(os_name):
    print('package %s plateform\r\n' % os_name)
    print(os.popen('mvn clean package -D javafx.platform=%s' % os_name).read())
    filename = './wetool-%s.jar' % os_name
    if os.path.exists(filename):
        os.remove(filename)
    os.rename('./target/wetool-%s.jar' % version, filename)


package('win')
package('mac')
package('linux')
