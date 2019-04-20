package com.yan.audio.mongo.util;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.yan.audio.mongo.schema.AudioMain;

public class SchemaUtil {

	public static Document audioToDocument(AudioMain audioMain) {
		Document doc = null;
		if(audioMain != null) {
			doc = new Document();
			
			if(audioMain.getId() != null && !"".equals(audioMain.getId().trim())) {
				doc.append("_id", new ObjectId(audioMain.getId()));
			}
			doc.append("name", audioMain.getName());
			doc.append("fileId", audioMain.getFileId());
			doc.append("suffix", audioMain.getSuffix());
			doc.append("fullName", audioMain.getFullName());
			
			doc.append("userCode", audioMain.getUserCode());
			doc.append("userName", audioMain.getUserName());
			doc.append("validStatus", audioMain.getValidStatus());
			doc.append("insertTime", audioMain.getInsertTime());
			doc.append("updateTime", audioMain.getUpdateTime());
		}
		return doc;
	}
	
	public static AudioMain documentToAudio(Document doc) {
		AudioMain audioMain = null;
		if(doc != null) {
			audioMain = new AudioMain();
			
			Object objectId = doc.get("_id");
			if(objectId != null) {
				audioMain.setId(objectId.toString());
			}
			audioMain.setName(doc.getString("name"));
			audioMain.setFileId(doc.getString("fileId"));
			audioMain.setSuffix(doc.getString("suffix"));
			audioMain.setFullName(doc.getString("fullName"));
			
			audioMain.setUserCode(doc.getString("userCode"));
			audioMain.setUserName(doc.getString("userName"));
			audioMain.setValidStatus(doc.getString("validStatus"));
			audioMain.setInsertTime(doc.getDate("insertTime"));
			audioMain.setUpdateTime(doc.getDate("updateTime"));
		}
		return audioMain;
	}
	
}
