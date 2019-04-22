package com.yan.audio.mongo.service.facade;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.yan.audio.mongo.schema.AudioMain;

public interface AudioService {

	List<AudioMain> findAudiosByCondition(Map<String, Object> condition);
	
	Long countAudiosByCondition(Map<String, Object> condition);

	String insertAudio(AudioMain audioMain);
	
	String insertFile(String fileName, InputStream streamToUploadFrom);
	
	void readFile(String fileId, OutputStream outputStream);
	
	byte[] readFile(String fileId);
	
	void deleteFile(String fileId);
}
