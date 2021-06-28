# 发展历史

ibatis:apache
2010 ibatis-转交给> google colde ,Mybatis

# 作用及概念

MyBatis可以简化JDBC操作，实现数据的持久化
ORM【Object Relational Mapping】是一个概念
person对象——person表
Mybatis是ORM的一个实现/Hibernate 
orm可以是的开发人员 像操作对象一样操作数据库表

# 开发mybatis程序步骤

**conf.xml：配置数据库信息和需要加载的映射文件**
表——类

## 引入jar包

mybatis-3.4.6.jar

## 映射文件xxMapper.xml 

~~~xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="映射文件的路径">
	<select id="queryPersonById" 						resultType="org.lanqiao.entity.Person"  parameterType="int">
		select * from person where  id = #{id} 
	</select>
</mapper>
~~~

## mybatis config配置文件

conf.xml

~~~xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
	<environments default="development">
		<environment id="development">
		<transactionManager type="JDBC"/>
			<dataSource type="POOLED">
				<!-- 配置数据库信息 -->
			<property name="driver" value="oracle.jdbc.OracleDriver"/>
			<property name="url" value="jdbc:oracle:thin:@127.0.0.1:1521:ORCL"/>
			<property name="username" value="scott"/>
			<property name="password" value="tiger"/>
		</dataSource>
		</environment>
	</environments>
	<mappers>
		<!-- 加载映射文件 -->
		<mapper resource="org/lanqiao/entity/personMapper.xml"/>
	</mappers>
</configuration>
~~~

## 测试类

session.selectOne("需要查询的SQL的namespace.id","SQL的参数值");

~~~java
package org.lanqiao.entity;

import java.io.IOException;
import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class TestMyBatis {
	public static void main(String[] args) throws IOException {
		//加载MyBatis配置文件（为了访问数据库）
		Reader reader = Resources.getResourceAsReader("conf.xml") ;
		SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader) ;
		//session - connection
		SqlSession session = sessionFactory.openSession() ;
		String statement = "org.lanqiao.entity.personMapper.queryPersonById" ;
		Student person = session.selectOne(statement,1 ) ;
		System.out.println(person);
		session.close(); 	
	}
}
~~~

