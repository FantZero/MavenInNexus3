# nexus3搭建maven私库

#### 介绍
nexus 作为私库服务器，提供上传下载包的功能（前后端的包均可管理）。从私服请求，如果私服上不存在，则从外网的远程仓库下载，缓存在私服上。
nexus3 maven 私库搭建 上传下载

### 1. nexus 安装
**window 下安装参考：**
	https://blog.csdn.net/xc_wt/article/details/104943313		java用Nexus搭建maven私服

**linux 下安装：**

```shell
tar -zxvf nexus-3.28.1-01-unix.tar.gz
cd nexus-3.28.1-01/bin/
vim nexus	  # 将里面一句 run_as_root=true 改成 run_as_root=false
./nexus start #（默认进入后台，建议使用） 或  ./nexux run（不能进入后台）
./nexus stop  #停掉 nexus
cat ../../sonatype-work/nexus3/admin.password	#初始登录密码
```

**开启nexus后，直接在浏览器访问：http://10.8.80.222:8001，如果访问不通则开启防火墙**

```shell
firewall-cmd --query-port=8081/tcp	 #查看 8081（默认启动端口）是否开启了，显示 no 则执行下面开启
firewall-cmd --add-port=8081/tcp --permanent
firewall-cmd --reload		#重启防火墙

netstat -anp |grep 8081		#查看指定端口占用情况
lsof -i:8081	#查看端口使用情况
```

角色设置 -> 用户设置	可参考window下安装后的角色和用户设置

### 2. 上传下载 设置

```xml
%MAVEN_HOME%/conf/settings.xml，添加两处内容：
<servers>
    ...
    <!-- 非必要，不涉及到上传可以不加 -->
	<server>
		<id>myRepository</id>
		<username>deployer</username>
		<password>deployer</password>
	</server>
  </servers>

  <mirrors>
	...
    <!-- 必要 -->
	<mirror>
		<id>myRepository</id>
		<name>myRepository</name>
		<url>http://10.8.80.222:8081/repository/maven-public/</url>
		<mirrorOf>central</mirrorOf>
	</mirror>
  </mirrors>

涉及到上传参考 maven-up-project 中的 pom.xml 文件配置，不涉及到上传则不用管，主要是仓库信息：
    <distributionManagement>
        <repository>
            <!--此id要和.m2/settings.xml中设置的id一致 -->
            <id>myRepository</id>
            <!--指定仓库地址 -->
            <url>http://10.8.80.222:8081/repository/maven-releases/</url>
        </repository>
    </distributionManagement>
```

### 3. 注意事项

1. 自行确认一遍，在 Idea 中找到 maven 引用的 setting.xml，设置为上面修改的 %MAVEN_HOME%/conf/settings.xml
   	File | Settings | Build, Execution, Deployment | Build Tools | Maven
   		User settings file：xxx\settings.xml
2. IntelliJ Idea 拉取私库包后运行可能会报 “程序包xxx找不到”（Idea版本问题），执行以下操作：
   	如果有 projName.iml文件则删除
   	右侧 maven 刷新按钮Reimport
   	Terminal窗口执行命令：mvn idea:module