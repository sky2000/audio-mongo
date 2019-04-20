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
				var refuuid = row.uuid;
				$.post(contextRootPath + '/deletefile?refuuid=' + refuuid, {},function(result){
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
 * 删除某一行数据
 * @param rowId
 * @returns
 */
function removeRow(rowId){
	//console.log("remove row, rowId:" + rowId);
	
	$("#" + rowId).detach();
	
//	detach() 方法移除被选元素，包括所有文本和子节点。
//	这个方法会保留 jQuery 对象中的匹配的元素，因而可以在将来再使用这些匹配的元素。
//	detach() 会保留所有绑定的事件、附加的数据，这一点与 remove() 不同。
	
//	remove() 方法移除被选元素，包括所有文本和子节点。
//	该方法不会把匹配的元素从 jQuery 对象中删除，因而可以在将来再使用这些匹配的元素。
//	但除了这个元素本身得以保留之外，remove() 不会保留元素的 jQuery 数据。其他的比如绑定的事件、附加的数据等都会被移除。这一点与 detach() 不同。
	
}

/**
 * 生成长度为length的随机Id
 */
function randomId(length){
	var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < length; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    
    var randomId = s.join("");
    return randomId;
}

/**
 * 生成uuid
 * @returns
 */
function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";
 
    var uuid = s.join("");
    return uuid;
}

/**
 * 将easyui的datagrid中的日期格式 转换为yyyy-MM-dd HH:mm:ss 的统一格式
 * @param val
 * @param row
 * @returns
 */
function formatDateTime(val,row){
	return '';
	//return formatDateTimeString(val);
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