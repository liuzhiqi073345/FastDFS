#### vue前端搭建

###### 安装node.js

###### 安装淘宝镜像

###### 全局安装`webpack cnpm install webpack -g`

###### 全局安装vue-cli `cnpm install -g vue-cli`

######使用webpack模板创建一个vue项目`vue init webpack project`

###### 安装项目依赖`cd project`、`npm install`

###### 启动项目`npm run dev`

###### 编译vue为静态文件`mpm run build`

###### 注意：如果是结合Spring boot项目，则将前端vue模块项目直接放在java项目的目录下即可，和src同目录

#### vue前台添加axios插件实现前后台的访问

###### 安装axios插件`npm install --save-dev axios`，重启前台`npm run dev`

###### 在前台配置的config中index.js中配置上下文以及后台访问的端口等信息

```js
const path = require('path');

module.exports = {
  
  dev: {
    port: 8081, // 前台项目启动的端口号
    env: require('./dev.env'),
    autoOpenBrowser: true,
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    proxyTable: {
      '/test': { // 在axios的强求路径中进行匹配，如axios.get('/test/hello')
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/test': '/test'
        },
        secure: false
      }
    },

    // Paths
    // assetsSubDirectory: 'static',
    // assetsPublicPath: '/',
    // proxyTable: {},
    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
```

###### 在vue页面使用axios插件实现前后台访问功能

```vue
<template>
<div>
  <h1>{{ msg }}</h1> 
  <button @click="test">hello</button> 
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'HelloWorld',
  data() {
    return {
      msg: 'Welcome to Your Vue.js App'
    };
  },
  methods: {
    test: function() {
      axios
        .get('/test/hello')
        .then(Response => {
          alert(Response.data);
        })
        .catch(() => {
          alert('失败');
        });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>

```

#### vue集成element-ui

###### 安装`npm i element-ui -S`

###### 在前台项目src目录下的main.js中引入和使用element-ui

```js
// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

// 使用element-ui组件
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
```

###### 在vue页面中使用element-ui组件

```vue
<template>

  <div>
    <el-carousel :interval="4000" type="card" height="200px">
      <el-carousel-item v-for="item in 6" :key="item">
        <h3>{{ item }}</h3>
      </el-carousel-item>
    </el-carousel>
    <!-- <h1>{{ msg }}</h1> -->
    <!-- <button @click="test">hello</button> -->
  </div>
</template>

<script>
import axios from 'axios';
export default {
  name: 'HelloWorld',
  data() {
    return {
      msg: 'Welcome to Your Vue.js App'
    };
  },
  methods: {
    test: function() {
      axios
        .get('/test/hello')
        .then(Response => {
          alert(Response.data);
        })
        .catch(() => {
          alert('失败');
        });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
</style>
```

#### Spring boot快速搭建

###### new Spring Starter Project，选择需要创建什么样的项目（如：web、jpa，即简单的spring mvc和spring data jpa）

###### 将src/main/resources下的application.properties改名为application.yml，不同的扩展名有不同的配置方式

```yml
server:
  port: 8080 #设置端口
  tomcat:
    uri-encoding: UTF-8  
  context-path: /test #设置项目模块的上下文
spring:
  datasource: #数据库连接信息
    url: jdbc:oracle:thin:@ip:1521:orcl
    username: username
    password: password
    driver-class-name: oracle.jdbc.driver.OracleDriver
  jpa:
    database: ORACLE
    show-sql: true
    hibernate:
      ddl-auto: validate
```

###### 使用了oracle数据库，在pom.xml中添加依赖包

```xml
<dependency>
  <groupId>com.oracle</groupId>
  <artifactId>ojdbc6</artifactId>
  <version>11.2</version>
  <scope>runtime</scope>
</dependency>
```

###### 创建和数据库表对应的Entity实体类

```java
package com.example.demo.test;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="FASTDFS")
public class FastDfs implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	String filename;
	String groupname;
	String extension;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
}
```

###### 创建使用spring data jpa的接口

```java
package com.example.demo.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface FileRepository extends JpaRepository<FastDfs, String>{
	
}
```

###### 编写一个controller类，连接数据库操作

```java
package com.example.demo.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
	FileRepository fileRepository;

	@GetMapping("/hello")
	public String getString() {
		
		FastDfs file = new FastDfs();
		file.setFilename("wwf");
		file.setGroupname("group1");
		file.setExtension("jpg");
		
		fileRepository.save(file);
		
		return "hello world";
	}
}
```

######运行后台项目run as Spring Boot App





