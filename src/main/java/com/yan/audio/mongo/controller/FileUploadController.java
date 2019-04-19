package com.yan.audio.mongo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yan.audio.mongo.schema.AudioMain;

@Controller
public class FileUploadController {
	
	@RequestMapping("/ajaxfile")
	public String ajaxfile() {
		return "ajaxfile";
	}
	
	@RequestMapping("/filelist")
	public String filelist() {
		return "filelist";
	}

	@RequestMapping("/ajaxupload")
	@ResponseBody
	public String ajaxupload(@RequestParam("file") MultipartFile file, String userCode) {
		// 计算文件的md5值
		String md5Hex = null;
		try {
			md5Hex = DigestUtils.md5DigestAsHex(file.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(md5Hex != null && !"".equals(md5Hex.trim())) {
			
			String fileName = file.getOriginalFilename();
			if (fileName.indexOf("\\") != -1) {
				fileName = fileName.substring(fileName.lastIndexOf("\\"));
			}
			
			// suffix
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
			// 后缀名都转为小写存储
			suffix = suffix.toLowerCase();
			
			// 不带后缀名得文件名称
			String fileNameWithoutSuffix = fileName.substring(0, fileName.lastIndexOf("."));
			
			// 组装ImageMain对象
			AudioMain audioMain = new AudioMain();
			

			// 将文件数据写入数据库

			
		}
		return "上传成功!";
	}

}