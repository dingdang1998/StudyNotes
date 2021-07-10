# jar包

spring-aop.jar
spring-bean.jar
spring-context.jar
spring-core.jar
spring-web.jar

spring-webmvc.jar
commons-logging.jar

# 第一个SpringMVC程序

1、springmvc配置文件：springmvc.xml

2、选中常用的命名空间：beans  aop context  mvc

3、配置一个 Springmvc自带的servlet

通过以下配置，拦截所有请求，交给SpringMVC处理

```java
<servlet>
  	<servlet-name>springDispatcherServlet</servlet-name>
  	<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  	<init-param>
  			<param-name>contextConfigLocation</param-name>
  			<param-value>classpath:springmvc.xml</param-value>
  	</init-param>
  	<load-on-startup>1</load-on-startup>
  </servlet>
  
  <servlet-mapping>
  	<servlet-name>springDispatcherServlet</servlet-name>
  	<url-pattern>/</url-pattern>
 </servlet-mapping>
```

* 映射是去匹配@RequestMapping注解，可以和方法名、类名不一致
  * 可以设置校验请求的请求头`headers`
* 通过method指定 请求方式（get  post  delete put）

```java
@RequestMapping(value="welcome",method=RequestMethod.POST)//映射
```

# ant风格的请求路径

1、?  单字符

2、*任意个字符（0或多个）

3、** 任意目录

# REST风格

软件编程风格

**1、在SpringMvc中**

GET：查

POST：增

DELETE：删

PUT：改

>特殊情况：
>
>普通浏览器 只支持get post方式 ；其他请求方式 如 delelte|put请求是通过 过滤器新加入的支持

**2、过滤器中处理put|delete请求的部分源码**

```java
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletRequest requestToUse = request;

		if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {
			String paramValue = request.getParameter(this.methodParam);
			if (StringUtils.hasLength(paramValue)) {
				requestToUse = new HttpMethodRequestWrapper(request, paramValue);
			}
		}

		filterChain.doFilter(requestToUse, response);
	}
```

> 原始请求：request，改请求默认只支持get post  header
> 但是如果 是"POST"  并且有隐藏域<input type="hidden"  name="_method" value="DELETE"/>则，过滤器 将原始的请求 request加入新的请求方式DELETE，并将原始请求 转为 requestToUse 请求（request+Delete请求）
> 最后将requestToUse 放入 请求链中， 后续再事情request时  实际就使用改造后的 requestToUse

```java
@RequestParam("uname") String name,@RequestParam(value="uage",required=false,defaultValue="23")
```

required=false：该属性不是必须的

defaultValue="23"：默认值23

* **获取请求头信息 @RequestHeader**

```java
public String  testRequestHeader(@RequestHeader("Accept-Language")  String al  ) {}
```

> 通过@RequestHeader("Accept-Language")  String al   获取请求头中的Accept-Language值，并将值保存再al变量中 

* **通过mvc获取cookie值（JSESSIONID）**

@CookieValue 

> 前置知识： 服务端在接受客户端第一次请求时，会给该客户端分配一个session （该session包含一个sessionId）),并且服务端会在第一次响应客户端时，请该sessionId赋值给JSESSIONID 并传递给客户端的cookie中

```java
@RequestMapping(value="testCookieValue")
		public String  testCookieValue(@CookieValue("JSESSIONID") String jsessionId) {
			System.out.println( jsessionId);
			return "success" ;//  views/success.jsp，默认使用了 请求转发的 跳转方式
		}
```

* **使用对象（实体类）接受请求参数**
* **在SpringMVC中使用原生态的Servlet API**

HttpServletRequest ：直接将 servlet-api中的类、接口等 写在springMVC所映射的方法参数中即可

```java
@RequestMapping(value="testServletAPI")
		public String testServletAPI(HttpServletRequest  request,HttpServletResponse response) {
//			request.getParameter("uname") ;
			System.out.println(request);
			return "success" ;
}
```

# 处理模型数据

如果跳转时需要带数据（既有视图、又有数据）V、M，则可以使用以下方式：

ModelAndView、ModelMap  、Map、Model 

> 数据放在了request作用域 

## ModelAndView

```java
@RequestMapping(value="testModelAndView")
public ModelAndView testModelAndView() {//ModelAndView:既有数据，又有视图
			//ModelAndView:Model -M     View-V
			ModelAndView mv = new ModelAndView("success");//view:  views/success.jsp 
			
			Student student = new Student() ;
			student.setId(2);
			student.setName("zs");
			
			mv.addObject("student", student);//相当于request.setAttribute("student", student);
			return mv;
}
```

## ModelMap 

## Map

## Model 

## 将数据放入session中

@SessionAttributes(..)

## @ModelAttribute 

i.经常在 更新时使用
ii.在不改变原有代码的基础之上，插入一个新方法

通过@ModelAttribute修饰的方法 ，会在每次请求前先执行

```java
@ModelAttribute//在任何一次请求前，都会先执行@ModelAttribute修饰的方法
public void queryStudentById(Map<String,Object> map) {
			//StuentService stuService = new StudentServiceImpl();
			//Student student = stuService.queryStudentById(31);
			//模拟调用三层查询数据库的操作
			Student student = new Student();
			student.setId(31);
			student.setName("zs");
			student.setAge(23);
			map.put("stu", student) ;//约定：map的key 就是方法参数 类型的首字母小写
		}

//修改:Zs-ls
@RequestMapping(value="testModelAttribute")
public String testModelAttribute(@ModelAttribute("stu")Student student) {
			student.setName(student.getName());//将名字修改为ls
			System.out.println(student.getId()+","+student.getName()+","+student.getAge());
			return "success";
}
```

> 注意：
>
> @ModelAttribute会在 该类的每个方法执行前 均被执行一次，因此使用时需要注意

# 视图、视图解析器

视图的顶级接口：View
视图解析器顶级接口：ViewResolver

* 常见的视图和解析器

InternalResourceView、InternalResourceViewResolver

* JstlView可以解析jstl\实现国际化操作

springMVC解析jsp时 会默认使用InternalResourceView， 如果发现Jsp中包含了jstl语言相关的内容，则自动转为JstlView

国际化： 针对不同地区、不同国家 ，进行不同的显示 

```java
public class JstlView extends InternalResourceView
```

## InternalResourceViewResolver其他功能

### <mvc:view-controller ...>

用SpringMVC实现：index.jsp -> succes.jsp

### 指定请求方式

> forward:   redirect: ，需要注意此种方式，不会被视图解析器加上前缀(/views)、后缀(.jsp)

### 处理静态资源：html css js  图片 视频

### 类型转换

**a.Spring自带一些 常见的类型转换器**

```java
//即可以接受int类型数据id  也可以接受String类型的id
public String  testDelete(@PathVariable("id") String id)
```

**b.可以自定义类型转换器**

1、编写自定义类型转器的类（实现Converter接口）

```java
public class MyConverter  implements Converter<String,Student>{

	@Override
	public Student convert(String source) {//source:2-zs-23
		//source接受前端传来的String:2-zs-23
		String[] studentStrArr = source.split("-") ;
		Student student = new Student();
		student.setId(  Integer.parseInt(  studentStrArr[0]) );
		student.setName(studentStrArr[1]);
		student.setAge(Integer.parseInt(studentStrArr[2] ));
		return student;
	}
}
```

2、配置：将MyConverter加入到springmvc中

a、将自定义转换器 纳入SpringIOC容器

```java
<bean  id="myConverter" class="org.lanqiao.converter.MyConverter"></bean>
```

b、将myConverter再纳入SpringMVC提供的转换器Bean

```java
<bean id="conversionService"  class="org.springframework.context.support.ConversionServiceFactoryBean">
		<property name="converters">
			<set>
				<ref bean="myConverter"/>
			</set>
		</property>
</bean>
```

c、将conversionService注册到annotation-driven中

```java
<!--此配置是SpringMVC的基础配置，很功能都需要通过该注解来协调  -->
<mvc:annotation-driven conversion-service="conversionService"></mvc:annotation-driven>
```

3、测试转换器

```java
@RequestMapping(value="testConverter")
public String testConverter(@RequestParam("studentInfo")  Student student) {// 前端：2-zs-23  
			
			System.out.println(student.getId()+","+student.getName()+","+student.getAge());
			
			return "success";
}
```

> 其中@RequestParam("studentInfo")是触发转换器的桥梁；
> @RequestParam("studentInfo")接受的数据 是前端传递过来的：2-zs-23  ，但是需要将该数据 复制给 修饰的目的对象Student；因此SPringMVC可以发现 接收的数据 和目标数据不一致，并且这两种数据分别是 String、Student,正好符合public Student convert(String source)转换器

# 数据格式化

* **配置**

```java
<!-- 配置 数据格式化 注解 所依赖的bean -->
<bean id="conversionService" class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
</bean>
```

* **通过注解使用**

@DateTimeFormat(pattern="yyyy-MM-dd")

@NumberFormat(parttern="###,#")  

# 错误消息

```java
public String testDateTimeFormat(Student student, BindingResult result ,Map<String,Object> map) {
```

需要验证的数据是 Student中的birthday, SpringMVC要求如果校验失败则将错误信息自动放入该对象之后紧挨着的BindingResult中，即Student student, BindingResult result之间不能有其他参数

如果要将控制台的错误消息传到jsp中显示，则可以将错误消息对象放入request域中，然后在jsp中从request中获取

# 数据校验

* 常用第三方校验

JSR303 
Hibernate Validator

* 使用Hibernate Validator步骤

1、引入jar包（注意各个jar之间可能存在版本不兼容）、

hibernate-validator-5.0.0.CR2.jar 	classmate-0.8.0.jar 	jboss-logging-3.1.1.GA.jar
validation-api-1.1.0.CR1.jar 	hibernate-validator-annotation-processor-5.0.0.CR2.jar

2、配置

```java
<mvc:annotation-driven ></mvc:annotation-driven>
```

> 此时mvc:annotation-driven的作用：要实现Hibernate Validator/JSR303 校验（或者其他各种校验），必须实现SpringMVC提供的一个接口：ValidatorFactory
>
> LocalValidatorFactoryBean是ValidatorFactory的一个实现类
> <mvc:annotation-driven ></mvc:annotation-driven>会在springmvc容器中 自动加载一个LocalValidatorFactoryBean类，因此可以直接实现数据校验

3、直接使用注解

```java
public class Student {

	@Past//当前时间以前
	private Date birthday ;
}
```

在校验的Controller中 ，给校验的对象前增加 @Valid

```java
public String testDateTimeFormat(@Valid Student student, BindingResult result ,Map<String,Object> map) {
			{...}
```

# Ajax请求SpringMVC，并且JSON格式的数据返回

@ResponseBody修饰的方法，会将该方法的返回值 以一个json数组的形式返回给前台

# 实现文件上传

和Servlet方式的本质一样，都是通过commons-fileupload.jar和commons-io.jar

SpringMVC可以简化文件上传的代码，但是必须满足条件：实现`MultipartResolver`接口 ；而该接口的实现类SpringMVC也已经提供了`CommonsMultipartResolver`

**实现步骤：**

1、jar包

commons-fileupload.jar、commons-io.jar

2、配置CommonsMultipartResolver

```java
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
			<property name="defaultEncoding" value="UTF-8"></property>
			<!-- 上传单个文件的最大值，单位Byte;如果-1，表示无限制 -->
			<property name="maxUploadSize"  value="102400"></property>
	</bean>
```

3、处理方法

```java
		//文件上传处理方法
		@RequestMapping(value="testUpload") //abc.png
		public String testUpload(@RequestParam("desc") String desc  , @RequestParam("file") MultipartFile file  ) throws IOException {
			
			System.out.println("文件描述信息："+desc);
			//jsp中上传的文件：file
			
			InputStream input = file.getInputStream() ;//IO
			String fileName = file.getOriginalFilename() ;
			
			OutputStream out = new FileOutputStream("d:\\"+fileName) ;
			
			
			byte[] bs = new byte[1024];
			int len = -1;
			while(( len = input.read(bs)) !=-1 ) {
				out.write(bs, 0, len);
			}
			out.close();
			input.close();
			//将file上传到服务器中的 某一个硬盘文件中
		System.out.println("上传成功！");
			
			return "success";
		}
```

# 拦截器

要想实现拦截器，必须实现一个接口`HandlerInterceptor`

1、编写拦截器

```java
public class MyInterceptor  implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("拦截请求");  //
		return true;//true:拦截操作之后，放行 ；false:拦截之后不放行，请求终止；
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("拦截响应");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("视图(jsp)被渲染完毕");
	}

}
```

2、将自己写的拦截器配置到springmvc中

```java
<!-- 将自己写的拦截器 配置到springmvc中（spring）；默认拦截全部请求 -->
	<mvc:interceptors>
		
			<!-- 配置具体的拦截路径 -->
			<mvc:interceptor>
				<!-- 指定拦截的路径,基于ant风格 -->
				<mvc:mapping path="/**"/>  
				<!-- 指定拦不截的路径 -->
				<mvc:exclude-mapping path="/handler/testUpload"/> 
				<bean  class="org.lanqiao.interceptor.MyInterceptor"></bean>
			</mvc:interceptor>
			
				<!-- 配置具体的拦截路径 -->
			<mvc:interceptor>
				<!-- 指定拦截的路径,基于ant风格 -->
				<mvc:mapping path="/**"/>  
				<!-- 指定拦不截的路径 -->
				<mvc:exclude-mapping path="/handler/testUpload"/> 
				<bean  class="org.lanqiao.interceptor.MySecondInterceptor"></bean>
			</mvc:interceptor>
</mvc:interceptors>
```

**处理时机：**

拦截器1拦截请求- 拦截器2拦截请求 - 请求方法 - 拦截器2处理相应-拦截器1处理相应-    只会被最后一个拦截器的`afterCompletion()`拦截

> 如果有多个拦截器，则每个拦截器的preHandle postHandle 都会在相应时机各被触发一次；但是afterCompletion，只会执行最后一个拦截器的该方法

# 异常处理

## 异常捕获

`HandlerExceptionResolver`接口

该接口的每个实现类都是异常的一种处理方式

1、ExceptionHandlerExceptionResolver：主要提供了@ExceptionHandler注解，并通过该注解处理异常

```java
//该方法可以捕获本类中抛出的ArithmeticException异常
@ExceptionHandler({ArithmeticException.class,ArrayIndexOutOfBoundsException.class  })
	public String handlerArithmeticException(Exception e) {
		System.out.println(e +"============");
		return "error" ;
}
```

> @ExceptionHandler标识的方法的参数必须在异常类型(Throwable或其子类) ，不能包含其他类型的参数

* **异常处理路径：最短优先**

如果有方法抛出一个ArithmeticException异常，而该类中有2个对应的异常处理方法

```java
	@ExceptionHandler({Exception.class  })
	public ModelAndView handlerArithmeticException2(Exception e) {}

	@ExceptionHandler({ArithmeticException.class  })
	public ModelAndView handlerArithmeticException1(Exception e) {}
```

则优先级：  最短优先

* @ExceptionHandler默认只能捕获当前类中的异常方法
* 如果发生异常的方法  和处理异常的方法不在同一个类中：`@ControllerAdvice`

```java
@ControllerAdvice
public class MyExceptionHandler {//不是控制器，仅仅是 用于处理异常的类
	
	@ExceptionHandler({Exception.class  })
	public ModelAndView handlerArithmeticException2(Exception e) {
		ModelAndView mv = new ModelAndView("error");
		System.out.println(e +"============"+"该@ControllerAdvice中的异常处理方法，可以处理任何类中的异常");
		mv.addObject("er", e) ;
		return  mv;
	}
	
}
```

> 总结：
>
> 如果一个方法用于处理异常，并且只处理当前类中的异常：@ExceptionHandler
> 如果一个方法用于处理异常，并且处理所有类中的异常： 类前@ControllerAdvice、 处理异常的方法前加@ExceptionHandler

## 异常处理页面

```java
@ResponseStatus(value=HttpStatus.FORBIDDEN,reason="数组越界222!!!")
public class MyArrayIndexOutofBoundsException extends Exception {//自定义异常

}
```

```java
public String testMyException(@RequestParam("i") Integer i) throws MyArrayIndexOutofBoundsException {
		if(i == 3) {
			throw new MyArrayIndexOutofBoundsException();//抛出异常
		}
		return "success" ;
	}
```

@ResponseStatus也可以标志在方法前

## 其他异常

DefaultHandlerExceptionResolver:SPringMVC在一些常见异常的基础上（300 500  405），新增了一些异常，例如：

```java
* @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
 * @see #handleNoSuchRequestHandlingMethod
 * @see #handleHttpRequestMethodNotSupported:如果springmvc的处理方法限制为post方式，如果实际请求为get,则会触发此异常显示的页面
 * @see #handleHttpMediaTypeNotSupported
 * @see #handleMissingServletRequestParameter
 * @see #handleServletRequestBindingException
 * @see #handleTypeMismatch
 * @see #handleHttpMessageNotReadable
 * @see #handleHttpMessageNotWritable
 * @see #handleMethodArgumentNotValidException
 * @see #handleMissingServletRequestParameter
 * @see #handleMissingServletRequestPartException
 * @see #handleBindException
```

## 通过配置来实现异常的处理

SimpleMappingExceptionResolver

```java
<!-- SimpleMappingExceptionResolver:以配置的方式 处理异常 -->
	<bean  class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
		<!-- 如果发生异常，异常对象会被保存在  exceptionAttribute的value值中；并且会放入request域中 ；异常变量的默认值是 exception-->
		<!--<property name="exceptionAttribute" value="exception"></property>-->
			<property name="exceptionMappings">
					<props>
						<!-- 相当于catch(ArithmeticException ex){ 跳转：error } -->
						<prop key="java.lang.ArithmeticException">
							error
						</prop>
						<prop key="java.lang.NullPointerException">
							error
						</prop>
					
					</props>
			</property>
</bean>
```

