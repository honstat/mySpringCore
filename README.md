# mySpringCore
手写springMVC核心功能

本项目根据咕咆学院tom老师的视频学习而来。

本项目依赖：
    javax.servlet-api.jar
    
    fastjson.jar

重点事项：

参数绑定部分自己实现的，用到了反射

    Parameter[] parameters = method.getParameters();
 
    p.getName()获取到的是agrs0 
 
 需要在idea设置才能获取到正确的变量名称 如id,name等
 
 设置如下：
 
   setting->java Compiler -> Additional command line parameters: -parameters
 
   把值设为-parameters
 
   并且保证项目jdk版本使用1.8
 
   Target bytecode verison 设为1.8
 
 参数绑定实现功能支持以下组合：
 
    无参
 
    单参数基本类型
 
    多参数基本类型
 
    单参数引用类型
 
    多参数引用类型
 
 
 请求类型能支持：
 
 GET
 
 POST
 
      form
      json
 
HomeController中提供了多种测试样本接口，可以检验

本项目实现spring核心功能有：

  spring ioc容器

  依赖自动注入

  请求路由
  
  参数绑定

使用到的技术：

  注解

  反射

  配置文件获取

加载实现步骤：

    加入核心类CJDispathServlet

    加载配置
 
    获取要扫描的包地址

    扫描要加载的类

    实例化要加载的类

    加载依赖注入,给属性赋值

    映射地址（url绑定接口方法）


url请求步骤：

    1.url路由

    2.反射方法签名

    3.解析请求类型

    4.参数类型绑定

    5.反射调用方法

    6.输出返回结果
