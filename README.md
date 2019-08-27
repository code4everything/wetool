## WeTool 工具集

`WeTool`是一款基于`JavaFX`开发的轻量级、简洁、可插件式扩展的通用工具集

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
- [x] 常见的文件编码互相转换
- [x] 二进制与十六进制的互转
- [x] 二维码生成器
- [x] 随机字符生成器
- [x] 自定义快速打开文件菜单

> 更多功能期待你的参与。。。。。。

### 配置文件说明

程序会在启动时根据当前的系统名称加载当前工作目录下与之对应的配置文件`we-config-{}.json`，
其中`{}`可以为`win`、`mac`和`lin`分别对应`Windows`、`Mac`以及`Linux`平台，如上述文件不存在，那么将加载默认的配置文件`we-config.json`，
如果配置文件加载失败，程序将放弃运行，直接退出。

- 配置内容可参考：[we-config.json](/we-config.json)
- 属性说明可参考：[WeConfig.Java](https://gitee.com/code4everything/wetool-plugin/blob/master/wetool-plugin-support/src/main/java/org/code4everything/wetool/plugin/support/config/WeConfig.java)

### 下载

- 自行打包

    ``` shell
    git clone https://gitee.com/code4everything/wetool.git
    cd wetool
    mvn package
    ```
  
 - 插件的安装
 
    将插件放到当前工作目录的`plugins`目录下后，并将插件需要的配置信息写到配置文件中，重启程序即可
    
> 日志路径：`${user.home}/logs/wetool/wetool.log`

### 二次开发

开发调试过程中，建议运行[`WeApplicationTest`](/src/test/org/code4everything/wetool/WeApplicationTest.java)主类，
而不是运行主类[`WeApplication`](/src/main/java/org/code4everything/wetool/WeApplication.java)，
运行`WeApplication`类时日志数据会输出文件中，而`WeApplicationTest`则只会输出到终端，方便开发调试

### 插件开发

没有你需要的功能？没关系，一分钟快速了解插件如何开发：[传送门](https://gitee.com/code4everything/wetool-plugin)

### 结语

本工具只是作者空闲时将自己常用的一些功能可视化了的这么一个工具，并顺便学习一下，各位看官如有不适的地方，还望多多包涵，提出来让我加以改正

如果觉得还不错，可将鼠标移动至`Star`处，轻轻点一下，以示支持哦

欢迎提Issue，Pull Request，大家一起交流学习
