## 一、入门及基本配置

入门的一些使用和了解，大家可以参考下面博客的内容和ureport官方网站

1、博客：https://www.cnblogs.com/niceyoo/p/14311257.html

2、官网：http://wiki.bsdn.org/pages/viewpage.action?pageId=76448364

基本使用和了解看上面两个资料大致就会对ureport有一个了解了

## 二、个人遇到的坑以及一些自定义扩展使用

在这里总结一些使用ureport2的坑和对应的解决办法，还有对原有框架自定义的扩展，之后大家遇到了什么问题以及相应的解决也可以在这里补充

### 添加内置数据源

可以直接使用直连数据源，也可以将常用的数据源配置为内置数据源，这样就不用每次像直连数据源那样进行配置了

1、ureport2提供了一个配置内置数据源的接口**BuildinDataSoure**



```java
/**
 * @author Jacky.gao
 * @since 2017年2月9日
 */
public interface BuildinDatasource {
    /**
     * @return 返回数据源名称
     */
    String name();
    /**
     * @return 返回当前采用数据源的一个连接
     */
    Connection getConnection();
}
```

接口含义比较明了，一是要配置内置数据源的名称，而是要配置一个对应数据源的链接

2、实现

这里附上代码

```java
package com.labi.ureport.datasource;
 
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.stereotype.Component;
 
import java.sql.Connection;
import java.sql.DriverManager;
 
/**
 * @program: ureport
 * @description: 232采集平台sub端内置数据源
 * @author: dzp
 * @create: 2021-09-09 10:31
 **/
@Component
public class ConsoleSubDatasource implements BuildinDatasource {
 
    private static String driver;
    private static String url;
    private static String user;
    private static String pwd;
 
    static {
        driver = "com.mysql.cj.jdbc.Driver";
        url = "jdbc:mysql://192.168.0.232:3306/console_sub?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&serverTimezone=GMT%2B8";
        user = "root";
        pwd = "123456";
    }
 
    /**
     * 内置数据源名称
     *
     * @return
     */
    @Override
    public String name() {
        return "ConsoleSubDatasource";
    }
 
    /**
     * 返回当前采用的数据库连接
     *
     * @return
     */
    @Override
    public Connection getConnection() {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, pwd);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

将实现类注册成bean之后，ureport2会自动监测改内置数据源，效果如下：

![image2021-9-9_14-44-43](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_14-44-43.png)

### 自定义时间格式化函数

ureport中自带了时间格式化函数formatdate，官方对这个函数的介绍如下：

![image2021-9-9_16-8-43](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_16-8-43.png)

但是，我在使用这个函数时，出现了问题，用此函数格式化【232sub端数据库中表s_dc_inventory_c_2021_09的TIME字段】

![image2021-9-9_16-11-33](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_16-11-33.png)

点击预览，出现报错：

![image2021-9-9_16-12-56](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_16-12-56.png)

在源码中查找该报错的来源：

![image2021-9-9_16-14-45](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_16-14-45.png)

发现数据格式无法转化为java.until.Date类型，这个时间格式对应的是java.time.LocalDateTime，这种情况只能自己定义实现格式化时间函数了，继续查看源码

![image2021-9-9_18-25-32](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_18-25-32.png)

![image2021-9-9_18-26-15](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_18-26-15.png)

由源码可知，框架会将程序里面所有Function放到这个functions中，并根据使用的函数名找到对应的实现类，那么只要仿照原有的function写一个类实现Function接口，重写里面的方法实现我们自己的逻辑就可以了

```java
package com.labi.ureport.function;
 
 
import com.bstek.ureport.build.Context;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.expression.function.Function;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.model.Cell;
import org.springframework.stereotype.Component;
 
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
 
/**
 * @program: ureport
 * @description: 自定义时间格式化函数，专门用来格式化LocalDateTime
 * @author: dzp
 * @create: 2021-09-09 10:06
 **/
@Component
public class FormatLocalDateTimeFunction implements Function {
    /**
     * 默认格式化
     */
    private final String defaultPattern = "yyyy-MM-dd HH:mm:ss";
 
    @Override
    public Object execute(List<ExpressionData<?>> dataList, Context context, Cell currentCell) {
        if (dataList == null) {
            return "";
        }
        Object obj = null;
        String pattern = defaultPattern;
        if (dataList.size() > 1) {
            obj = dataList.get(0).getData();
            pattern = (String) dataList.get(1).getData();
        }
        if (dataList.size() > 0) {
            obj = dataList.get(0).getData();
        }
        if (obj == null) {
            throw new ReportComputeException("Function [formatdate] need a LocalDateTime type parameter at least");
        } else {
            if (obj instanceof LocalDateTime) {
                Date from = Date.from(((LocalDateTime) obj).atZone(ZoneId.systemDefault()).toInstant());
                SimpleDateFormat sd = new SimpleDateFormat(pattern);
                return sd.format(from);
            } else {
                throw new ReportComputeException("Function [formatdate] first parameter is LocalDateTime type");
            }
        }
    }
 
    /**
     * 函数名称
     *
     * @return
     */
    @Override
    public String name() {
        return "formatLocalDateTime";
    }
}
```

> 注意：我这里没有照抄按照框架中提供的【FormatDateFunction】中excute()方法的实现，而是参考着写了自己的逻辑，因为如果完全照抄，只修改格式化时间的那个地方，你只能将时间格式化成默认的格式，无法动态的根据前端传来的格式化形式将日期数据格式化，具体原因可以自己实现一下debug看一下就明白了

之后，就可以使用自己定义的函数了

![image2021-9-9_19-1-37](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_19-1-37.png)

预览查看格式化效果

![image2021-9-9_19-2-50](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_19-2-50.png)

也可以动态根据传入的表达式格式化

![image2021-9-9_19-8-14](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_19-8-14.png)

查看效果：

![image2021-9-9_19-8-47](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-9_19-8-47.png)

### Ureport不支持mysql中日期类型为datetime的数据库字段

我们选择导出数据表中类型为datetime的TIME字段的数据和ID

![image2021-9-10_9-41-24](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-41-24.png)

查看预览结果，预览结果是能显示的：

![image2021-9-10_9-45-44](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-45-44.png)

1、导成word，无法显示不支持

![image2021-9-10_9-47-3](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-47-3.png)

2、后面我也试了导成Excel，也是同样的无法显示

3、导成PDF，这个是可以的

![image2021-9-10_9-48-34](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-48-34.png)

可以看到，如果是将datetime类型的数据库字段导成文本形式的文件，是无法显示的，但是导成pdf是没问题的，如果想把该字段数据导成文档形式，可以如下操作：

在添加数据集的时候，将datetime字段格式化一下

![image2021-9-10_9-56-51](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-56-51.png)

![image2021-9-10_9-57-18](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-57-18.png)

选择格式化好的字段，导出即可

![image2021-9-10_9-58-24](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-58-24.png)

![image2021-9-10_9-58-54](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_9-58-54.png)

如上操作就可以了

> 总结：结合使用ureport2自带的时间格式化函数formatdate不能格式化datitime字段来看，ureport2是不支持对datetime类型的mysql数据库字段的任何操作的，如果想操作自己数据库中类型为datetime字段的数据，需要结合特定的情况进行处理

官方文档中对日期参数类型的要求：

![image2021-9-10_13-58-45](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_13-58-45.png)

### 自定义报表文件存储位置

默认存储路径这里就不说了，可以看官方文档里面的介绍，这里主要说一下如何自定义报表文件存储位置，项目部署时还是用我们自定的存储路径会好一点

当前项目是用springboot整合ureport2

1、在resource下创建一个bean配置文件

![image2021-9-10_11-43-45](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_11-43-45.png)

内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
 
    <!--导入ureport配置文件。这里如果不导入这个配置文件的话，下方parent属性不会匹配出来-->
    <import resource="classpath:ureport-console-context.xml"/>
 
    <!--修改了ureport默认配置时，需配置修改后的文件信息-->
    <bean id="propertyConfigurer" parent="ureport.props">
        <!-- 读取配置文件 -->
        <property name="location" value="classpath:application.properties"/>
    </bean>
</beans>
```

2、在application.properties中配置两个属性

![image2021-9-10_11-45-40](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_11-45-40.png)

spring的那个配置要加一下，不加的话会提示报错

3、在启动类中加载一下context.xml文件

![image2021-9-10_11-47-19](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_11-47-19.png)

经过这三个配置，就能将报表文件默认的存储路径改成我们自定义的，效果如下：

![image2021-9-10_11-48-54](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_11-48-54.png)

![image2021-9-10_11-49-39](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_11-49-39.png)

### 实际场景的案列

#### 1、查看所有站所有罐整点罐存数据是否正常，vt>60000为正常，否则为异常

（1）数据集，先把时间格式化

![image2021-9-10_15-20-16](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_15-20-16.png)

（2）

![image2021-9-10_15-40-26](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_15-40-26.png)

效果预览：

![image2021-9-10_15-40-11](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_15-40-11.png)

#### 2、在1、的前提下再增加一列，展示当前罐存与上一条罐存的差值

这里涉及到单元格坐标的使用

（1）这里注意要将C2的聚合方式改为列表方式

![image2021-9-10_16-26-15](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_16-26-15.png)

效果预览：

![image2021-9-10_16-29-42](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_16-29-42.png)

#### 3、每个罐每天的日均罐温，并且保留两位小数

![image2021-9-10_17-25-12](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_17-25-12.png)

效果预览：

![image2021-9-10_17-35-20](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-10_17-35-20.png)



还可以有更简单的写法：

![image2021-9-13_13-51-22](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-13_13-51-22.png)

#### 3、在2的前提下，将大于25的平均值背景颜色变为红色

这时候需要利用条件属性这个功能

1、选中当前单元格，添加条件属性即可

![image2021-9-13_14-12-40](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-13_14-12-40.png)

2、效果预览

![image2021-9-13_14-13-48](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-13_14-13-48.png)



#### 4、根据传入的油站编码展示该站对应的数据（如果不输入全查，如果输入某个站的，则展示对应油站的数据）

1、在数据集的地方采用表达式的方式，根据传入的参数返回不同的sql表达式

![image2021-9-13_15-5-34](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-13_15-5-34.png)

表达式如下：

```javascript
${
if(param("stationCode")==null || param("stationCode")==''){
        return "select *,date_format(TIME,\'%Y-%m-%d\') as timeformate from s_dc_inventory_c_2021_09"
}else{
    return "select *,date_format(TIME,\'%Y-%m-%d\') as timeformate from s_dc_inventory_c_2021_09 where STATION_CODE=:stationCode"
}
   
}
```

> 注意：如果你的sql中有‘’包含的表达式，需要插入"\"对单引号进行转义 



2、只需要在预览数据的URL后面动态的传入参数及其对应的值就可以了

（1）只展示stationCode为wdlt的数据

![image2021-9-13_15-8-38](C:\Users\admin\Desktop\JavaCore-master\StudyNotes\ureport\图片\image2021-9-13_15-8-38.png)

（2）URL后面不追加参数，则全部展示

### 与具体业务相结合

罐方给出的与业务结合的案例是用jsp实现的，而我们目前开发是采用前后端分离的方式，我们自己写导出是前端请求接口，后端将生成的文件返回，利用这种思路，结合一下ureport2提供的api就可以了

这里大家可以先提前看一下官方文档的第九章，然后再看我下面贴出来的代码，就比较好理解了

1、前端传值和后端封装

~~~java
package com.labi.ureport.controller;
 
import com.bstek.ureport.export.ExportConfigure;
import com.bstek.ureport.export.ExportConfigureImpl;
import com.bstek.ureport.export.ExportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @program: ureport
 * @description:
 * @author: dzp
 * @create: 2021-09-13 15:20
 **/
@RestController
@RequestMapping(value = "/export")
public class ExportController {
 
    @Autowired
    private ExportManager exportManager;
 
    @GetMapping("/get")
    public ResponseEntity<byte[]> getPage(@RequestParam(value = "file", required = false) String file,
                                          @RequestParam(value = "stationCode", required = false) String stationCode) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stationCode", stationCode);
        ExportConfigure exportConfigure = new ExportConfigureImpl(file, parameters, new ByteArrayOutputStream());
        exportManager.exportExcel(exportConfigure);
        //输出
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode("test.xls", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) exportConfigure.getOutputStream();
        return new ResponseEntity<byte[]>(outputStream.toByteArray(), headers, HttpStatus.CREATED);
    }
}
~~~

