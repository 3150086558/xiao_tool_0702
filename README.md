# 组织权限管理与个人工具系统

## 项目概述

这是一个前后端分离的综合工具项目，分为两大部分：

- `backend-java`：组织、用户、岗位、角色、菜单、JWT 登录、数据权限等系统管理能力
- `backend-python`：记账、统计、待办、备忘录、数据库查询等个人工具能力
- `frontend`：基于 Vue 3 + Element Plus 的统一前端界面

当前仓库更适合按“正在开发中的集成项目”理解，而不是已经完全联调完成的成品。部分模块已经具备基础 CRUD 和页面能力，但仍存在若干接口契约不一致、假数据兜底和启动配置未收口的问题。

## 当前技术栈

| 层 | 技术 | 当前开发端口 |
|---|---|---|
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router | `5173` |
| Java 后端 | Spring Boot 3 + Spring Security + MyBatis-Plus | `8081` |
| Python 后端 | FastAPI + Uvicorn | `8000` |
| 数据库 | PostgreSQL 16 | `5432` |

## 当前已实现模块

### 1. 系统管理模块（Java）

接口前缀：`/api/sys/*`

已看到的主要能力：

- 登录、退出、当前用户信息、修改密码
- 组织树管理
- 用户管理、重置密码、启停状态
- 岗位管理
- 角色管理、角色菜单分配
- 菜单管理
- 数据权限可见用户、可见组织查询

### 2. 业务工具模块（Python）

接口前缀：`/api/app/*`

已看到的主要能力：

- 记账记录分页、增删改、导入导出、模板下载
- 统计汇总、分类统计、趋势统计
- 待办事项管理
- 备忘录管理
- 外部数据库连接配置与只读 SQL 查询

### 3. 前端页面

已存在页面包括：

- 登录页、首页、个人中心
- 组织、用户、岗位、角色、菜单管理
- 记账管理、统计报表
- 待办事项、备忘录、数据库查询

## 目录结构

```text
my_project/
├─ backend-java/        Java 系统管理后端
├─ backend-python/      Python 业务工具后端
├─ frontend/            Vue 前端
├─ sql/                 数据库初始化脚本
├─ nginx/               Nginx 配置
├─ test_data/           临时验证脚本
├─ docker-compose.yml   Docker 编排草稿
├─ IMPLEMENTATION_PLAN.md
└─ FIXES_SUMMARY.md
```

## 推荐启动方式

当前建议优先使用“本地分别启动”的方式，不建议直接依赖 `docker-compose.yml`，因为仓库内目前没有对应的 `Dockerfile`，且编排端口与实际应用端口也未完全对齐。

### 1. 准备数据库

创建数据库：

```sql
CREATE DATABASE org_sys WITH ENCODING 'UTF8';
```

执行初始化脚本：

```bash
psql -d org_sys -U postgres -f sql/init_postgres.sql
```

### 2. 启动 Java 后端

配置文件位置：`backend-java/src/main/resources/application.yml`

当前默认配置：

- 端口：`8081`
- 数据库：`jdbc:postgresql://127.0.0.1:5432/org_sys`
- 用户名：`postgres`
- 密码：`123456`

启动命令：

```bash
cd backend-java
mvnw.cmd spring-boot:run
```

说明：如果本机没有 Maven 依赖缓存，首次启动需要联网下载依赖。

### 3. 启动 Python 后端

安装依赖：

```bash
cd backend-python
pip install -r requirements.txt
```

启动命令：

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

关键环境变量：

- `DB_HOST=127.0.0.1`
- `DB_PORT=5432`
- `DB_NAME=org_sys`
- `DB_USER=postgres`
- `DB_PASSWORD=123456`
- `JWT_SECRET=xiao-sys-secret-key-2026-very-long-secret`
- `JAVA_SERVICE_URL=http://127.0.0.1:8081`
- `DATA_SCOPE_ENABLED=false`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

当前 Vite 代理：

- `/api/sys` -> `http://127.0.0.1:8081`
- `/api/app` -> `http://127.0.0.1:8000`

访问地址：

- 前端：<http://localhost:5173>
- Python Swagger：<http://localhost:8000/docs>

## 默认账号说明

仓库内存在两个相互冲突的默认账号线索：

- 登录页提示：`admin / admin123`
- 初始化 SQL 注释提示：`admin / Admin@123`

因此请不要直接信任前端提示文案，建议以实际数据库初始化结果为准，或初始化后立即手工验证一次登录。

## 当前已确认的现状与限制

以下内容是基于当前源码检查得到的结论，开发和联调时请优先关注：

### 1. Docker 编排未收口

- `docker-compose.yml` 中引用了 `backend-java/Dockerfile`、`backend-python/Dockerfile`、`frontend/Dockerfile`
- 当前仓库内未看到这些 `Dockerfile`
- `docker-compose.yml` 中 Java 暴露的是 `8080:8080`，但 Java 实际配置端口是 `8081`

结论：`docker-compose.yml` 目前不能视为可直接使用的正式部署方案。

### 2. 多个前端页面带有假数据兜底

当接口失败时，部分页面会自动回退到本地 `mockData()` / `mockTree()`，包括但不限于：

- 用户管理
- 组织管理
- 角色管理
- 岗位管理
- 待办事项
- 备忘录

结论：页面“看起来有数据”不代表后端真的可用，联调和验收时必须看网络请求结果。

### 3. 部分接口契约存在不一致

当前源码中已经确认存在以下不一致：

- 前端岗位列表调用 `/api/sys/position/list`，后端实际提供的是 `GET /api/sys/position`
- 前端角色菜单回显调用 `/api/sys/role/{id}/menu-ids`，后端实际提供的是 `GET /api/sys/role/{id}/menus`
- 前端岗位权限分配调用 `/api/sys/position/{id}/permissions`，后端目前没有这组聚合接口
- 前端岗位数据权限使用数字值，后端 `DataScopeDTO` 使用字符串 `scopeType`
- 数据库查询页前端使用 `postgresql`，后端连接器识别 `postgres`

结论：当前项目最需要优先做的是接口契约收口，而不是继续堆页面。

### 4. 业务功能还有未闭环点

已确认的典型问题：

- 待办编辑时 `remark` 字段无法持久化
- 数据库查询页的 PostgreSQL / SQLite 表单与后端要求不一致
- SQL 执行失败时前端没有把错误原因展示给用户
- 记账导入界面允许选择 `.csv`，但后端导入实现是 Excel 读取逻辑
- 统计页的“近期明细”仍是静态示例数据，不是实时接口数据
- 首页统计卡片是写死的展示数据，不是后端实时统计

## 建议的后续开发优先级

建议开发顺序如下：

1. 先统一前后端接口路径、参数名、枚举值、返回结构
2. 去掉生产页面里的假数据兜底，改成明确错误提示
3. 修复岗位权限、角色菜单、岗位列表等主流程阻塞问题
4. 补齐待办、数据库查询、统计页等业务模块的真实联调
5. 最后再整理 Docker、部署和环境变量配置

## 当前验证情况

截至本次 README 更新时，已完成的验证如下：

- 前端 `npm run build` 可以成功打包
- 前端构建后存在较大的 bundle 告警，但不阻塞运行
- Java 自动化测试未在当前环境完整跑通，原因是 Maven Wrapper 依赖下载受网络环境影响
- Python 侧进行了源码检查，但当前本地虚拟环境解释器引用存在异常，未完成完整运行验证

## 适用结论

如果你是继续开发这个项目，建议把它当成“已完成基础框架和大部分页面，但仍需一次系统联调和接口收口”的版本。

如果你是准备交付或部署这个项目，当前状态还不适合作为最终交付版本，至少需要先完成：

- 接口契约统一
- 假数据清理
- 主流程联调
- 启动与部署脚本收口
- 默认账号与环境变量整理
