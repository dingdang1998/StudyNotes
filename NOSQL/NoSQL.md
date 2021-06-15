# NoSQL数据库

NoSQL: not only sql

Redis:分布式数据库

Hbase: 大数据领域的数据库(hadoop)

neo4j:图形化数据库

mongodb:分布式文档存储的数据库

## Redis

全称：remote dictionary server，基于key-value结构 。将数据存储在内存 （硬盘，rdb/aof）,写速度10w/s。

（1）缓存

​	java -redis - db

（2）存储

### redis 环境搭建

部署环境:linux（centos7）

在centos7中执行：wget http://download.redis.io/releases/redis-6.0.1.tar.gz

解压tar -zxvf redis-6.0.1.tar.gz

重命名mv redis-6.0.1 redis

编译:进入redis目录：执行make

> 说明：如果执行make报错：/bin/sh: cc: command not found
>
> 解决： yum install gcc-c++
>
> 如果安装cc后仍然报错：fatal error: jemalloc/jemalloc.h: No such file or directory
>
> 解决： make  MALLOC=libc
>
> 如果报错： error: â€˜struct redisServerâ€™ has no member named â€˜supervised_modeâ€
>
> 解决：可能是gcc版本太低，升级gcc:
>
> yum -y install centos-release-scl yum -y install devtoolset-9-gcc devtoolset-9-gcc-c++ devtoolset-9-binutils scl enable devtoolset-9 bash
>
> 再试： make  MALLOC=libc

配置：redis.conf：  daemonize  yes  (理解为：开启redis后，可以同时去操作其他事情)

测试：

服务端：redis默认端口6379

客户端

![1588556842280](C:\Users\admin\Desktop\JavaCore-master\JavaCore-master\notes\微服务\NOSQL\nosql.assets\1588556842280.png)

### redis常见操作

注意事项： 

1.不区分大小写 hello HELLO

2.单位: k != kb  

k:1000  kb:1024  

3.默认提供了16个数据库（编号0-15） select 0

### String(默认)

http://www.redis.cn/

```
set key value  [EX 秒][px 毫秒]  [NX不存在][XX存在] 
```

案例：

```
127.0.0.1:6379> select 0
OK
127.0.0.1:6379> set name zs
OK
127.0.0.1:6379> get name
"zs"
127.0.0.1:6379> strlen name
(integer) 2
127.0.0.1:6379> getrange name 0 1
"zs"
127.0.0.1:6379> set name zhangsan
OK
127.0.0.1:6379> getrange name 0 1
"zh"
127.0.0.1:6379> mset k1 v1 k2 v2 
OK
127.0.0.1:6379> get k1
"v1"
127.0.0.1:6379> mget k1 k2 k3
1) "v1"
2) "v2"
3) (nil)
127.0.0.1:6379> setex address 10 xa
OK
127.0.0.1:6379> get address
(nil)
127.0.0.1:6379> setex address 10 xa
OK
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
"xa"
127.0.0.1:6379> get address
(nil)
127.0.0.1:6379> setex address 10 xa
OK
127.0.0.1:6379> ttl address
(integer) 4
127.0.0.1:6379> ttl address
(integer) 2
127.0.0.1:6379> ttl address
(integer) 1
127.0.0.1:6379> ttl address
(integer) -2
127.0.0.1:6379> ttl address
(integer) -2
127.0.0.1:6379> ttl address
(integer) -2
127.0.0.1:6379> setnx age 23
(integer) 1
127.0.0.1:6379> setnx age 23
(integer) 0
127.0.0.1:6379> setnx age 23
(integer) 0
127.0.0.1:6379> set num 10
OK
127.0.0.1:6379> incr num
(integer) 11
127.0.0.1:6379> incr num
(integer) 12
127.0.0.1:6379> decr num
(integer) 11
127.0.0.1:6379> decr num
(integer) 10
127.0.0.1:6379> incrby num 100
(integer) 110
127.0.0.1:6379> decrby num 50
(integer) 60
127.0.0.1:6379> set name lisi ex 10 nx
(nil)
127.0.0.1:6379> get name
"zhangsan"
127.0.0.1:6379> set name lisi ex 10 xx
OK
127.0.0.1:6379> set name wangwu 
OK
```



### List操作



```
127.0.0.1:6379> lpush names zs ls ww
(integer) 3
127.0.0.1:6379> rpush names zhangsan lisi wangwu
(integer) 6
127.0.0.1:6379> lrange names 0 -1
1) "ww"
2) "ls"
3) "zs"
4) "zhangsan"
5) "lisi"
6) "wangwu"
127.0.0.1:6379> clear
127.0.0.1:6379> lrange names 0 -1
1) "ww"
2) "ls"
3) "zs"
4) "zhangsan"
5) "lisi"
6) "wangwu"
127.0.0.1:6379> lrange names 0 3
1) "ww"
2) "ls"
3) "zs"
4) "zhangsan"
127.0.0.1:6379> ltrim names 0 2
OK
127.0.0.1:6379> lrange names 0 -1
1) "ww"
2) "ls"
3) "zs"
127.0.0.1:6379> lset names 1 QQ
OK
127.0.0.1:6379> lrange names 0 -1
1) "ww"
2) "QQ"
3) "zs"
127.0.0.1:6379> linsert names before QQ WW
(integer) 4
127.0.0.1:6379> lrange names
(error) ERR wrong number of arguments for 'lrange' command
127.0.0.1:6379> lrange names 0 -1
1) "ww"
2) "WW"
3) "QQ"
4) "zs"
127.0.0.1:6379> linsert names after  QQ PP
(integer) 5
127.0.0.1:6379> lrange names 0 -1
1) "ww"
2) "WW"
3) "QQ"
4) "PP"
5) "zs"
```





### Set

list元素可以重复，set不能重复;set是无序的,list是有序的。

sadd set h1 h2 h3



srandmember set 1 :随机获取1个元素（不删除）



spop set 1 :随机获取1个元素（删除）

交集：

​	sinter set set2

差集：

 sdiff set set2

并集：

​	 sunion set set2

### SortedSet

解决set因无序 而无法遍历的问题：sortedset增加了一个score字段。



```
127.0.0.1:6379> zadd persons  8 zs 9 ls 10 ww
(integer) 3
127.0.0.1:6379> zrange persons 1 2
1) "ls"
2) "ww"
127.0.0.1:6379> zrange persons 1 2 withscores
1) "ls"
2) "9"
3) "ww"
4) "10"
127.0.0.1:6379> zrerange persons 0 2
(error) ERR unknown command `zrerange`, with args beginning with: `persons`, `0`, `2`, 
127.0.0.1:6379> zrevrange persons 0 2
1) "ww"
2) "ls"
3) "zs"
127.0.0.1:6379> zrevrange persons 0 2 withscores
1) "ww"
2) "10"
3) "ls"
4) "9"
5) "zs"
6) "8"
127.0.0.1:6379> zrangebyscore persons 8 9
1) "zs"
2) "ls"
127.0.0.1:6379> zrangebyscore persons (8  (10
1) "ls"
127.0.0.1:6379> zrangebyscore persons 8 10 withscores limit 1 1 
1) "ls"
2) "9"
127.0.0.1:6379> zrangebyscore persons 8 10 withscores limit 1 2
1) "ls"
2) "9"
3) "ww"
4) "10"
127.0.0.1:6379> zrem person ls
(integer) 0
127.0.0.1:6379> zrem persons ls
(integer) 1
127.0.0.1:6379> zcount person  8 10
(integer) 0
127.0.0.1:6379> zcount persons  8 10
(integer) 2
127.0.0.1:6379> zrank persons zs
(integer) 0
127.0.0.1:6379> zrank persons ww
(integer) 1
127.0.0.1:6379> zscore persons ww
"10"
127.0.0.1:6379> zscore persons zs
```



### Hash

person(name,age)

person.setName("zs")  :  

hset  p name zs

hmset p name ls age 23



```
127.0.0.1:6379> hset  p name zs
(integer) 1
127.0.0.1:6379> hmset p name ls age 23
OK
127.0.0.1:6379> hget p name
"ls"
127.0.0.1:6379> hmget p name age
1) "ls"
2) "23"
127.0.0.1:6379> hkeys p
1) "name"
2) "age"
127.0.0.1:6379> hvals p
1) "ls"
2) "23"
127.0.0.1:6379> hgetall p
1) "name"
2) "ls"
3) "age"
4) "23"
127.0.0.1:6379> hdel p name age
(integer) 2
127.0.0.1:6379> hincrby p age 1.5
(error) ERR value is not an integer or out of range
127.0.0.1:6379> hincrby p age 1
(integer) 1
127.0.0.1:6379> hgetall p
1) "age"
2) "1"
127.0.0.1:6379> hincrby p age 10
(integer) 11
127.0.0.1:6379> hget all p
(nil)
127.0.0.1:6379> hgetall p
1) "age"
2) "11"
127.0.0.1:6379> hincrybyfloat p age 1.5
(error) ERR unknown command `hincrybyfloat`, with args beginning with: `p`, `age`, `1.5`, 
127.0.0.1:6379> hincrbyfloat p age 1.5
"12.5"
127.0.0.1:6379> hdecrbyfloat p age 1.5
(error) ERR unknown command `hdecrbyfloat`, with args beginning with: `p`, `age`, `1.5`, 
127.0.0.1:6379> hincrbyfloat p age -1.5
"11"
```



### key及其他操作

expire k1

pexpire k1

keys *

keys k?

type k1

flushdb:清空当前数据库  select 1

flushall：清空全部数据(慎用)



### Java 操作Redis：  Jedis

java - Jedis - Redis

配置步骤：

1.设置redis服务器地址

redis.conf中配置服务器的地址：  bind 192.168.2.130  127.0.0.1  (注意，在第二个bind中配置)

2.引入jedis依赖

（1）直接引入jedis-2.9.1.jar

  (2)通过maven引入

3.使用

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Created by 颜群
 */
public class JedisDemo {
    public static void main(String[] args) throws  Exception {
    //java - redis
        Jedis jedis = new Jedis("192.168.2.130",6379) ;


        System.out.println(  jedis.ping( ) );
        //String
        jedis.select(0) ;
        jedis.flushDB() ;
        jedis.set("kj1","vj1234567");
        jedis.setnx("kj2","vj2");
        String r = jedis.getrange("kj1",2,4) ;
        String v = jedis.get("kj1");
        System.out.println(v);
        System.out.println(r);

        //数字
        jedis.set("n","1") ;
        jedis.incr("n")     ;
        jedis.incr("n")     ;
        jedis.incr("n")     ;
        jedis.decr("n") ;
        jedis.incrBy("n",10) ;
        jedis.decrBy("n",3) ;
        System.out.println(  jedis.get("n")   );

        //key
        Boolean f = jedis.exists("n");
        System.out.println(f);
        String type = jedis.type("n");
        System.out.println(f);
        System.out.println(type);
        jedis.expire("n",100) ;

//        Thread.sleep(3000);
        System.out.println(  jedis.ttl("n") );

        jedis.del("n","kj1") ;

        //list
        jedis.lpush("list1","a","b","c") ;
//        jedis.lpush("list1","hello","world") ;
        System.out.println("----");
        jedis.lpush("list2","listv1","listv2","list2v3") ;
        jedis.lset("list1",1,"QQ") ;//"v1","QQ","v3"


//        jedis.lrem("list1",2,"v1") ;
//        jedis.ltrim( "list2", 0,1  );//只保留第0、第1个元素，其他全部删除

        List<String> list1 = jedis.lrange("list1", 0, -1);
        for(String e :list1){
            System.out.println(e);
        }

        System.out.println("----");
        List<String> list2 = jedis.lrange("list2", 0, 1);
        for(String e :list2){
            System.out.println(e);
        }

        //hash
        jedis.hset("person","name","zs");
        jedis.hset("person","age","23");

        Map<String, String> person = jedis.hgetAll("person");
        System.out.println(person);

        Map<String,String> map = new HashMap<>() ;
        map.put("id","1");
        map.put("name","lisi");

        jedis.hset("people" ,map) ;
        Map<String, String> result = jedis.hgetAll("people");
        System.out.println(result);

        Set<String> keys = jedis.hkeys("people");
        List<String> values = jedis.hvals("people") ;
        System.out.println("keys:"+keys);
        System.out.println("values:"+values);

        //set
        jedis.sadd("skey","sv1","sv2") ;
        Long count = jedis.scard("skey");
        System.out.println(count);
        System.out.println(  jedis.sismember("skey","sv1"));

       //sortedset
        jedis.zadd("zset" , 10,"zv1") ;
        jedis.zadd("zset" , 9,"zv2") ;

        Map<String,Double> map2 = new HashMap<>() ;
        map2.put("zv3",8.8) ;
        map2.put("zv4",9.9) ;
        jedis.zadd("zset",map2) ;
        Set<String> zset = jedis.zrange("zset", 0, -1);
        System.out.println(zset);

        Set<Tuple> zset1 = jedis.zrangeByScoreWithScores("zset", 9, 10);
        System.out.println(zset1);


    }
}

```

### 爬虫获取海量数据 、 Redis基本操作

redis（key-value） - 关系型数据库 （二维表）

##### redis和关系型数据的数据互通方式

![1588642791959](C:\Users\admin\Desktop\JavaCore-master\JavaCore-master\notes\微服务\NOSQL\nosql.assets\1588642791959.png)



准备工作：

引入json相关Jar

jackson-core-2.9.6.jar

jackson-databind-2.9.6.jar

jackson-annotations-2.9.6.jar



网站数据->下载->解析->存储到nosql（redis）

引入依赖

```

        <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.9.6</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.6</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.6</version>
        </dependency>


        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>3.1.0</version>
        </dependency>

```

json工具类

```
package com.yanqun.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanqun.entity.Course;

/*
 * Created by 颜群
 */
public class JsonUtils {
    static ObjectMapper objectMapper = new ObjectMapper();

    //json对象 - 对象(一行记录)

    //json对象(字符串形式) -> 对象
    public static<T> T json2Object(String json ,Class<T> valueType) throws Exception{
        return objectMapper.readValue(json, valueType);
    }


    // 对象 -> json对象(字符串形式)
    public static String  object2json(Object value) throws Exception{
        return objectMapper.writeValueAsString( value ) ;
    }

    public static void main(String[] args)throws Exception {
        //演示json对象(字符串形式) -> 对象
         String json = "{\"name\":\"java\",\"num\":\"30\",\"imgPath\":\"sxxx\"  }" ;
        Course course = JsonUtils.json2Object(json, Course.class);
        System.out.println(course);

    }

}

```





```
    public static void writeRedis( List<Course> courses){
        try {
            for (Course course : courses) {
                //使用uuid模拟课程id
                jedis.hset("course", String.valueOf(UUID.randomUUID()  ), JsonUtils.object2json(course));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        //获取数据
        String html = getData() ;
		// System.out.println(html);

        //解析数据
        List<Course> courses = parseData(html);
        //写入redis
        writeRedis(courses);



        System.out.println(courses);

        //存储数据
        //使用Jdbc、框架技术 进行存储
    }

    public static void testReadRedis(){
        String course = jedis.hget("course", "3ab99b0e-1c4b-488b-9ac7-65eda44136a0");
        System.out.println(course);
    }
```

## MongoDB 

分布式的、面向文档的NoSQL数据库

| mongodb             |        |
| ------------------- | ------ |
| 文档(document)      | 行     |
| 集合(collections)   | 表     |
| 数据库（databases） | 数据库 |

#### 安装配置

下载（windows）mongodb-win32-x86_64-2008plus-ssl-v3.4-latest-signed.exe

将 D:\MongoDB\Server\3.4\bin配置到path中

启动服务：mongod --dbpath=已存在的路径

* 默认端口号27017

* 另开一个cmd窗口登录mongo

* 语法

  bson ,类似json

* 创建数据库:  use mg

#### 插入数据

~~~mangodb
db.集合.insert(bson格式数据)
~~~

db.person.insert( {id:"1",name:"zhangsan",age: NumberInt(23)}     )

db.person.insert( {id:"2",name:"zs",age: NumberInt(24)}     )

db.person.insert( {id:"3",name:"zs",age: NumberInt(25)}     )

> 默认插入字符串，要想插入数字，用自带的转换函数NumberInt()

#### 查询数据

db.person.find()  --查询全部

db.person.find( {   id:"2"})     --可以任何条数

db.person.findOne( {   id:"2"})     --仅仅有一条数据 

db.person.find() .limit(2)

#### 修改数据

db.集合.update(条件,修改后的数据)

方式一：会舍去其他字段

db.person.update( {id:"2"}, {name:"wangwu"}   )

方式二：保留其他字段

db.person.update( {id:"3"},        {$set:   {name:"LISI"} }                            )



删除：

db.集合.remove(条件)

删除全部：db.集合.remove({})

根据条件删除：db.person.remove({name:"wangwu"})



统计：

db.person.count() --查询全部的数量

db.person.count({name:"zs"}) --查询name=zs的数量

模糊查询：正则表达式

db.person.find(   { name : /^z/ })



条件查询：

```
>: $gt
>=:  $gte
<:  &lt
<= :&lte
!= :  &ne
```

db.person.find({age: {$gte:24}})

$in  / $nin 

db.person.find(    {age :  {  $in :[ 23,24]  }})



where  ..  and .... or ...

$and /  $or  

  $or:  [ {},{}  ]

find({})

db.person.find( {  $or: [    {age:{&gte:25}},  {age:{&lte:23}}   ]    } ) 



示例

db.person.find({ 	$or: [  		 {age:{$gte:25}},  {age:{$lte:23}} 	     ]

})

#### java mongodb API

依赖

mongodb-driver

```
<!-- https://mvnrepository.com/artifact/org.mongodb/mongodb-driver -->
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver</artifactId>
    <version>3.8.2</version>
</dependency>
```

爬虫数据->mongodb

将爬虫数据写入mongodb

```java
  //将爬虫数据 存入mongodb
    //数据库：crawler
    //集合：course
    public static void writeMongodb( List<Course> courses){
        MongoDatabase database = mongoClient.getDatabase("crawler");
        //数据库->集合->文档
        MongoCollection<org.bson.Document> course = database.getCollection("course");


        for(Course c:courses){
            //c ->document
            Map<String,Object> map = new HashMap<>() ;
            map.put( "name", c.getName() );
            map.put("num",c.getNum());
            map.put( "img", c.getImgPath());
            org.bson.Document documet = new org.bson.Document(map) ;
            course.insertOne(documet);
        }
        mongoClient.close();
    }
```

从mongodb中读取数据

```java
    public static void testReadMongodb(){
        MongoDatabase crawler = mongoClient.getDatabase("crawler");
        MongoCollection<org.bson.Document> course = crawler.getCollection("course");

        BasicDBObject bson = new BasicDBObject("num", new BasicDBObject("$gte",20)  ) ;
        FindIterable<org.bson.Document> documents = course.find(bson);
        for(org.bson.Document  d: documents){
            System.out.println("课程名:"+d.getString("name"));
            System.out.println("课程数量:"+d.getInteger("num"));
        }

    }
```


