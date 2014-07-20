# AbleSky代码发布系统

### 介绍
- 本系统旨在实现代码发布流程的可视化界面操作，从而简化代码发布流程，减少因ftp上传、终端登录和命令行输入等零散操作所导致的人为失误。

- 本系统的运行需要依赖AbleSky的代码发布脚本(shell)和[ablejs](https://github.com/ablesky/ablejs)构建工具(Node.js)。

### 主要功能

- **管理用户:** 包括用户注册、登录、角色设置和修改密码。系统中的第一个注册用户会自动成为管理员。

- **管理项目:** 包括各项目的管理。项目名称及war包名称，要与发布脚本和发布环境的实际情形保持一致。

- **发布代码:** 包括war包(.war或.tar文件)和补丁压缩包(.zip文件)的上传、解压、调用shell发布脚本和在页面上实时输出日志。

- **管理补丁组:** 不同功能的补丁，要关联相应的补丁组。此后，在发布某一补丁组的补丁文件时，系统会自动检查所有正在测试中的其他补丁组，并发现潜在的文件冲突。

- **静态文件查询:** 配合ablejs的命令，可以按文件路径查询构建后的静态文件的hashcode，或者根据hashcode反查源文件的路径。

### 技术选型

- **前端框架:**&ensp;&ensp;jQuery, Bootstrap

- **后端框架:**&ensp;&ensp;Spring-MVC, Spring, Hibernate, Shiro

- **数据库:**&ensp;&ensp;&ensp;&ensp;SQLite(用于开发和生产环境), H2(用于测试)
 
- **服务器:**&ensp;&ensp;&ensp;&ensp;生产环境使用内嵌版Jetty服务器

- **测试框架:**&ensp;&ensp;Junit, Mockito

- **项目管理:**&ensp;&ensp;Maven

- **版本控制:**&ensp;&ensp;Git

### 开发环境运行方法

 &ensp;&ensp;**1.下载工程**  `git clone https://github.com/yangziwen/asdeploy-java.git`

 &ensp;&ensp;**2.初始化数据库**  `mvn compile exec:exec -Pinit-db`

 &ensp;&ensp;**3.运行工程**  `mvn tomcat7:run`，如需开启ldap验证，则可在系统变量中设置ldap服务器的url

 &ensp;&ensp;&ensp;&ensp;如`mvn tomcat7:run -Dasdeploy.ldap.url=ldap://localhost:10389`

 &ensp;&ensp;**4.访问工程** [http://localhost:8099](http://localhost:8099)

 &ensp;&ensp;另外，如需手动操作数据库，可安装[SQLite](http://www.sqlite.org/download.html)及其可视化管理工具[SQLite Expert Personal](http://www.sqliteexpert.com/download.html)
