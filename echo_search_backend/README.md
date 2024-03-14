# 聚合搜索项目后端

## 1. 后端初始化

- 使用 SpringBoot 初始化模板

## 2. 获取数据源-爬虫

- 直接请求数据接口

  - Java 种调用接口：HttpClient、OKHttp、RestTemplate、Hutool（https://hutool.cn）

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

## 3. 聚合接口优化思路

**思考**: 如何能让前端既能一次搜出所有数据，又能够分别获取某一类数据

- 前端传 type 调用后端同一个接口，**后端根据 type 调用不同的 service 查询**

- **搜索逻辑**： 

  - 如果 type 为空，那么搜索出所有的数据 
  - 如果 type 不为空 
    - 如果 type 合法，那么查出对应数据
    - 否则报错

### 3.1 实现步骤

- **添加 搜索枚举类型**：`model/enums/SearchTypeEnum.java`

```java
USER("用户", "user"),
POST("帖子", "post"),
PICTURE("图片", "picture");
```

- **修改搜索的请求对象**：新增 type 字段

```java
private String type;
```

- **修改 SearchController 方法**

> **问题**：type 增多后，查询逻辑会堆积在 controller 代码里
> 
> **本质**：怎么能让搜索系统**更轻松的**接入更多的数据源？

### 3.2 门面模式

**作用**：帮助我们用户(客户端)去更轻松的实现功能，不需要关心门面背后的细节

- **利用门面模式优化搜索代码**:

  - 新建组件：`manager/SearchFacade.java`
  - controller 层直接调用即可

```java
switch (searchTypeEnum) {
    case POST:
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
        searchVO.setPostList(postVOPage.getRecords());
        break;
    case USER:
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        searchVO.setUserList(userVOPage.getRecords());
        break;
    case PICTURE:
        Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
        searchVO.setPictureList(picturePage.getRecords());
        break;
    default:
}
```

### 3.3 适配器模式

还需要解决的问题: type 增多后，**查询的 if-else 逻辑会堆积**

- **定制统一的数据源接入规范（标准）**：什么数据源允许接入？数据源接入时要满足什么要求？要做什么事情？
 
  - **任何接入我们系统的数据，它必须要能够根据关键词搜索、并且支持分页搜索**
  > 声明接口来定义规范，定制规范很重要！
  
- 假如我们的数据源已经支持了搜索，但是**原有的方法参数和我们的规范不一致，怎么办？**

  - **适配器模式的作用：通过转换，让两个系统能够完成对接**
  
  - 使参数兼容，编写一个适配器转换参数即可
  
  - 如果连适配都适配不了咋办？==> 具体问题具体分析
  
- 具体实现:

  - 编写**数据源接口**：`datasource/DataSource.java`
  
  ```java
  public interface DataSource<T> {
      Page<T> doSearch(String searchText, long pageNum, long pageSize);
  }
  ```
  
  - 分别**实现数据源接口**，如 USER 类型
  
  ```java
  public class UserDataSource implements DataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        // 运用适配器模式, 使参数适配
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);

        // 再将封装后的对象传入 userService 方法
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return userVOPage;
    }
  }
  ```
  
  - **修改门面，改用适配器的接口**
  
    - 为了进一步优化掉 switch-case 语句, **使用 map 来关联 枚举值和 datasource**
  
    ```java
    Map<String, DataSource> typeDataSourceMap = new HashMap() {{
      put(SearchTypeEnum.USER.getValue(), userDataSource);
      put(SearchTypeEnum.POST.getValue(), postDataSource);
      put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
    }};
    ```
    
    - 统一返回值

    ```java
    // private List<?> dataList;
    
    // 根据传入的类型来定义 dataSource 的类型
    DataSource<?> dataSource = typeDataSourceMap.get(type);
    
    Page<?> page = dataSource.doSearch(searchText, current, pageSize);
    searchVO.setDataList(page.getRecords());
    return searchVO;
    ```

> 之后如果要新增类型: 实现接口 videoDataSource ，实现方法 doSearch

### 3.4 注册器模式

- 再进一步优化: 创建一个注册器 `DataSourceRegistry`，在注册器中注册对象并提供访问方法

  - 提前通过一个 map 或者其他类型存储好后面需要调用的对象
  
```java
private Map<String, DataSource<T>> typeDataSourcesMap;

@PostConstruct
public void doInit() {
    typeDataSourcesMap = new HashMap() {{
        put(SearchTypeEnum.POST.getValue(), postDataSource);
        put(SearchTypeEnum.USER.getValue(), userDataSource);
        put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
    }};
}

public DataSource getDataSourceByType(String type) {
    if (typeDataSourcesMap == null) {
        return null;
    }
    return typeDataSourcesMap.get(type);
}
```

- 修改门面，改用注册器模式

```java
SearchVO searchVO = new SearchVO();
// 根据传入的类型来定义 dataSource 的类型
DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
Page<?> page = dataSource.doSearch(searchText, current, pageSize);
searchVO.setDataList(page.getRecords());
return searchVO;
```

**最终优化效果：代码量大幅度减少，可维护可扩展**


