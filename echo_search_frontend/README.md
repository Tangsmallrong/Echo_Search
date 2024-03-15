# 聚合搜索平台前端

## 1. 前端初始化

> [Ant Design Vue 官网](https://www.antdv.com/docs/vue/getting-started-cn)

- 安装组件库

```shell
npm i --save ant-design-vue@4.x
```

- 安装依赖

```shell
npm install
```

## 2. 记录搜索状态

- 目标：用 url 记录页面搜索状态，当用户刷新页面时，能够从 url 还原之前的搜索转态需要双向同步：url <=> 页面状态

- 核心技巧：把同步状态改为单向，即只允许 url 来改变页面状态，不允许反向 

- 实现步骤：

  - 让用户在操作的时候，改变 url 地址（eg. 点击搜索框，搜索内容填充到 url 上，切换 tab 时，也要填充）
 
  - 当 url 改变的时候，去改变页面状态（监听 url 的改变）



