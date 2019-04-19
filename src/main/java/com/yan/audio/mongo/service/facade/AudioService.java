package com.yan.audio.mongo.service.facade;

import java.util.List;
import java.util.Map;

import com.yan.audio.mongo.schema.AudioMain;

public interface AudioService {

	List<AudioMain> findAudiosByCondition(Map<String, Object> condition);
	
	Long countAudiosByCondition(Map<String, Object> condition);

	String insertAudio(AudioMain audioMain);
	
}
