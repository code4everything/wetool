## WeTool 工具箱

`WeTool`是一款基于`JavaFX`开发的轻量级、简洁、可插件式扩展的通用工具箱

### 开发环境

- JDK 1.8
- Maven 3.5
- Scene Builder 2.0
- IDEA 2019.2
- Lombok
- Git

### 有哪些功能？

- [x] 批量重命名文件，复制文件，合并文件
- [x] 监听系统剪贴板，随时查看剪贴板历史（仅支持字符串）
- [x] 查看本地IP地址，MAC地址，以及域名解析
- [x] 基于 `FastJson` 的JSON解析，可用于测试路径语法
- [x] 常见的文件编码互相转换，识别文件编码
- [x] 二进制与十六进制的互转
- [x] 二维码生成器
- [x] 随机字符生成器
- [x] 自定义快速打开文件菜单

### 配置文件说明

程序会在启动时根据当前的系统名称加载当前工作目录下与之对应的配置文件`we-config-{}.json`，
其中`{}`可以为`win`、`mac`和`lin`分别对应`Windows`、`Mac`以及`Linux`平台，如上述文件不存在，那么将加载默认的配置文件`we-config.json`，
如果配置文件加载失败，程序将放弃运行，直接退出。

- 配置内容可参考：[we-config.json](we-config.json)
- 属性说明可参考：[WeConfig.Java](https://gitee.com/code4everything/wetool-plugin/blob/master/wetool-plugin-support/src/main/java/org/code4everything/wetool/plugin/support/config/WeConfig.java)

### 下载与使用

- [wetool-1.0.2.zip](http://share.qiniu.segocat.com/tool/wetool/wetool-1.0.2.zip)

- [更新历史](history.md)

- 自行打包

    ``` shell
    git clone https://gitee.com/code4everything/wetool.git
    cd wetool
    mvn package
    ```
  
- 运行

    ``` shell
    # windows平台
    javaw -jar ./wetool.jar
    # mac或linux平台
    java -jar ./wetool.jar &
    ```
    > 日志路径：`${user.home}/logs/wetool/wetool.log`
  
- 插件的安装
 
    将插件放到当前工作目录的`plugins`目录下后，并将插件需要的配置信息写到配置文件中，重启程序即可
    
    > [插件库](https://gitee.com/code4everything/wetool-plugin/tree/master/wetool-plugin-repository)
    
### 运行截图

![wetool](images/wetool.png)

### 二次开发

开发调试过程中，建议运行[`WeApplicationTest`](src/test/java/org/code4everything/wetool/WeApplicationTest.java)主类，
而不是运行主类[`WeApplication`](src/main/java/org/code4everything/wetool/WeApplication.java)，
运行`WeApplication`类时产生的日志数据会被输出到文件中，而`WeApplicationTest`则只会输出到终端，方便开发调试

### 插件开发

没有你需要的功能？没关系，一分钟快速了解插件如何开发：[传送门](https://gitee.com/code4everything/wetool-plugin)

### 结语

本工具只是作者空闲时将自己常用的一些功能可视化了的这么一个工具，各位看官如有不适的地方，还望多多包涵，也可提出来让在下加以改正哦

关于插件的支持，纯粹是为了减轻本工具包的负担，毕竟本工具的宗旨是轻量级、轻量级、轻量级，而某些只会在特定场景下使用的功能则以插件的方式加载进来，可以极大缩减本工具包的大小，而不至于臃肿

如果你觉得这个项目还不错，可将鼠标移动至`Star`处，轻轻点一下，以示支持哦^_^

欢迎提Issue，Pull Request，大家一起交流学习
