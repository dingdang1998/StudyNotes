# maven的作用

* 管理Jar

i.增加第三方Jar   (commons-fileupload.jar   commons-io.jar)
ii.jar包之间的依赖关系 （commons-fileupload.jar 自动关联下载所有依赖的Jar，并且不会冲突）

* 将项目拆分成若干个模块

# maven概念

是一个基于Java平台的自动化构建工具

将原材料（java、js、css、html、图片）->产品（可发布项目）

# maven可以做的事情

## 清理

删除编译的结果，为重新编译做准备

## 编译

java->class

## 测试

针对于项目中的关键点进行测试，亦可用项目中的测试代码去测试开发代码

## 打包

将测试的结果进行显示

## 安装

将打成的包放到本地仓库，供其他项目使用

## 部署

将打成的包放到服务器上准备运行

# 使用maven

## 下载配置maven

1、配置JAVA_HOME

2、配置MAVEN_HOME

> D:\apache-maven-3.5.3\bin

3、配置path

> %MAVEN_HOME%\bin

4、验证

> mvn -v

5、配置本地仓库

maven目录/conf/settings.xml

默认本地仓库：Default: ${user.home}/.m2/repository

修改本地仓库：  <localRepository>D:/mvnrep</localRepository>

## 使用maven

约定优于配置

### maven约定目录结构

项目
		-src				
			--main		   ：程序功能代码
				--java		       java代码  (Hello xxx)
				--resources    资源代码、配置代码
			--test			  :  测试代码
				--java			
				--resources	
	     -pom.xml		：项目对象模型

~~~java
<groupId>域名翻转.大项目名</groupId>
<groupId>org.lanqiao.maven</groupId>
<artifactId>子模块名</artifactId>
<artifactId>HelloWorld</artifactId>
<version>版本号</version>
<version>0.0.1-SNAPSHOT</version>
~~~

### 依赖

A中的某些类需要使用B中的某些类，则称为A依赖于B

在maven中通过

```java
<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.0</version>
			<scope>test</scope>
</dependency>
```

体现

#### 依赖的范围、有效性

compile(默认)   test   provided

#### 依赖排除

```java
<exclusions>
	<exclusion>
		<groupId>org.springframework</groupId>
   		<artifactId>spring-beans</artifactId>
	</exclusion>
</exclusions>
```

#### 依赖的传递性

* A.jar-B.jar->C.jar

要使 A.jar ->C.jar:当且仅当 B.jar 依赖于C.jar的范围是compile

* 多个maven项目（模块）之间如何 依赖： p项目 依赖于->q项目

1. p项目 install 到本地仓库
2. q项目依赖：

```java
<!-- 本项目  依赖于HelloWorld2项目 -->
<dependency>
 	<groupId>org.lanqiao.maven</groupId>
	<artifactId>HelloWorld2</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### 依赖原则

a.路径最短优先原则

b.路径长度相同

i.在同一个pom.xml文件中有2个相同的依赖（覆盖）：后面声明的依赖会覆盖前面声明的依赖 （严禁使用本情况，严禁在同一个pom中声明2个版本不同的依赖）

ii.如果是不同的 pom.xml中有2个相同的依赖（优先）：则先声明的依赖 ，会覆盖后声明的依赖

### maven常见命令

第一次执行命令时，因为需要下载执行该命令的基础环境，所以会从中央仓库下载该环境到本地仓库

* mvn compile

只编译main目录中的java文件

* mvn test

测试

* mvn package

打成jar/war

* mvn install

将开发的模块 放入本地仓库，供其他模块使用 （放入的位置 是通过gav决定）

* mvn clean

删除target目录（删除编译文件的目录）

**注意：**

运行mvn命令，必须在pom.xml文件所在目录

### maven生命周期

* 生命周期和构建的关系

生命周期中的顺序：a b c d e 

当我们执行c命令，则实际执行的是 a b c

* 生命周期包含的阶段：3个阶段

1、clean lifecycle ：清理

pre-clean   clean   post-clearn

2、default lifecycle ：默认(常用)

3、site lifecycle：站点

pre-site   site   post-site site-deploy

### 通过maven统一jdk版本

```java
<profiles>
    <profile>  
        <id>jdk-18</id>  
        <activation>  
            <activeByDefault>true</activeByDefault>  
            <jdk>1.8</jdk>  
        </activation>  
        <properties>  
            <maven.compiler.source>1.8</maven.compiler.source>  
            <maven.compiler.target>1.8</maven.compiler.target>  
<maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>  
        </properties>   
    </profile>  
 </profiles>
```





