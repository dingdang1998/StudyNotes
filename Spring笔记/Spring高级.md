# SpringIoc容器

两种形式

* xml配置文件

applicationContext.xml

**存bean**

~~~xml
<bean id class>
~~~

**取bean**

```java
ApplicationContext context= new ClassPathXmlApplicationContext("applicationContext.xml");
		context.getBean();
```

* 注解

带有@Configuration注解的类（配置类）

**存bean**

```xml
xxx
```

**取bean**

```java
ApplicationContext context  = new AnnotationConfigApplicationContext(MyConfig.class) ;
```

> 注意：两种形式获取的Ioc容器是独立的

## XXX：注解形式给IoC容器中存放Bean

* 必须有@Configuration注解（配置类）
* 形式

### 三层组件

a.给三层组件分别加注解（@Controller、@Service、@Repository）

b.将注解所在包纳入ioc扫描器（ComponentScan）

​	纳入ioc扫描器的两种方式：

​		1、xml配置文件

```java
<context:component-scan base-package="com.yanqun.controller">
```

​		2、注解

```java
//只对三层组件注解
@component-scan
```

**给扫描器指定规则：**

过滤类型：FilterType(ANNOTATION，ASSIGNABLE_TYPE，CUSTOM)

excludeFilters（排除）

includeFilters（包含）：有默认行为，可以通过useDefaultFilters = false禁止

* ANNOTATION：三层注解类型
* ASSIGNABLE_TYPE：具体的类(StudentService.class)

> 区分：
>
> ANNOTATION:Controller.clss 指的是所有标有@Controller的类
>
> ASSIGNABLE_TYPE：指的是具体的一个类StudentController.class

* CUSTOM自定义：自己定义包含规则

```java
@ComponentScan.Filter(type= FilterType.CUSTOM ,value={MyFilter.class}
```

**实现：**

MyFilter implements TypeFilter重写其中的match，如果return true则加入IoC容器

```java
//自定义筛选
public class MyFilter  implements TypeFilter {

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        //能拿到扫描包下所有标有三层注解的类
        String className = annotationMetadata.getClassName();
        if(className.contains("School"))
            return true ;
        return false;
    }
}
```

### 非三层组件

#### @Bean+方法的返回值 

id默认就是方法名（可以通过@Bean("stu") 修改id值）

#### import使用

1、直接编写到@Import注解中，并且id值是全类名

```java
@Import({Apple.class,Banana.class})
```

2、自定义ImportSelector接口的实现类，通过selectimports方法实现（方法的返回值就

是要纳入IoC容器的Bean） 并且告知程序自己编写的实现类

```java
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        return new String[]{"com.yanqun.entity.Apple","com.yanqun.entity.Banana"}; //返回值就是 要加入IOC容器的Bean的全类名
    }
}
```

```java
@Import({Orange.class,MyImportSelector.class})
```

3、编写ImportBeanDefinitionRegistrar接口的实现类，重写方法

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition =  new RootBeanDefinition("com.yanqun.entity.Orange") ;
        // id ,class
        registry.registerBeanDefinition("myorange", beanDefinition ); 
    }
}
```

#### FactoryBean(工厂Bean)

1、准备bean，实现类和重写方法

```java
public class MyFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        return new Apple();
    }

    @Override
    public Class<?> getObjectType() {
        return Apple.class;  //Apple
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
```

2、注册bean，注册到@Bean中

> 需要通过&区分 获取的对象是哪一个：
>
> 不加&,获取的是最内部真实的Apple；
>
> 如果加了&，获取的是FacotryBean

**源码：**

```java
/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from
	 * beans <i>created</i> by the FactoryBean. For example, if the bean named
	 * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
	 * will return the factory, not the instance returned by the factory.
	 */
	String FACTORY_BEAN_PREFIX = "&";
```

### bean的作用域

Spring支持5种作用域

常用作用域：singleton| prototype

* 执行时机（产生bean的时机）

**singleton：**

容器在初始化时，就会创建对象（唯一的一个）；以后再getBean时，不再产

生新的bean；singleton也支持延迟加载（懒加载）：在第一次使用时产生`@Lazy`

**prototype：**

容器在初始化时，不创建对象；只是在每次使用时（每次从容器获取对象时 ，

context.getBean(Xxxx)）,再创建对象；并且每次getBean()都会创建一个新的对象

### 条件注解

可以让某一个Bean 在某些条件下加入Ioc容器，其他情况下不加IoC容器（例如SpringBoot）

**实现步骤：**

a.准备bean

b.增加条件Bean：给每个Bean设置条件 ，必须实现Condition接口

```java
public class OilCarCondition  implements Condition {
    //如果当前环境是 oil，则加入 OilCar
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {

        //获取环境
        Environment environment = conditionContext.getEnvironment();
        String carType = environment.getProperty("car.type");//car.type="oil"
        if(carType.contains("oil")){
            return true ;
        }
        return false;
    }
}
```

c.根据条件，加入IoC容器

```java
@Bean
@Conditional(OilCarCondition.class)
public Car oilCar()
{
       return new OilCar() ;
 }
```

> 环境需要在Vmoptions中加

## Bean的生命周期

创建(new ...)、初始化（赋初值）、  ....、销毁

### 方法一

Student.java

**适用于bean+返回值**

在实体类中有	init()方法	destory()方法

**xml：**

```xml
init-method="myInit"  destroy-method="myDestroy"
```

**注解：**

```java
@Bean(value="stu",initMethod = "myInit",destroyMethod = "myDestroy")
```

> IoC容器在初始化时，会自动创建对象(构造方法) ->init ->.....->当容器关闭时调用
>
> destroy...

### 方法二

基于JAVA规范 ：JSR250

**实现：**

1.将响应组件加入@Component注解、 给初始化方法加@PostConstruct、给销毁方法加

@PreDestroy

@PostConstruct：相当于方法一的init

@PreDestroy：相当于方法一的destroy

* 三层注解 （功能性注解、MyIntToStringConverter.java）

@Controller、@Service、@Repository、@Component 

### 方法三

接口：适用于三层组件（扫描器+三层组件）

InitializingBean初始化

DisposableBean 销毁

```java
package com.yanqun.converter;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class MyFunction implements InitializingBean , DisposableBean {

        public void myMethod(){

        }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("MyFunction初始化...afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("MyFunction销毁。。。destroy");
    }
}
```

> 初始化：只需要实现InitializingBean中的afterPropertiesSet()方法
>
> 销毁：实现DisposableBean 中的destroy()方法

### 方法四

（给容器中的所有Bean加初始化、销毁）一个接口

接口`BeanPostProcessor`：拦截了所有中容器的Bean

> 适用于三层组件

```java
package com.yanqun.converter;

import com.yanqun.entity.Student;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Controller;

@Controller //(4个)
public class MyXxx implements BeanPostProcessor {

    //拦截器
    @Override//bean:Student(zs)
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        System.out.println("初始化:"+beanName+":"+bean);
//        bean.setName("ls")
		//可以在初始化时修改bean的值
        if(bean instanceof Student){
            System.out.println("MyXxx...初始化..");
            Student stu = (Student)bean ;
            stu.setStuName("zs123456");
            stu.setStuNo(123);
            return stu ;
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof Student) {
            System.out.println("MyXxx...销毁..");
        }
            return bean;
    }
}
```

## 自动装配

### 方式一

@Autowired(Spring)  **默认根据类型匹配**

多用于三层组件(4个注解+扫描器)

* 三层注解

1、如果@Autowired在属性前标注，则不调用setXxx；如果标注在setXxx前面 ，则调用setXxx

2、不能放在方法的参数前

* @Autowired 根据类型匹配

1.如果有多个类型相同的，匹配哪个？

报错

> 默认值@primary

2.能否根据名字匹配？

可以，结合 @Qualifier("stuDao2")使用

```java
@Autowired
@Qualifier("stuDao2")
```

### 方式二

@Resource（JSR250）**默认根据名字查找**

> 如果有名字根据名字匹配，如果没有名字，先根据名字查找，如果没找到，再根据类型查找；也可以通过name或type属性指定根据名字或类型查找

### 方式三

@Inject（JSR330），额外引入javax.inject.jar，默认根据类型匹配

## 利用Spring底层组件进行开发

能够供我们使用的组件，都是**Aware的子接口**，即XxxxAware

* 以ApplicationContextAware为例

**实现步骤：**

a.实现ApplicationContextAware接口

```java
@Component("myComponent99999")  //id  name
public class MyComponent implements ApplicationContextAware , BeanNameAware {
        private ApplicationContext applicationContext;
        private String beanName ;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("000000000000000000000000000000"+applicationContext);
        this.applicationContext= applicationContext ;
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("获取当前bean的name"+name);
        this.beanName = name ;
    }
}
```

b.重写其中的方法，都包含了一个对象。只需要将该对象赋值到属性中即可

**作用：**

例如ApplicationContextAware，可以通过该接口获取到Ioc容器对象

**执行时机：**

如果在main（）中new Ioc容器： 先执行ApplicationContextAware实现类中的方法，通过该方法传入IoC容器供我们自己使用；  然后再将该容器通过new返回给用户

## 环境切换

Spring:切换环境	

### 激活方式一

@Profile注解

虚拟机参数：

> -Dspring.profiles.active=@Profile环境名

使用环境：

更改数据库环境

### 激活方式二

硬编码

坑：其中AnnotationConfigApplicationContext中有一个refresh()操作：会将我们设置的一些参数还原

原来流程：

> 没激活 |->进行激活 ->刷新 ->没激活

更改流程：

> 没激活->进行激活  |    ->刷新

什么时候设置保存点|： 配置类的编写处

> IoC容器在使用时必须refresh() ;如果是有参构造，内部已经刷新；如果无参构造，需要手工刷新

## Spring重要组件

### 接口BeanPostProcessor

拦截了所有中容器的Bean，并且可以进行bean的初始化 、销毁

### BeanFactoryPostProcessor

拦截容器

```java
@Component
public class MyYYY  implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        beanFactory.getBeanDefinition("id");//根据bean的名字(id)获取bean
        int count = beanFactory.getBeanDefinitionCount();
        System.out.println("【b】&&&&&&&&&&&&&&容器中bean的个数："+count);
        String[] names = beanFactory.getBeanDefinitionNames();//name->id <bean id ="">
        System.out.println("【b】&&&&&&&&&&&&&&容器中所有bean的名字：" +Arrays.asList( names  )   );
    }
}
```

### BeanDefinitionRegistryPostProcessor

bean即将被加载之前（解析之前，成为BeanDefination对象之前）

```java
@Component
public class MyZZZ implements BeanDefinitionRegistryPostProcessor {


    //继承自BeanFactoryPostProcessor的方法    （bean的工厂）
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("【a后】postProcessBeanFactory:容器中注册的bean的数量:"+beanFactory.getBeanDefinitionCount());
        Object myBean = beanFactory.getBean("myBean");
        System.out.println( myBean.getClass().getName() );

    }

//    ApplicationListener，

    //BeanDefinitionRegistryPostProcessor接口自己的方法  （维护着容器中所有bean的注册信息）
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("【a先】postProcessBeanDefinitionRegistry:容器中注册的bean的数量:"+registry.getBeanDefinitionCount());

        //额外增加一个：postProcessBeanDefinitionRegistry （可以为容器额外增加一些bean的注册）
        //Orange
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(Orange.class);//产生BeanDefinition
//        beanDefinitionBuilder.getBeanDefinition();;//AbstractBeanDefinition

        registry.registerBeanDefinition("myBean", beanDefinitionBuilder.getBeanDefinition());


    }
}
```

**流程：**

BeanDefinitionRegistryPostProcessor(a)  ——加载bean——BeanFactoryPostProcessor(b)->实例化bean->BeanPostProcessor

> 同一个方法 在不同地方（类、接口）的出现时机问题：
>
> a继承b，因此a中必然包含b中的方法(记c )：虽然a和b中都有c，但是 因此c出现的时机不同， 则c的执行顺序也不同： 如果是在a中出现，则先执行；如果是在b中执行 则后执行

### 监听器

可以监听事件 ，监听的对象必须是ApplicationEvent自身或其子类/子接口

#### 方式一

必须实现ApplicationListener接口

```java
@Component
public class MyListener implements ApplicationListener {
    //监听对象
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("========**********========"+event+"======*********==========");
    }
}
```

#### 方式二

注解

```java
@Component
public class MyListener2 {

    //本方法是一个 监听方法
    @EventListener(classes = {ApplicationEvent.class})
    public void myListenerMethod(ApplicationEvent event){
        System.out.println("--0000000--------"+event);
    }
```

#### 自定义被监听的事件

a.自定义类实现ApplicationEvent接口（自定义事件）

```java
public class MyEvent3 extends ApplicationEvent {

    public MyEvent3(Object source) {
        super(source);
    }
}
```

b.发布事件

```java
context.publishEvent(自定义事件);
```

