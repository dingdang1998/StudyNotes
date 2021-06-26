* **修改数据库环境**

**通过配置文件修改默认加载环境**

<!--通过environments的default值和environment的id指定MyBatis运行时的数据库环境-->

**通过编码修改**

build()的第二个参数

~~~java
SqlSessionFactory sessionFacotry = new SqlSessionFactoryBuilder().build(reader,"development") ;
~~~

* **数据源类型【dataSource】**

**UNPOOLED：**传统的JDBC模式（每次访问数据库，均需要打开、关闭等数据库操作，但是打开、关闭数据库是比较消耗性能的）
**POOLED：**使用数据库连接池
**JNDI：**从tomcat中获取一个内置的数据库连接池（数据库连接池-数据源  ）

* **事务提交方式【transactionManager】**

**JDBC:**利用JDBC方式处理事务（commit  rollback  close）
**MANAGED：**将事务交由其他组件去托管（spring ,jobss）,默认会关闭连接

> 如果MANAGED用完链接不关闭
>
> <transactionManager type="MANAGED"/>
> 				<property name="closeConnection" value="false"/>

* **namespace**

该mapper.xml映射文件的唯一标识

* **parameterType**

输入参数的类型

* **resultType**

查询返回结果值的类型 ,返回类型

* **注意事项**
  * mybatis约定：输入参数parameterType和输出参数resultType，在形式上都只能有一个
  * 如果输入参数 ：是简单类型（8个基本类型+String） 是可以使用任何占位符#{xxxx}如果是对象类型，则必须是对象的属性 #{属性名}
  * 输出参数：  如果返回值类型是一个 对象（如Student），则无论返回一个、还是多个，在resultType都写成org.lanqiao.entity.Student即 resultType="org.lanqiao.entity.Student"

# 基础方式的增删改查CRUD

* **注意事项**

a、如果使用的事务方式为 jdbc,则需要手工commit提交，即session.commit();
b、所有的标签 <select> <update>等 ，都必须有sql语句，但是sql参数值可选

~~~java
//sql有参数
session.insert(statement, 参数值 );
//sql没参数
session.insert(statement);
~~~

# Mapper动态代理方式的crud （MyBatis接口开发）

基于一个原则：约定优于配置 

## 具体实现步骤

* **基础环境**

mybatis.jar/ojdbc.jar、**conf.xml**、mapper.xml

* **不同之处**

约定的目标： 省略掉statement,即根据约定直接可以定位出SQL语句

**接口中的方法必须遵循以下约定：**

1.方法名和mapper.xml文件中标签的id值相同
2.方法的 输入参数 和mapper.xml文件中标签的 parameterType类型一致 (如果mapper.xml的标签中没有 parameterType，则说明方法没有输入参数)
3.方法的返回值  和mapper.xml文件中标签的 resultType类型一致 （无论查询结果是一个还是多个（student、List<Student>），在mapper.xml标签中的resultType中只写 一个（Student）；如果没有resultType，则说明方法的返回值为void）

> 除了以上约定，要实现接口中的方法和Mapper.xml中SQL标签一一对应，还需要以下1点：namespace的值 ，就是接口的全类名（ 接口 - mapper.xml 一一对应）

**匹配的过程（约定的过程）：**
1.根据接口名找到 mapper.xml文件（根据的是namespace=接口全类名）
2.根据接口的方法名找到mapper.xml文件中的SQL标签 （方法名=SQL标签Id值）

## 配置优化

* **可以将配置信息单独放入db.properties文件中，然后再动态引入**

a、db.properties（存放K-V结构）
	  k=v

b、conf.xml中增加标签引入

~~~xml
<configuration>
	<properties  resource="db.properties"/>
~~~

c、引入之后，使用${key}

* **MyBatis全局参数**

> 注意：mybatis全局参数对系统影响较大，牵一发而动全身，不熟悉或不到万不得以不要轻易修改，具体有哪些参数，参考word文档

**如何设置**

在conf.xml中设置

~~~xml
<settings>
		<setting name="cacheEnabled" value="false"  />
		<setting name="lazyLoadingEnabled" value="false"  />
</settings>
~~~

* **定义别名**

a.设置单个别名

~~~xml
<typeAliases>
	<!-- 单个别名（别名忽略大小写） -->
	<typeAlias type="org.lanqiao.entity.Student" alias="student"/> 
</typeAliases>
~~~

b.批量设置别名

~~~xml
<typeAliases>
	<!--  批量定义别名  （别名 忽略大小写），以下会自动将该包中的所有类 批量定义别名： 别名就是类名（不带包名，忽略大小写）   -->
	<package name="org.lanqiao.entity"/>
</typeAliases>
~~~

c.mybatis内置别名映射

> 文件中的图片

## 类型处理器（类型转换器）

* **MyBatis自带一些常见的类型处理器**

int  - number

* **自定义MyBatis类型处理器**

java——数据库(jdbc类型)

**示例：**

~~~
实体类Student :  boolean   stuSex  	
			true:男
			false:女

表student：	number  stuSex
			1:男
			0:女
~~~

**自定义类型转换器（boolean -number）步骤：**

a.需要实现`TypeHandler`接口

通过阅读源码发现，此接口有一个实现类BaseTypeHandler，因此要实现转换器有2种选择：
	i.实现接口TypeHandler接口
	ii.继承BaseTypeHandler类

~~~java

public class BooleanAndIntConverter extends BaseTypeHandler<Boolean>{
	
	/*
	 * ps:PreparedStatement对象
	 * i：PreparedStatement对象操作参数的位置
	 * parameter:java值
	 * jdbcType：jdbc操作的数据库类型
	 */
    //set：java代码——数据库
    //java(boolean)-DB(number)
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
			throws SQLException {
			if(parameter) {
				ps.setInt(i, 1); 
			}else {
				ps.setInt(i, 0); 
			}
    }

	//db(number)->java(boolean)
	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        //rs.getInt("stuno")
		int sexNum = rs.getInt(columnName);
		return sexNum == 1?true:false;
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        //rs.getInt(1)
		int sexNum = rs.getInt(columnIndex) ;
		return sexNum == 1?true:false ;
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        //rs.getInt(1)
		int sexNum = cs.getInt(columnIndex) ;
		return sexNum == 1?true:false ;
	}
}
~~~

b.配置conf.xml

~~~xml
<typeHandlers>
	<typeHandler handler="org.lanqiao.converter.BooleanAndIntConverter" javaType="Boolean" jdbcType="INTEGER" />
</typeHandlers>
~~~

mapper.xml

> <!-- 查询：使用了类型转换器
> 	1如果类中属性和表中的字段类型能够合理识别（String-varchar2），则可以使用resultType;否则(boolean-number) 使用resultMap
> 	2如果类中属性名和表中的字段名能够合理识别（stuNo -stuno）则可以使用resultType;否则(id-stuno) 使用resultMap
> -->
>
> 需要注意的问题：INTEGER【只能设置成大写】

~~~xml
<select id="queryStudentByStunoWithConverter" 	parameterType="int"  resultMap="studentResult" >
	select * from student where stuno = #{stuno}
</select>
	
<resultMap type="student" id="studentResult">
			<!-- 分为主键id 和非主键 result-->
			<id property="stuNo"  column="stuno"  />
			<result property="stuName"  column="stuname" />
			<result property="stuAge"  column="stuage" />
			<result property="graName"  column="graname" />
			<result property="stuSex"  column="stusex"  javaType="boolean" jdbcType="INTEGER"/>
</resultMap>

<!-- 带转换器的增加 -->
<insert id="addStudentWithConverter" parameterType="student" >
		insert into student(stuno,stuname,stuage,graname,stusex) values(#{stuNo},#{stuName},#{stuAge},#{graName} ,#{stuSex ,javaType=boolean  ,jdbcType=INTEGER} ) 
</insert>
~~~

* **resultMap可以实现2个功能：**

1.类型转换
2.属性-字段的映射关系