# 苍穹外卖毕业设计系统

作者与演示角色：`liurui`

这是一个可独立运行的苍穹外卖单体项目，包含 Spring Boot 后端、H2 持久化数据库、后台管理页面和用户点餐页面。项目面向毕业设计答辩场景，重点覆盖餐饮外卖系统的核心业务闭环：登录、分类、菜品、套餐、地址、购物车、下单、订单流转、店铺营业状态和经营数据看板。

## 技术栈

- Java 17
- Spring Boot 3.3
- Spring Web
- Spring JDBC
- H2 Database
- HTML + CSS + 原生 JavaScript

## 运行环境

需要先安装：

- JDK 17+
- Maven 3.9+

当前机器没有检测到 `java` 和 `mvn` 命令，所以我无法在本机完成编译与启动验证。安装环境后可直接运行。

## 启动项目

```powershell
mvn spring-boot:run
```

启动后访问：

- 项目入口：http://localhost:8080/
- 后台管理端：http://localhost:8080/admin/
- 用户点餐端：http://localhost:8080/user/
- H2 控制台：http://localhost:8080/h2-console

H2 控制台连接信息：

```text
JDBC URL: jdbc:h2:file:./data/sky_take_out
User Name: sa
Password:
```

## 默认账号

后台管理员：

```text
账号：admin
密码：123456
姓名：liurui
```

用户端：

```text
账号：liurui
密码：123456
姓名：liurui
```

## 已实现功能

- 后台登录：基于 `employee` 表校验管理员账号。
- 用户登录：基于 `customer` 表校验用户账号。
- 分类管理：新增分类、分类列表、启用/禁用。
- 菜品管理：新增菜品、菜品列表、起售/停售。
- 套餐管理：新增套餐、套餐关联菜品、起售/停售。
- 店铺状态：后台设置营业/打烊，用户端实时查看。
- 用户点餐：按分类浏览菜品和套餐。
- 地址簿：新增地址、默认地址。
- 购物车：加入购物车、查看购物车、清空购物车。
- 订单：提交订单、查看历史订单、后台推进订单状态。
- 经营看板：分类数、菜品数、套餐数、订单数、营业额、营业状态。
- 数据持久化：使用 H2 文件数据库，表结构在 `schema.sql`，初始化数据在 `data.sql`。

## 后端接口概览

- `POST /admin/employee/login`
- `GET /admin/dashboard`
- `GET /admin/shop/status`
- `PUT /admin/shop/status`
- `GET /admin/category/list`
- `POST /admin/category`
- `PUT /admin/category/{id}/status/{enabled}`
- `GET /admin/dish/list`
- `POST /admin/dish`
- `PUT /admin/dish/{id}/status/{enabled}`
- `GET /admin/setmeal/list`
- `POST /admin/setmeal`
- `PUT /admin/setmeal/{id}/status/{enabled}`
- `GET /admin/order/list`
- `PUT /admin/order/{id}/status/{status}`
- `POST /user/user/login`
- `GET /user/shop/status`
- `GET /user/category/list`
- `GET /user/dish/list`
- `GET /user/setmeal/list`
- `GET /user/addressBook/list`
- `POST /user/addressBook`
- `PUT /user/addressBook/{id}/default`
- `GET /user/shoppingCart/list`
- `POST /user/shoppingCart/add`
- `DELETE /user/shoppingCart/clean`
- `POST /user/order/submit`
- `GET /user/order/historyOrders`

## 项目结构

```text
src/main/java/com/liurui/sky
  common      统一响应、业务异常、全局异常处理
  controller  管理端和用户端 REST 接口
  model       请求和业务模型
  store       JDBC 数据访问与核心业务编排
src/main/resources
  schema.sql  数据表结构
  data.sql    初始化演示数据
  static      后台管理端和用户点餐端页面
```

## 答辩说明要点

- 系统采用前后端同工程部署，降低演示复杂度。
- 业务分为后台管理端和用户点餐端，两端通过 REST API 访问统一业务层。
- 当前使用 H2 方便答辩部署，后续可平滑迁移到 MySQL，只需要替换 `application.yml` 的数据源并调整少量 SQL 方言。
- `schema.sql` 展示了实体关系，包含员工、用户、分类、菜品、套餐、购物车、订单和订单明细。
- 店铺状态、订单状态流转和经营看板体现了真实外卖业务中的运营管理能力。

## 后续增强方向

- 增加 JWT 拦截器和接口权限控制。
- 使用 BCrypt 存储密码。
- 将 JDBC 数据访问拆分为 Repository/Service 分层。
- 增加分页查询、订单支付、催单、退款、派送完成等状态。
- 增加单元测试和集成测试。
