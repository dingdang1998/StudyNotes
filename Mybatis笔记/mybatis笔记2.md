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

# 类型处理器（类型转换器）

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

# 输入参数【parameterType】

**1.类型为简单类型（8个基本类型+String）**

**#{}、${}的区别**

a.

#{任意值}
${value} ，其中的标识符只能是value

> 只在简单类型下

b.

#{}自动给String类型加上' '  （自动类型转换）

${} 原样输出，但是适合于动态排序（动态字段）

~~~xml
select stuno,stuname,stuage  from student where stuname = #{value}
select stuno,stuname,stuage  from student where stuname = '${value}'
<!--动态排序-->
select stuno,stuname,stuage  from student  order by ${value} asc
~~~

c.

#{}可以防止SQL注入
${}不防止

**${}、#{}相同之处**

a.都可以获取对象的值（嵌套类型对象）

i.获取对象值
模糊查询，方式一

~~~xml
select stuno,stuname,stuage  from student where stuage= #{stuAge}  or stuname like #{stuName}
<!--student.setStuName("%w%") #{}会自动加''-->
~~~

模糊查询，方式二

~~~xml
<!--student.setStuName("w")-->	
select stuno,stuname,stuage  from student where stuage= #{stuAge}  or stuname like '%${stuName}%'
~~~

ii.嵌套类型对象

输入参数为级联属性，**属性.属性**

**输入对象为**`HashMap`

用map中的key的值匹配占位符`#{xxx}`，如果匹配成功，就用map的value替换占位符

# 输出参数【resultType和resultMap】

**1.类型为 简单类型（8个基本类型+String）**

**2.输出参数为实体对象类型**

**3.输出参数为实体对象类型的集合** 

**4.输出参数类型为HashMap**

~~~xml
<!-- 别名作为Map的key -->
<select id="queryStudentOutByHashMap"  resultType="HashMap" >
	select stuno "no",stuname "name" from student where stuno=1
</select>
~~~

> HashMap本身是一个集合，可以存放多个元素，但是根据提示发现返回值为HashMap时  ，查询的结果只能是1个学生（no,name）；
>
> 结论：一个HashMap 对应一个学生的多个元素（多个属性）【一个map，一个学生】，如果查询多个学生，返回结果用List接收就行

~~~
虽然输出类型为集合，但是resultType依然写集合的元素类型（resyltType="Student"）
~~~

> resultType、resultMap:实体类的属性、数据表的字段： 类型、名字不同时（stuno,id）
> 注意：当属性名和字段名不一致时，除了使用resultMap以外，还可以使用resultType+HashMap

**a.resultMap**

~~~xml
<resultMap type="student" id="queryStudentByIdMap">
	<!-- 指定类中的属性 和 表中的字段 对应关系 -->
	<id property="stuNo"  column="id" />
	<result property="stuName" column="name" />
</resultMap>
~~~

## Mybatis标签

**<where>标签**

~~~xml
<select id="queryStuByNOrAWishSQLTag" 	 parameterType="student"	resultType="student" >
		select stuno,stuname,stuage from student
		<where>
			<!-- <if test="student有stuname属性 且不为null"> -->
			<if test="stuName !=null and stuName!=''  "> 
				and stuname = #{stuName}
			</if>
			<if test="stuAge !=null and stuAge!=0  "> 
				 and stuage = #{stuAge}
			</if>
		</where>
</select>
~~~

> <where>会自动处理第一个<if>标签中的and，但不会处理之后<if>中的and

**<foreach>标签**

<foreach>迭代的类型：数组、对象数组、集合、属性(Grade类：List<Integer> ids)

~~~xml
	<!-- 将多个元素值放入对象的属性中 -->
	<select id="queryStudentsWithNosInGrade"  parameterType="grade" resultType="student">
	  	select * from student 
	  	<where>
	  		 <if test="stuNos!=null and stuNos.size>0">
	  		 	<foreach collection="stuNos" open=" and  stuno in (" close=")" 
	  		 		item="stuNo" separator=",">   
	  		 		#{stuNo}
	  		 	</foreach>
	  		 </if>
	  	</where>
	</select>
~~~

**简单类型的数组：**无论编写代码时，传递的是什么参数名(stuNos)，在mapper.xml中必须用array代替该数组

**集合：**无论编写代码时，传递的是什么参数名(stuNos)，在mapper.xml中必须用list代替该数组

~~~xml
	<!-- 将多个元素值 放入数组中 int[] stuNos = {1,2,53} -->
	<select id="queryStudentsWithArray"  parameterType="int[]" resultType="student">
	  	select * from student 
	  	<where>
	  		 <if test="array!=null and array.length">
	  		 	<foreach collection="array" open=" and  stuno in (" close=")" 
	  		 		item="stuNo" separator=",">   
	  		 		#{stuNo}
	  		 	</foreach>
	  		 </if>
	  	
	  	</where>
	</select>
	
	
	<!-- 将多个元素值放入数组中 List<Integer> stuNos 值 {1,2,53} -->
	<select id="queryStudentsWithList"  parameterType="list" resultType="student">
	  	select * from student 
	  	<where>
	  		 <if test="list!=null and list.size>0">
	  		 	<foreach collection="list" open=" and  stuno in (" close=")" 
	  		 		item="stuNo" separator=",">   
	  		 		#{stuNo}
	  		 	</foreach>
	  		 </if>
	  	</where>
	</select>
~~~

**对象数组**

~~~xml
parameterType="Object[]" 
	 	<foreach collection="array" open=" and  stuno in (" close=")" 
	  		 		item="student" separator=",">   
	  		 		#{student.stuNo}
	  	</foreach>
~~~

## SQL片段

**作用：**

a.提取相似代码
b.引用

~~~xml
<sql id="objectArrayStunos">
		<where>
	  		 <if test="array!=null and array.length>0">
	  		 	<foreach collection="array" open=" and  stuno in (" close=")" 
	  		 		item="student" separator=",">   
	  		 		#{student.stuNo}
	  		 	</foreach>
	  		 </if>
	  	</where>
</sql>


<select id="queryStudentsWithObjectArray"  parameterType="Object[]" resultType="student">
	  	select * from student 
	  	<!--如果sql片段和引用处不在同一个文件中，则需要在refid 引用时加上namespace:namespace.id
	   <include refid="org.lanqiao.mapper.abcMapper.objectArrayStunos"></include> -->
	   <include refid="objectArrayStunos"></include>
</select>
~~~

## 关联查询

* **一对一**

**a.业务扩展类**

核心：用resultType指定类的属性包含多表查询的所有字段

**b.resultMap**

1.通过**属性成员**将2个类建立起联系

2.

~~~xml
<resultMap type="student" id="student_card_map">
			<!-- 学生的信息 -->
			<id  property="stuNo" column="stuNo"/>
			<result property="stuName" column="stuName" />
			<result property="stuAge" column="stuAge" />
			<!-- 一对一时，对象成员使用 association映射;javaType指定该属性的类型-->
			<association property="card" javaType="StudentCard" >
					<id property="cardId" column="cardId"/>
					<result property="cardInfo" column="cardInfo"/>
			</association>
</resultMap>
~~~

* **一对多**

一对多：collection

~~~xml
<!-- 类-表的对应关系 -->
<resultMap type="studentClass" id="class_student_map">
			<!-- 因为 type的主类是班级，因此先配置班级的信息-->
			<id  property="classId" column="classId"/>
			<result  property="className" column="className"/>
			<!-- 配置成员属性学生，一对多;属性类型：javaType，属性的元素类型ofType -->
			<collection property="students" ofType="student">
				<id  property="stuNo" column="stuNo"/>
				<result  property="stuName" column="stuName"/>
				<result  property="stuAge" column="stuAge"/>	
			</collection>
</resultMap>
~~~

# Mybatis整合Log4j

**a.Log4j**

log4j.jar (mybatis.zip中lib中包含此jar)

**b.开启日志【conf.xml】**

~~~xml
<settings>
		<!-- 开启日志，并指定使用的具体日志 -->
		<setting name="logImpl" value="LOG4J"/>
</settings>
~~~

> 如果不指定，Mybatis就会根据以下顺序寻找日志：
> SLF4J →Apache Commons Logging →Log4j 2 → Log4j →JDK loggin

**c.编写配置日志输出文件【log4j.properties】**

~~~xml
log4j.rootLogger=DEBUG, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n
~~~

**作用**

可以通过日志信息，详细的阅读mybatis执行情况（ 观察mybatis实际执行sql语句以及SQL中的参数和返回结果）

# 延迟加载【懒加载】

**作用**

一对一、一对多、多对一、多对多

如果不采用延迟加载（立即加载），查询时会将一和多都查询，班级、班级中的所有学生
**延迟加载**可以暂时只查询1的一方，  而多的一方先不查询而是在需要的时候再去查询 

**mybatis中使用延迟加载，需要先配置**

~~~xml
<settings>
	<!-- 开启延迟加载 -->
	<setting name="lazyLoadingEnabled" value="true"/>
	<!-- 关闭立即加载 -->
	<setting name="aggressiveLazyLoading" value="false"/>
</settings>
~~~



~~~xml
<resultMap type="student" id="student_card_lazyLoad_map">
			<!-- 学生的信息 -->
			<id  property="stuNo" column="stuNo"/>
			<result property="stuName" column="stuName" />
			<result property="stuAge" column="stuAge" />
			<!-- 一对一时，对象成员使用 association映射;javaType指定该属性的类型
				此次采用延迟加载：在查询学生时，并不立即加载学生证信息
			-->
			<!-- 学生证,通过select 在需要的时候再查学生证 -->
			<association property="card" javaType="StudentCard"  select="org.lanqiao.mapper.StudentCardMapper.queryCardById"  column="cardid">
			</association>
</resultMap>

<mapper namespace="org.lanqiao.mapper.StudentCardMapper">
	<!-- 查询学生证信息 -->	
	<select id="queryCardById" parameterType="int"  resultType="studentCard">
		<!-- 查询学生对应的学生证 -->
		select * from studentCard  where cardid = #{cardId}
	</select>
	<!-- 根据cardid查询学生证的SQL： org.lanqiao.mapper.StudentCardMapper.queryCardById-->
</mapper>
~~~

> 两张表关联的外键放在column中
>
> 如果程序只需要学生，则只向数据库发送了查询学生的SQL
> 当我们后续 需要用到学生证的时候，再第二次发送 查询学生证的SQL

**一对多配置延迟加载**

和一对一的延迟加载配置方法相同

1.开启延迟加载conf.xml配置settings

2.配置mapper.xml

~~~xml
<select id="queryClassAndStudents"   resultMap="class_student_lazyLoad_map">
		select  c.* from studentclass c
</select>

<resultMap type="studentClass" id="class_student_lazyLoad_map">
			<!-- 因为 type的主类是班级，因此先配置班级的信息-->
			<id  property="classId" column="classId"/>
			<result  property="className" column="className"/>
			<!-- 配置成员属性学生，一对多;属性类型：javaType，属性的元素类型ofType -->
			<collection property="students" ofType="student" select="org.lanqiao.mapper.StudentMapper.queryStudentsByClassId" column="classid">

			</collection>
</resultMap>
	<!--即查询学生的sql是通过 select属性指定，并且通过column指定外键学生mapper.xml-->
	<!-- 一对多,延迟加载需要的： 查询班级中的所有学生 -->
	<select id="queryStudentsByClassId" parameterType="int" resultType="student">
		select * from student where classId = #{classId}
	</select>
~~~

# 查询缓存

## 一级缓存

**同一个sqlSession对象**

MyBatis默认开启一级缓存，如果用同样的SqlSession对象查询相同的数据，则只会在第一次 查询时 向数据库发送SQL语句，并将查询的结果放入到SQLSESSION中（作为缓存在）后续再次查询该同样的对象时，则直接从缓存中查询该对象即可（即省略了数据库的访问）

## 二级缓存

### Mybatis自带的二级缓存

【同一个namespace】生成的mapper对象

> 只要产生的xxxMapper对象 来自于同一个namespace，则这些对象共享二级缓存	

**使用**

MyBatis默认情况没有开启二级缓存，需要手工打开：

a.conf.xml

~~~xml
<!-- 开启二级缓存 -->
<setting name="cacheEnabled" value="true"/>
~~~

b.在具体的mapper.xml中声明开启(studentMapper.xml中)

~~~xml
<mapper namespace="org.lanqiao.mapper.StudentMapper">
<!-- 声明次namespace开启二级缓存 -->
<cache/>
~~~

> 准备缓存的对象，必须实现了序列化接口 （如果开启的缓存Namespace="org.lanqiao.mapper.StudentMapper"），可知序列化对象为Student，因此需要将Student序列化 （序列化Student类，以及Student的级联属性和父类）
>
> 触发将对象写入二级缓存的时机：SqlSession对象的close()方法
>
> **注意：二级缓存的范围是同一个namespace, 如果有多个xxMapper.xml的namespace值相同，则通过这些xxxMapper.xml产生的xxMapper对象仍然共享二级缓存**

**禁用**

~~~xml
select标签中useCache="false"
~~~

**清理缓存**

a.与清理一级缓存的方法相同
commit(); （一般执行增删改时会清理掉缓存；设计的原因是为了防止脏数据）

> commit会清理一级和二级缓存；但是清理二级缓存时，不能是查询自身的commit；

b. 在select标签中增加属性flushCache="true"

**命中率**

> 1: 	0%  
> 2:     50%
> 3:     2/3 	0.666
> 4: 	3/4    0.75

### 三方提供的二级缓存

ehcache、memcache

* **前提**

要想整合三方提供的二级缓存 （或者自定义二级缓存），必须实现`org.apache.ibatis.cache.Cache`接口，该接口的默认实现类是`PerpetualCache`

* **整合ehcache二级缓存**

a.导入依赖
`ehcache-core.jar`
`mybatis-Ehcache.jar`
`slf4j-api.jar`

b.编写ehcache配置文件Ehcache.xml

~~~xml
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../config/ehcache.xsd">
  <!--当二级缓存的对象 超过内存限制时（缓存对象的个数>maxElementsInMemory），存放入的硬盘文件  -->
 <diskStore path="D:\Ehcache"/>
 <!-- 
 	maxElementsInMemory:设置在内存中缓存对象的个数
    maxElementsOnDisk：设置在硬盘中缓存对象的个数
    eternal：设置缓存是否永远不过期
    overflowToDisk：当内存中缓存的对象个数超过maxElementsInMemory的时候，是否转移到硬盘中
    timeToIdleSeconds：当2次访问超过该值的时候，将缓存对象失效 
    timeToLiveSeconds：一个缓存对象最多存放的时间（生命周期）
    diskExpiryThreadIntervalSeconds：设置每隔多长时间，通过一个线程来清理硬盘中的缓存
    memoryStoreEvictionPolicy：当超过缓存对象的最大值时，处理的策略：LRU（最近最少使用），FIFO,LFU
  -->		     
 
 <defaultCache
  maxElementsInMemory="1000"
  maxElementsOnDisk="1000000"
  eternal="false"
  overflowToDisk="false"
  timeToIdleSeconds="100"
  timeToLiveSeconds="100"
  diskExpiryThreadIntervalSeconds="120"
  memoryStoreEvictionPolicy="LRU">
 </defaultCache>
</ehcache>
~~~

c.开启EhCache二级缓存

~~~xml
<!--在xxxMapper.xml中开启-->
<cache  type="org.mybatis.caches.ehcache.EhcacheCache">
	<!-- 通过property覆盖Ehcache.xml中的值 -->
	<property name="maxElementsInMemory" value="2000"/>
	<property name="maxElementsOnDisk" value="3000"/>
</cache>
~~~

# 逆向工程

类、接口、mapper.xml四者密切相关，因此 当知道一个的时候  其他三个应该可以自动生成（一般根据表—>生成其他三个）

* **实现步骤**

a.导入依赖  

`mybatis-generator-core.jar`

`mybatis.jar`

`ojdbc.jar`

b.  逆向工程的配置文件generator.xml

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
   <context id="DB2Tables" targetRuntime="MyBatis3">
   <commentGenerator>
   <!--
			suppressAllComments属性值：
				true:自动生成实体类、SQL映射文件时没有注释
				true:自动生成实体类、SQL映射文件，并附有注释
		  -->
  <property name="suppressAllComments" value="true" />
 </commentGenerator>
 
 
 <!-- 数据库连接信息 -->
  <jdbcConnection driverClass="oracle.jdbc.OracleDriver"
   connectionURL="jdbc:oracle:thin:@127.0.0.1:1521:ORCL" 
   userId="scott"  password="tiger">
  </jdbcConnection>
  <!-- 
			forceBigDecimals属性值： 
				true:把数据表中的DECIMAL和NUMERIC类型，
解析为JAVA代码中的java.math.BigDecimal类型 
				false(默认):把数据表中的DECIMAL和NUMERIC类型，
解析为解析为JAVA代码中的Integer类型 
		-->
 <javaTypeResolver>
  	<property name="forceBigDecimals" value="false" />
 </javaTypeResolver>
 <!-- 
		targetProject属性值:实体类的生成位置  
		targetPackage属性值：实体类所在包的路径
	-->
 <javaModelGenerator targetPackage="org.lanqiao.entity"
                            targetProject=".\src">
  <!-- trimStrings属性值：
			true：对数据库的查询结果进行trim操作
			false(默认)：不进行trim操作       
		  -->
  <property name="trimStrings" value="true" />
 </javaModelGenerator>
 <!-- 
		targetProject属性值:SQL映射文件的生成位置  
		targetPackage属性值：SQL映射文件所在包的路径
	-->
  <sqlMapGenerator targetPackage="org.lanqiao.mapper" 
			targetProject=".\src">
  </sqlMapGenerator>
  <!-- 生成动态代理的接口  -->
 <javaClientGenerator type="XMLMAPPER" targetPackage="org.lanqiao.mapper" targetProject=".\src">
 </javaClientGenerator>
 
 <!-- 指定数据库表  -->
  <table tableName="Student"> </table>
  <table tableName="studentCard"> </table>
  <table tableName="studentClass"> </table>
 </context>
</generatorConfiguration>
~~~

c.  执行

~~~java
package org.lanqiao.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

public class Test {
	public static void main(String[] args) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
		//配置文件
		File file = new File("src/generator.xml") ;

		List<String> warnings = new ArrayList<>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(file);
		
		DefaultShellCallback callBack = new DefaultShellCallback(true);
		//逆向工程的核心类
		MyBatisGenerator generator = new MyBatisGenerator(config, callBack,warnings  );
		generator.generate(null);
	}
}
~~~

