var contextRootPath = '/audio';

function newRecord(title){
	//打开新的标签，在新的标签中进行添加操作
	//addTab(title,'leave/editLeaveApplication?editType=new');
	
	$('#dlg').dialog('open').dialog('setTitle', title);
	$('#fm').form('clear');
//	//设置修改类型，否则action中保存方法不知道是什么修改类型
//	$('#editType_edit').val("new");

	// 先判断隐藏域中是否有userCode，如果没有从cookie中取值
	var userCode = $('#userCode_hidden').val();
	
	if(userCode == null || userCode == ''){
		userCode = $.cookie('userCode');
	}
	
	// 先判断隐藏域中是否有userName，如果没有从cookie中取值
	var userName = $('#userName_hidden').val();
	
	if(userName == null || userName == ''){
		userName = $.cookie('userName');
	}
	
	// 清空原有数据
	$('#userCode_edit').textbox('setValue', userCode);  // 用户编码不需要清空
	$('#userName_edit').textbox('setValue', userName);
	$('#file_edit').filebox('setValue', '');
	
}

function destroyRecord(){
	var rows = $('#dg').datagrid('getSelections');
	if (rows != null && rows.length != null && rows.length > 1){
		$.messager.alert('提示','不允许选择多条记录进行修改');
		return false;
	}
	
	var row = $('#dg').datagrid('getSelected');
	if (row){
		$.messager.confirm('Confirm','确定删除这条记录吗？',function(r){
			if (r){
				var id = row.id;
				$.post(contextRootPath + '/deleteAudio?id=' + id, {},function(result){
					if (result.success){
						$('#dg').datagrid('reload');	// reload the user data
					} else {
						$.messager.show({	// show error message
							title: 'Error',
							msg: result.errorMsg
						});
					}
				},'json');
			}
		});
	}
}

function uploadFile(){
	var userCode = $('#userCode_edit').textbox('getValue');
	var userName = $('#userName_edit').textbox('getValue');

	var fileList = $("input[type='file'].textbox-value")[0].files;

	if(fileList == null || fileList.length == 0){
		$.messager.alert("操作提示", '请选择文件！', "info");
		return ;
	}else{
		if(fileList.length > 1){
			$.messager.alert("操作提示", '暂不支持一次上传多个文件！', "info");
			return ;
		}
	}

	var type = "file"; 
	//后台接收时需要的参数名称，自定义即可 
	// 获取file对象
	var targetFile = $("input[type='file'].textbox-value")[0].files[0];

	var formData = new FormData(); 
	formData.append(type, targetFile); //生成一对表单属性 

	// 页面隐藏域中填写上最近一次上传文件的userCode
	if(userCode != null && userCode != ''){
		$('#userCode_hidden').val(userCode);
		
		// 如果cookie中没有userCode，填写上
		var userId = $.cookie('userCode');
		if(userId == null || userId == ''){
			$.cookie('userCode', userCode, { expires: 7, path: '/' });
		}
	}
	
	if(userName != null && userName != ''){	
		$('#userName_hidden').val(userName);
		// 如果cookie中没有userCode，填写上
		var uName = $.cookie('userName');
		if(uName == null || uName == ''){
			$.cookie('userName', userName, { expires: 7, path: '/' });
		}
	}
	
	
	$.ajax({ 
		type: "POST", //因为是传输文件，所以必须是post 
		url: contextRootPath + '/ajaxupload?userCode=' + userCode + '&userName=' + userName, //对应的后台处理类的地址 
		data: formData, 
		processData: false, 
		contentType: false, 
		success: function (result) { 
			$(function () {
		        $.messager.alert("操作提示", result.errorMsg, "info", function () {
		        	// 上传成功再关闭上传窗口，上传不成功不关闭
		        	if(result.success){
		        		// 关闭弹窗
		        		$('#dlg').dialog('close');
		        		// datagrid重新加载数据
		        		$('#dg').datagrid('reload');	// reload the user data
		        	}
		        });
		    });
		},
		failure:function (result) {  
			$.messager.alert("操作提示", result, "error");
		}
	}); 
}

/**
 * 转换日期格式为 yyyy-MM-dd HH:mm:ss 的统一格式
 * @param timeStr
 * @returns
 */
function formatDateTimeString(timeStr){
	
	if(timeStr == null || timeStr == ''){
		return '';
	}
	
	// 先判断下时间格式
	// yyyy/MM/dd HH:mm
	// yyyy/MM/dd HH:mm:ss
	// yyyy-MM-dd HH:mm
	// yyyy-MM-dd HH:mm:ss
	
	var reg = /(\d+)/g;
	var r = timeStr.match(reg);
	if(r != null && r.length > 0){
		// r[0] 表示匹配到的全体
		
		var y = 1971;
		var m = 1;
		var d = 1;
		var h = 0;
		var mi = 0;
		var s = 0;
		
		if(r.length > 0){
			y = parseInt(r[0],10);
		}
		
		if(r.length > 1){
			m = parseInt(r[1],10);
		}
		
		if(r.length > 2){
			d = parseInt(r[2],10);
		}
		
		if(r.length > 3){
			h = parseInt(r[3],10);
		}
		
		if(r.length > 4){
			mi = parseInt(r[4],10);
		}
		
		if(r.length > 5){
			s = parseInt(r[5],10);
		}
		
		var newStr = '' + y + '-' + (m<10?('0'+m):m) + '-' + (d<10?('0'+d):d) + ' ' + (h<10?('0'+h):h) + ':' + (mi<10?('0'+mi):mi) + ':' + (s<10?('0'+s):s);
		return newStr;
	}else{
		return '';
	}
}

/**
 * 将easyui的datagrid中的代码翻译为汉字
 * @param val
 * @param row
 * @returns
 */
function formatTrueOrFalse(val,row){
	if(val == '1'){
		return '是';
	}else if(val == '0'){
		return '否';
	}
}

/**
 * 将操作列转换为播放音频
 * @param val
 * @param row
 * @returns
 */
function formatOperation(val,row){
	var fileId = row.fileId;
	if(fileId != null && fileId !== ''){
		return '<audio id="mp3_' + fileId + '" src="/audio/audioplay?fileId=' + fileId + '" controls="true"></audio>';
	}
}