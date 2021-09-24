## 常识

**磁盘：**

1、寻址：ms

2、带宽：G/M

**内存：**

1、寻址：ns

2、带宽：很大

> 秒>毫秒>微秒>纳秒
>
> 在寻址上，磁盘比内存慢了十万倍

**I/Obuffer：成本问题**

磁盘与磁道，扇区，一扇区512Byte带来一个成本变大：索引 4K 操作系统，无论你读多少，都是最少4K从磁盘拿

## 数据存储发展进程

![img](C:\Users\admin\AppData\Local\Temp\企业微信截图_16322755123163.png)

## 数据库引擎介绍

**网站：**https://db-engines.com/en/

## redis简单介绍

中文官方网站：http://redis.cn/

### memcached与redis对比

![image-20210922101752579](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20210922101752579.png)

**计算向数据移动**

## redis安装实操

## epoll介绍

### 阻塞

![阻塞](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\Redis\图片\阻塞.PNG)

> **存在问题：**
>
> CPU只有一颗
>
> JVM：一个线程的成本1MB
>
> 1、线程多了调度成本CPU浪费
>
> 2、内存成本

### 同步非阻塞NIO

![同步非阻塞](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\Redis\图片\同步非阻塞.PNG)

### 多路复用NIO

#### select

![多路复用NIO](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\Redis\图片\多路复用NIO.PNG)

#### epoll

![epoll](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\Redis\图片\epoll.PNG)

## redis原理

![resid原理](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\Redis\图片\resid原理.PNG)

>只有Windows有AIO，Linux没有AIO

如果是在单个socket连接上，是存在顺序性的，但是如果多个socket连接，不存在广义上的顺序性

## redis使用

## redis中value类型--字符串

可以在redis中使用 help @String 查看关于Sring相关的操作

## redis中value类型--数值

## redis--二进制安全

使用redis时，要在客户端沟通好编码和解码的方式

## redis--Module

https://redis.io/modules

redis英文官网中，module模块中有许多增强redis功能的第三方插件

![module](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\Redis\图片\module.png)



