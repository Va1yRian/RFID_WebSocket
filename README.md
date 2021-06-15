# RFID_WebSocket
An RFID tool using WebSocket to transfer data from Ocetane SDK (Java) to Python
# 环境搭建

项目IDE使用IntelliJ IDEA Community Edition 2020.3.1与PyCharm Community Edition 2020.3.2，IDE一样的话直接使用对应IDE打开相应文件夹即可，否则需要按一下步骤搭建环境。

### JAVA环境搭建

1. 创建一个Maven项目，可以不使用模板

![image](https://cdn.jsdelivr.net/gh/brokengun1001/Hello-World@master/RFID_WebSocket/image.2veckkeirys0.png)

2. 编辑pom.xml文件，在project标签下添加以下子标签，添加WebSocket相关依赖：

   ```xml
   <dependencies>
       <dependency>
           <groupId>org.glassfish.tyrus.bundles</groupId>
           <artifactId>tyrus-standalone-client</artifactId>
           <version>1.13</version>
       </dependency>
   </dependencies>
   ```

   注：连接Maven中心库可能会失败，可以使用梯子或者使用国内镜像

3. 手动加入Octane-SDK依赖

   ![image](https://cdn.jsdelivr.net/gh/brokengun1001/Hello-World@master/RFID_WebSocket/image.6mw5o4n0f3k0.png)

4. 将SDK样例目录（Impinj_SDK_Java_v3.5.0\samples\com\example\sdksamples）下的SampleProperties与TagReportListenerImplementation文件复制到源代码目录下

   ![image](https://cdn.jsdelivr.net/gh/brokengun1001/Hello-World@master/RFID_WebSocket/image.4yasalna1f00.png)

