# audio-mongo

## 数据模型

AudioMain

```Schema
{
	id : '',
	fileId : '',
	name : '测试',
	suffix : 'mp3',
	fullName : '测试.mp3',
	userCode : 'zhangsan',
	userName : '张三',
	validStatus : '1',
	insertTime : ,
	updateTime : ''
}
```

## API

### 上传文件

后端api

```api
/audio/ajaxupload?userCode=' + userCode + '&userName=' + userName
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

### 查询列表

后端api

```api
/audio/queryAudios?name=&pageNo=1&pageSize=10&validStatus=1
```

返回数据

```
{
	success : true,
	errorMsg : '',
	pageNo : 1,
	pageSize : 10,
	totalCount : 230,
	totalPageCount : 23,
	results : [
				{
					...
				},
				{
					...
				}
	]
}
```

### 播放音频示例

后端api

```api
/audio/audioplay?fileId=
```

前端h5代码

```Html5
<audio id="mp3" src="/audio/audioplay?fileId=5cba8928aecddb05b8994612" controls="true"></audio>
```
