# 聚合搜索项目后端

## 1. 后端初始化

- 使用 SpringBoot 初始化模板

## 2. 获取数据源-爬虫

- 直接请求数据接口
  
  -  Java 种调用接口：HttpClient、OKHttp、RestTemplate、Hutool（https://hutool.cn）
    
  > 这里使用 Hutool

```xml
<!-- https://hutool.cn/docs/index.html#/-->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.8</version>
</dependency>
```

- 等网页渲染出明文内容后，从前端页面的内容抓取
  
  - jsoup 库：获取到 HTML 文档,然后从中解析出需要的字段
    
```xml
<!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.15.3</version>
</dependency>
```

## 3. 聚合接口

- 在加载页面时，调用三个接口分别获取文章、图片、用户数据

- 可以考虑几种不同的业务场景：
  
  - 用户点击某个 tab 的时候，只调用这个 tab 的端口, 如: 根据类型字段的传参返回对应类型的数据 
    
  - 如果是针对聚合内容的网页，可以一个请求搞定（如: https://tophub.today/） ==> 全部返回为一个对象

  - 有可能还要查询其他的信息，同时反馈给用户（如 b 站）
    
> 要根据实际情况去选择方式

- 对于第二种场景，需要解决的问题：

  - **请求数量较多**，可能会受到浏览器的限制
    - 用一个接口请求完所有的数据(后端可以并发，几乎没有并发数量限制)
    > 注意: 并发不一定更快！短板效应。要以实际测试为准!
    
  - **请求不同接口的参数可能不一致**，增加前后端沟通成本
    - 用一个接口把请求参数统一，前端每次传固定的参数，后端去对参数进行转换
    - 统一返回结果：比如 page 页面封装
    
  - 前端写调用多个接口的代码，**代码重复**
    - 用一个接口，通过不同的参数去区分查询的数据源
    
