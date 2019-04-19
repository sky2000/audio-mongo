package com.yan.audio.mongo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yan.audio.mongo.schema.AudioMain;
import com.yan.audio.mongo.service.facade.AudioService;
import com.yan.audio.mongo.vo.DataGridVo;

@RestController
public class AudioRestController {
	
	@Autowired
	private AudioService audioService;
	
	@RequestMapping("/audiodatagrid")
	@ResponseBody
	public DataGridVo audioDataGrid(Integer page, Integer rows, String validStatus) {
		DataGridVo dataGrid = new DataGridVo();
		dataGrid.setSuccess(false);
		
		int offset = 0;
		int pageSize = 10;
		
		if(rows > 0){
			pageSize = rows;
		}
		
		if(page > 0){
			offset = (page - 1) * pageSize;
		}
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("validStatus", validStatus);
		condition.put("offset", offset);
		condition.put("pageSize", pageSize);
		
		List<AudioMain> audioMains = audioService.findAudiosByCondition(condition);
		Long total = audioService.countAudiosByCondition(condition);
		
		dataGrid.setSuccess(true);
		dataGrid.setErrorMsg("");
		dataGrid.setTotal(total.intValue());
		dataGrid.setRows(audioMains);
		
		return dataGrid;
	}
}