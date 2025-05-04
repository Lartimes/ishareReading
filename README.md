# iShareReading - 智能阅读平台
    iShareReading 是一个创新的在线阅读平台，旨在通过 AI 技术提升阅读效率和认知深度。
我们相信，在信息爆炸的时代，大学生需要更高效、更智能的阅读方式来获取知识。
大学四年是构建知识体系的黄金期，通过阅读深化认知是传统且有效的途径。 但逐句精读的线性学习模式效率有限，加之图书市场良莠不齐，低价值阅读可能导致时间成本虚耗。
引入 AI 技术辅助阅读，利用其多维度解读与高效总结能力，可实现知识获取的加速度，突破传统阅读的认知边界，助力大学生构建更立体、多元的知识网络。
通过 AI 多角度解读和智能总结，我们帮助读者快速把握书籍精髓，扩展认知边界。

## 🌟 核心特性

- **辩证思考**: AI 辅助分析不同观点，培养批判性思维
- **云端存储**: 支持在线阅读进度同步，随时随地继续阅读
- **AI Agent**: 支持自定义Agent，添加MCP Tools，自定义知识库
- **智能总结**: 自动生成章节目录和全书摘要，帮助快速把握核心内容
- **个性化推荐**: 基于阅读历史和兴趣的标签概率算法多维度智能推荐系统
- **Memory**: 支持多维度记忆功能，支持内存短期记忆、短期记忆、长期记忆
- **Token**: 对Token Count实现了三种Token解决方案: 无策略、滑动窗口、摘要
- **AI 多角度解读**: 利用大语言模型对文本进行多维度分析，提供不同视角的见解
- **Prompts**: 过滤用户Input敏感词、优化提示词，Rerank层面提取热点Input进行Rag缓存



## 🏗️ 项目架构

项目采用模块化设计，主要包含以下三个核心模块：

```
ishareReading/
├── oss-starter/          # 对象存储服务模块
├── common/              # 公共组件模块
├── agent-reading/       # Agent阅读模块
├── backend-reading/     # 后端服务模块
└── sql/                # 数据库脚本
```

## 🏗️ 文件描述

```
├───agent-reading
│   ├───src
│   │   └───main
│   │       ├───java
│   │       │   └───org
│   │       │       └───ishareReading
│   │       │           └───bankai
│   │       │               ├───config  #AI相关配置类
│   │       │               ├───controller #AI对话等ETL RAG接口
│   │       │               ├───interceptor #类似网关层面进行AC自动机过滤敏感词 + 优化及动态Prompts
│   │       │               ├───mapper #持久代理
│   │       │               ├───model  #实体类
│   │       │               ├───service
│   │       │               │   ├───factory #创建Model工厂
│   │       │               │   ├───impl   # beans 实现类
│   │       │               │   ├───memory #长短期记忆 + Token解决策略
│   │       │               │   └───rag #RAG 知识库，PostgreSQL + ES 双路召回，重排序
│   │       │               │       └───impl
│   │       │               └───util #SSE 工具类
├───backend-reading
│   ├───src
│   │   ├───main
│   │   │   ├───java
│   │   │   │   └───org
│   │   │   │       └───ishareReading
│   │   │   │           └───bankai
│   │   │   │               ├───aop #AOP切面，类似SPI，一种更新的方式，融合SPRING 容器,定义通用及修改用户模型
│   │   │   │               ├───config #配置类
│   │   │   │               ├───controller #后端接口
│   │   │   │               ├───es #ES相关，实现全文倒排索引KNN搜索
│   │   │   │               ├───exception #异常捕获 
│   │   │   │               ├───interceptor #Token拦截器
│   │   │   │               ├───listener #监听类
│   │   │   │               ├───mapper #持久层代理曾
│   │   │   │               ├───model #实体类
│   │   │   │               ├───nlp #NLP 工具类，以及实现本地Model + LLM 双重过滤 添加训练集，热更新本地Model
│   │   │   │               ├───scheduletasks #定时任务
│   │   │   │               ├───service #服务类
│   │   │   │               └───utils #工具类
│   │   │   └───resources
│   │   │       └───META-INF
├───common
│   ├───src
│   │   └───main
│   │       └───java
│   │           └───org
│   │               └───ishareReading
│   │                   └───bankai
│   │                       ├───constant #常量
│   │                       ├───exception #异常处理
│   │                       ├───holder #ThreadLocal User...
│   │                       ├───response #通用响应类
│   │                       └───utils #工具类
├───data
│   └───train #HaNLP 维基百科中文分词训练集
├───docker-compose #Docker-Compose启动脚本
│   ├───es
│   │   └───config
│   ├───pgvector
│   └───redis
│       └───conf
├───ishareReadingParent #Maven父工程，定义版本依赖
├───oss-starter #基于Aliyun SDK 自定义Starter自动配置
└───sql #PostgreSQL 脚本
```


## 🛠️ 技术栈

- **对象存储**: Aliyun Oss
- **后端框架**: Spring Boot 3.3.4 、MyBatis-Plus 3.5.5
- **AI 框架**: Spring AI 1.0.0-M6,Spring AI Alibaba 1.0.0-M6.1
- **数据库、搜索引擎**: PostgreSQL 17.x, PgVector 、Elasticsearch 8.17.5
- - **LLM** : HaNLP portable-1.8.6、支持多种 DashScope Model、OpenAI Model

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+
- Docker  28.1.1  & Docker Compose 

### 安装步骤

1. 克隆项目
```bash
git clone https://github.com/yourusername/ishareReading.git
cd ishareReading
```

2. 使用 Docker Compose 启动 
```bash
cd docker-compose
chmod +x linux_start.sh
sh linux_start.sh
```

或者手动安装：

1. 安装依赖
```bash
# 按顺序安装各个模块
cd oss-starter
mvn clean install

cd ../common
mvn clean install

cd ../agent-reading
mvn clean install

cd ../backend-reading
mvn clean install
cd ..
```

2. 配置数据库
- 创建 PostgreSQL 数据库
- 执行 `sql/` 目录下的数据库脚本

3.启动Docker-Compose服务
```bash

cd ./docker-compose/es
docker-compose up -d
cd ../pgvector
docker-compose up -d
cd ../redis
docker-compose up -d
cd ..
sh build-backend.sh
docker run --name ishareReading -dp 8080:8080 ishareReading:latest
#=============或者启动好依赖服务之后手动启动jar包
java -jar backend-reading-1.0.0.jar & 

#无报错即为成功 

```


## 📝 开发计划
- [ ] 支持MCP TOOLS, 采用A2A协议实现Agents协作
- [ ] 开展用户反馈与改进：建立完善的用户反馈机制，通过问卷调查、社区留言、在线客服等渠道收集用户意见和建议，定期对平台进行优化和改进，满足用户多样化需求。
- [ ] 在云端存储的基础上，完善跨设备同步功能，确保用户在电脑、手机、平板等不同设备间切换时，阅读进度、笔记、标记等内容实时更新且保持一致。
- [ ] 增加阅读数据分析：为用户生成可视化的阅读数据报告，涵盖阅读时长、阅读速度、知识掌握度、阅读偏好变化等维度，帮助用户清晰了解自己的阅读情况，调整阅读计划  
- [ ] 优化个性化推荐算法：引入协同过滤算法，结合用户的阅读行为、收藏偏好以及其他相似用户的阅读数据，进一步提升推荐的精准度和个性化程度，为用户发现更多符合兴趣的优质书籍。​

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目。在提交 PR 之前，请确保：

1. 代码符合项目规范
2. 添加必要的测试
3. 更新相关文档

## 📄 开源协议

本项目采用 [MIT 协议](LICENSE) 开源。