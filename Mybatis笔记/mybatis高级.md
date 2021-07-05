# 数据库环境切换

a.切换 environment （指定实际使用的数据库）

```xml
<!--default指定環境 -->
    <environments default="devOracle">
        <!--oracle -->
        <environment id="devOracle">
            <transactionManager type="JDBC" />
            <!-- 配置数据库连接信息 -->
            <dataSource type="POOLED">
                <property name="driver"  value="${oracle.driver}" />
                <property name="url"
                          value="${oracle.url}" />
                <property name="username" value="${oracle.username}" />
                <property name="password" value="${oracle.password}" />
            </dataSource>
        </environment>
        <!--mysql -->
        <environment id="devMysql">
            <transactionManager type="JDBC" />
            <!-- 配置数据库连接信息 -->
            <dataSource type="POOLED">
                <property name="driver"  value="${mysql.driver}" />
                <property name="url"
                          value="${mysql.url}" />
                <property name="username" value="${mysql.username}" />
                <property name="password" value="${mysql.password}" />
            </dataSource>
        </environment>
    </environments>
```



b.配置 Provider别名

```xml
<!-- 配置数据库支持类-->
    <databaseIdProvider type="DB_VENDOR">
            <property name="MySQL" value="mysql" />
            <property name="Oracle" value="oracle" />
    </databaseIdProvider>
```



c.写不同数据库的SQL语句

d.在mappe.xml中配置databaseId="Provider别名"

> 如果mapper.xml的 sql标签 仅有 一个 不带databaseId的标签，则改标签会自动适应
>
> 当前数据库如果既有不带databaseId的标签，又有带databaseId的标签,则程序会优
>
> 先使用带databaseId的标签

# 注解

推荐使用xml

a.将sql语句写在接口的方法上@Select("") ;

b.将接口的全类名写入<mapper>，让mybatis知道sql语句此时是存储在接口中

* **注解/xml都支持批量引入**

```xml
<mappers>
	<!--以下可以将com.yanqun.mapper 包中的注解接口 和 xml全部一次性引入 -->
          <package name="com.yanqun.mapper" />
 </mappers>
```

# 增删改的返回值问题

返回值可以是void、Integer、Long、Boolean

如何操作：只需要在接口中修改返回值即可

# 事务提交

* 自动提交

自动提交：每个dml语句自动提交

```java
sessionFactory.openSession(true);
```

* 手动提交

```java
 sessionFactory.openSession();
 session.commit();//手动提交
```

# 自增问题

* mysql支持自增

只需要配置两个属性即可

`useGeneratedKeys="true"` 将主键自增的值回写回来

`keyProperty="stuNo"`指定字段

```xml
<insert id="addStudent"
            parameterType="com.yanqun.entity.Student"  databaseId="mysql" useGeneratedKeys="true" keyProperty="stuNo">
          insert into student(stuName,stuAge,graName)
          values(#{stuName},#{stuAge},#{graName})
</insert>
```

# 参数问题

* 传入多个参数时，不用在mapper.xml中编写parameterType

可以使用的是： [arg3, arg2, arg1, arg0, param3, param4, param1, param2]

```xml
   <insert ...>
        insert into student(stuno,stuName,stuAge,graName)
        values(#{arg0} , #{arg1},#{arg2},#{arg3})
    </insert>
```

* 命名参数

可以在接口中通过@Param("sNo") 指定sql中参数的名字

~~~java
public abstract Integer addStudent(@Param("sNo") Integer stuNo)
~~~

~~~xml
<insert...>
    insert into student(stuno,...)
    values(#{sNo}, ...)
</insert>
~~~

* 综合使用

~~~java
Integer addStudent(@Param("sNo")Integer stuNo, @Param("stu")Student student);
~~~

~~~xml
<insert id="addStudent"  databaseId="oracle">
       	 insert into student(stuno,stuName,stuAge,graName)
       	 values(#{sNo} , #{stu.stuName},#{stu.stuAge},#{stu.graName})
</insert>
~~~

# 增加的数据为null

* oracle

如果插入的字段是Null, 提示错误：Other而不是null

* mysql

如果插入的字段是Null, 可以正常执行（没有约束）

# 返回值为HashMap

* 查一个学生

```xml
<select id="queryStudentOutByHashMap"   parameterType="int"
            resultType="HashMap">
         select stuNo "no",stuName "name",stuAge "age"
        from student  where stuNo = #{stuNo}
</select>
```

> 其中 stuNo是数据库的字段名“no”是stuNo的别名，用于 在map中 get值时使用(作为map的key) map.get("no" );
>
> 如果不加别名，则map的key就是字段名

* 查多个学生

程序根据select的返回值 知道map的value就是 Student ,根据 @MapKey("stuNo")知道

Map的key是stuNo

```java
  @MapKey("STUNO")
  HashMap<Integer,Student> queryStudentsByHashMap();
```

```xml
<select id="queryStudentsByHashMap"
            resultType="HashMap">
         select stuNo ,stuName ,stuAge  from student
</select>
```

# ResultMap

* resultMap中可以使用鉴别器

  对相同sql中不同字段值进行判断，从而进行不同的处理

```xml
<resultMap type="com.yanqun.entity.Student" id="studentResultMap">
        <!--主键 -->
        <id  column="sno" property="stuNo"/>
        <!--普通字段
        <result  column="sname" property="stuName"/> -->
        <result  column="sage" property="stuAge"/>
        <result  column="gname" property="graName"/>
        <!-- 鉴别器: 对查询结果进行分支处理： 如果是a年级，则真名，如果b年级，显示昵称-->
        <discriminator javaType="string"  column="gname">
            <case value="a" resultType="com.yanqun.entity.Student" >
                <result  column="sname" property="stuName"/>
            </case>
            <case value="b" resultType="student">
                <result  column="nickname" property="stuName"/>
            </case>
        </discriminator>
</resultMap>
```

# 别名

如果在批量设置别名时，出现了冲突，可以使用`@Alias("myStudent")`区分

# SQL标签

* where标签

<where>可以处理拼接sql中 【开头】第一个and

```xml
<where>
                   <if  test="stuName != null and stuName !='' ">
                     and stuName like '%${stuName}%'
                   </if>
                   <if  test="graName != null and graName !='' ">
                       and graName like '%${graName}%'
                   </if>
                   <if  test="stuAge != null and stuAge !='' ">
                       and stuAge = #{stuAge}
                	</if>
</where>
```

* trim标签

<trim>可以处理拼接sql中【开头或结尾】第一个and

```xml
<trim prefix="where" prefixOverrides="and">
               <if  test="stuName != null and stuName !='' ">
                   and stuName like '%${stuName}%'
               </if>
               <if  test="graName != null and graName !='' ">
                   and graName like '%${graName}%'
               </if>
               <if  test="stuAge != null and stuAge !='' ">
                   and stuAge = #{stuAge}
               </if>
</trim>
```

开头：给拼接的SQL加prefix="where" 

```xml
<trim prefix="where" prefixOverrides="and">
```

> prefixOverrides="and"，处理拼接SQL中【开头】第一个and
>
> suffixOverrides="and"，处理拼接SQL中【结尾】最后一个and

# 内置参数

_parameter:  代表mybatis的输入参数

_databaseId: 代表当前数据库的名字

# 模糊查询

* ${} ：原样输出

```
stuName like '%${stuName}%'
```

* #{} ：自动加' ' 

```xml
student.setStuName("%s%");  
stuName like #{stuName}
```

* bind参数

```xml
<!--通过bind将传入的stuName进行了处理（增加了%...%）-->
<bind name="_queryName" value="'%'+stuName+'%'"/>
```

# Mybatis架构和源码分析

## MyBatis中步骤

a.获取SqlSessionFactory对象

b.获取SqlSession对象

c.获取XxxMapper对象（代理接口中的方法、mapper.xml中的<select>

等标签）

d.执行<select>等标签中定义的SQL语句

### 获取SqlSessionFactory对象

* parser解析器

通过`parseConfiguration()`在configuration标签设置了properties、settings、	

environments等属性标签；

将所有的配置信息 放在了`Configuration`对象中；

解析所有的XxxMapper.xml文件（分析其中的增删改查标签）；

```
<select id="" resultType=" 等属性>是通过 parseStatementNode()解析的 
```

会将XxxMapper.xml中的 <select>等标签解析成 MappedStatement对象

> 每一个增删改都对应一个MappedStatement对象

**总结：**

MappedStatement ->存在于Configuration中

environment ->存在于Configuration中

======所有的配置信息、增删改标签 全部存在于Configuration中 ->

Configuration又存在于DefaultSqlSessionFactory对象中（SqlSessionFactory）

->

SqlSessionFactory对象 ->DefaultSqlSessionFactory ->Configuration ->包

含了一切配置信息

### 获取SqlSession对象

configuration.newExecutor(tx, execType) ->SimpleExecutor

根据不同的类型execType，产生不同的Executor,并且会对执行器进行拦截操作：

executor = (Executor) interceptorChain.pluginAll(executor);

通过**装饰模式**，将刚才产生的executor包装成一个更加强大的executor

> 作用：以后如果我们要给MyBatis写自己的插件， 就可以通过拦截器实现
>
> 插件开发：1、写插件	2、放入拦截器

返回DefaultSqlSession(configuration,executor,事务)

**总结：**

SqlSession -》openSession()->openSessionFromDataSource()-

>DefaultSqlSession对象

SqlSession -》 DefaultSqlSession对象 -》执行SQL

### 获取XxxMapper对象、执行

执行增删改查->MapperProxy/invoke()-->InvocationHandler ：JDK动态代

理接口

> 用到了 动态代理模式：增删改查 -> 代理对象 （MapperProxy对象） -
>
> 代理对象帮
>
> 我们“代理执行” 增删改查

* mapperMethod.execute(sqlSession,args) ：实际调用增删改查的方

  法，依靠了sqlSession中的configuration和 executor

* 处理增删改查方法的参数：

method.convertArgsToSqlCommandParam(args); 

如果参数是0个，reutrun null ;如果参数是1，返回第一个；如果有多个参

数放入map中

* 执行SQL 是通过Executor 

* 如果缓存中没有要查询的内容，则进入数据库真实查询：

queryFromDatabase()

* mybatis使用的jdbc對象是PreparedStatement

* 底层执行增删改查：PreparedStatement的execute()

* MyBatis底层在执行CRUD时可能会涉及到四个处理器：

  StatementHandler  ParameterHandler  TypeHandler 

  ResultSetHandler

* xxxMapper对象包含： SqlSession(configuration,executor,事务)、代

  理接口的对象(MapperInterface)、methodCache(存放查询缓存， 底

  层是CurrentHashMap)

# 自定义插件

* 四个处理器

StatementHandler  ParameterHandler   ResultSetHandler   TypeHandler

* 四大核心对象

StatementHandler  ParameterHandler   ResultSetHandler    Executor

**共同点：**

1、都涉及到了拦截器用于增强

2、四大核心对象都包含了该增强操作

# 批量操作DML

```java
//推荐的写法
sessionFactory.openSession(ExecutorType.BATCH ); 
```



