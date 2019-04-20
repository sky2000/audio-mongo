package com.yan.audio.mongo.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.yan.audio.mongo.schema.AudioMain;
import com.yan.audio.mongo.service.facade.AudioService;
import com.yan.audio.mongo.vo.ResponseVo;

@Controller
public class FileUploadController {
	
	@Autowired
	private AudioService audioService;
	
	@RequestMapping("/ajaxfile")
	public String ajaxfile() {
		return "ajaxfile";
	}
	
	@RequestMapping("/filelist")
	public String filelist() {
		return "filelist";
	}
	
	@RequestMapping("/audiopage")
	public ModelAndView audiopage(String fileId) {
		ModelAndView mav = new ModelAndView("audiopage");
		mav.addObject("fileId", fileId);
		return mav;
	}
	
	@RequestMapping("/ajaxupload")
	@ResponseBody
	public ResponseVo ajaxupload(@RequestParam("file") MultipartFile file, String userCode, String userName) {
		ResponseVo response = new ResponseVo();
		response.setSuccess(false);
		
		// uuid
		//String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		
		String fileName = file.getOriginalFilename();
		if (fileName.indexOf("\\") != -1) {
			fileName = fileName.substring(fileName.lastIndexOf("\\"));
		}
		
		// suffix
		String suffix = null;
		// 不带后缀名得文件名称
		String fileNameWithoutSuffix = null;
		if(fileName.lastIndexOf(".") > 0) {
			suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
			// 后缀名都转为小写存储
			suffix = suffix.toLowerCase();
			
			// 不带后缀名得文件名称
			fileNameWithoutSuffix = fileName.substring(0, fileName.lastIndexOf("."));
		}else {
			fileNameWithoutSuffix = fileName;
		}
		
		// 组装ImageMain对象
		AudioMain audioMain = new AudioMain();
		audioMain.setName(fileNameWithoutSuffix);
		audioMain.setSuffix(suffix);
		audioMain.setFullName(fileName);
		
		audioMain.setUserCode(userCode);
		audioMain.setUserName(userName);
		audioMain.setValidStatus("1");
		audioMain.setInsertTime(new Date());
		audioMain.setUpdateTime(new Date());

		
		try {
			String fileId = audioService.insertFile(fileNameWithoutSuffix, file.getInputStream());
			audioMain.setFileId(fileId);
			
			// 将文件数据写入数据库
			String id = audioService.insertAudio(audioMain);
			audioMain.setId(id);
			
			response.setSuccess(true);
			response.setErrorMsg("上传成功");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			response.setSuccess(false);
			response.setErrorMsg(e.getMessage());
			
		}
		
		response.setResult(audioMain);
		
		return response;
	}

	
	@RequestMapping("/audioplay")
	public @ResponseBody void audioplay(String fileId, HttpServletResponse response) throws Exception {

		ServletOutputStream out = response.getOutputStream();
		
		byte[] bytesToWriteTo = audioService.readFile(fileId);
		
		out.write(bytesToWriteTo);
		out.flush();
		out.close();
	}
}