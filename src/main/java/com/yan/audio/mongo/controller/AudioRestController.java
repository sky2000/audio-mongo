package com.yan.audio.mongo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.yan.audio.mongo.schema.AudioMain;
import com.yan.audio.mongo.service.facade.AudioService;
import com.yan.audio.mongo.vo.DataGridVo;
import com.yan.audio.mongo.vo.ResponseVo;


@RestController
public class AudioRestController {
	
	@Autowired
	private AudioService audioService;
	
	@RequestMapping("/audiodatagrid")
	@ResponseBody
	public DataGridVo audioDataGrid(String name, Integer page, Integer rows, String validStatus) {
		DataGridVo dataGrid = new DataGridVo();
		dataGrid.setSuccess(false);
		
		if(rows <= 0){
			rows = 10;
		}
		
		if(page < 1){
			page = 1;
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("name", name);
		condition.put("validStatus", validStatus);
		condition.put("page", page);
		condition.put("rows", rows);
		
		List<AudioMain> audioMains = audioService.findAudiosByCondition(condition);
		Long total = audioService.countAudiosByCondition(condition);
		
		dataGrid.setSuccess(true);
		dataGrid.setErrorMsg("");
		dataGrid.setTotal(total.intValue());
		dataGrid.setRows(audioMains);
		
		return dataGrid;
	}
	
	@RequestMapping("/queryAudios")
	@ResponseBody
	public ResponseVo queryAudios(HttpServletResponse res,String name, Integer pageNo, Integer pageSize, String validStatus) {
		ResponseVo responseVo = new ResponseVo();
		
		responseVo.setSuccess(false);
		
		if(pageSize <= 0){
			pageSize = 10;
		}
		
		if(pageNo < 1){
			pageNo = 1;
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("name", name);
		condition.put("validStatus", validStatus);
		condition.put("page", pageNo);
		condition.put("rows", pageSize);
		
		List<AudioMain> audioMains = audioService.findAudiosByCondition(condition);
		// 总条数
		Long total = audioService.countAudiosByCondition(condition);
		
		responseVo.setSuccess(true);
		responseVo.setErrorMsg("");
		
		responseVo.setPageNo(pageNo);
		responseVo.setPageSize(pageSize);
		
		responseVo.setTotalCount(total);
		
		// 总页数
		Long totalPageCount = 1L;
		
		if(total % pageSize == 0) {
			totalPageCount = total / pageSize;
		}else {
			totalPageCount = total / pageSize + 1;
		}
		responseVo.setTotalPageCount(totalPageCount);
		
		responseVo.setResults(audioMains);
		res.setHeader("Access-Control-Allow-Origin","*");
		res.setHeader("Cache-Control","no-cache");
		
		return responseVo;
	}
	
	@RequestMapping("/deleteAudio")
	@ResponseBody
	public ResponseVo deleteAudio(String id) {
		ResponseVo responseVo = new ResponseVo();
		
		responseVo.setSuccess(false);
		
		audioService.deleteFile(id);
		
		responseVo.setSuccess(true);
		
		return responseVo;
	}
}