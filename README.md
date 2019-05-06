# audio-mongo

## 数据模型

AudioMain

```json
{
	"id" : "",
	"fileId" : "",
	"name" : "测试",
	"suffix" : "mp3",
	"fullName" : "测试.mp3",
	"userCode" : "zhangsan",
	"userName" : "张三",
	"validStatus" : "1",
	"insertTime" : "",
	"updateTime" : ""
}
```

字段说明

|参数|说明|
|---|---|
|id|主键|
|fileId|文件存储在GridFS中的主键|
|name|文件的名称，不带后缀名|
|suffix|文件后缀名|
|fullName|文件的名称，带后缀名|
|userCode|用户代码|
|userName|用户名称|
|validStatus|有效状态，1位有效，0位无效|
|insertTime|创建时间|
|updateTime|修改时间|

## API

### 上传文件

后端api

```api
/audio/ajaxupload?userCode=' + userCode + '&userName=' + userName
```

参数说明

|参数|说明|
|---|---|
|name|文件的名称|
|userCode|用户代码|
|userName|用户名称|

返回数据

```json
{
	"success" : true,
	"errorMsg" : "",
	"result" : {
				"id" : "",
				"fileId" : "",
				"name" : "测试",
				"suffix" : "mp3",
				"fullName" : "测试.mp3",
				"userCode" : "zhangsan",
				"userName" : "张三",
				"validStatus" : "1",
				"insertTime" : "",
				"updateTime" : ""
			}
}
```

前端jquery上传文件代码

```
// 组织数据
var formData = new FormData(); 
formData.append('file', targetFile); //生成一对表单属性 

// 上传文件部分
$.ajax({ 
	type: "POST", //因为是传输文件，所以必须是post 
	url: '/audio/ajaxupload?userCode=' + userCode + '&userName=' + userName, 
	data: formData, 
	processData: false, 
	contentType: false, 
	success: function (result) { 
		// TODO
	},
	failure:function (result) {  
		// TODO
	}
}); 
```

### 删除文件

后端api

```api
/audio/deleteAudio?id=' + id
```

参数说明

|参数|说明|
|---|---|
|id|文件的id|

返回数据

```json
{
	"success" : true,
	"errorMsg" : ""
}
```

### 查询列表

后端api

```api
/audio/queryAudios?name=&pageNo=1&pageSize=10&validStatus=1
```

参数说明

|参数|说明|
|---|---|
|name|文件的名称|
|pageNo|当前页码|
|pageSize|每页条数|
|validStatus|有效状态，1位有效，0位无效|

返回数据

```
{
	"success" : true,
	"errorMsg" : "",
	"pageNo" : 1,
	"pageSize" : 10,
	"totalCount" : 230,
	"totalPageCount" : 23,
	"results" : [
				{
					"id" : "",
					"fileId" : "",
					"name" : "测试",
					"suffix" : "mp3",
					"fullName" : "测试.mp3",
					"userCode" : "zhangsan",
					"userName" : "张三",
					"validStatus" : "1",
					"insertTime" : "",
					"updateTime" : ""
				},
				{
					"id" : "",
					"fileId" : "",
					"name" : "测试2",
					"suffix" : "mp3",
					"fullName" : "测试2.mp3",
					"userCode" : "lisi",
					"userName" : "李四",
					"validStatus" : "1",
					"insertTime" : "",
					"updateTime" : ""
				}
	]
}
```

### 播放音频示例

后端api

```api
/audio/audioplay?fileId=
```

参数说明

|参数|说明|
|---|---|
|fileId|音频文件的主键，也就是AudioMain数据结构中的fileId|

前端h5代码

```Html5
<audio id="mp3" src="/audio/audioplay?fileId=5cba8928aecddb05b8994612" controls="true"></audio>
```

## 安装

### 环境要求

- MongoDB
- Maven

### 修改配置文件

拷贝`application-example.properties`命名为`application-test.properties`

修改`application-test.properties`中mongodb的连接及数据库配置部分

|配置字段|说明|
|---|---|
|mongodb.ip|MongoDB的ip，本地环境默人为localhost|
|mongodb.port|MongoDB端口，默人为27017|
|mongodb.database|应用需要连接的database|
|mongodb.dbUserDefined|定义用户的database|
|mongodb.user|用户名|
|mongodb.password|密码|

### 打包

```maven

mvn clean

mvn package
```

## 发布说明

### v1.0.0

- 使用MongoDB存储音频文件
- 音频文件的上传、删除、查询功能

### v1.1.0

- 使用连接池
- 使用MongoTemplate获取Collection来进行查询

## 协同开发

使用github flow工作流

- clone到本地
- 在本地创建自己的一个分支
- switch到本地自己的分支上
- 修改代码
- commit代码
- pull request
