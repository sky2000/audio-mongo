package com.yan.audio.mongo.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.yan.audio.mongo.schema.AudioMain;
import com.yan.audio.mongo.service.facade.AudioService;
import com.yan.audio.mongo.util.SchemaUtil;

@Service
public class AudioServiceSpringImpl implements AudioService{

	@Value("${mongodb.user}")
	private String user;
	
	@Value("${mongodb.dbUserDefined}")
	private String dbUserDefined;
	
	@Value("${mongodb.password}")
	private String password;
	
	@Value("${mongodb.ip}")
	private String ip;
	
	@Value("${mongodb.port}")
	private Integer port;
	
	@Value("${mongodb.database}")
	private String db;
	
	public String insertAudio(AudioMain audioMain){

		//To connect to a single MongoDB instance:
		//You can explicitly specify the hostname and the port:
		MongoCredential credential = MongoCredential.createCredential(user, dbUserDefined, password.toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(ip, port),
		                                         Arrays.asList(credential));
		//Access a Database
		MongoDatabase database = mongoClient.getDatabase(db);
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("AudioMain");
		
		//Create a Document
		Document doc = SchemaUtil.audioToDocument(audioMain);
		
		//Insert a Document
		collection.insertOne(doc);
		 
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		mongoClient.close();
		return id;
	}
	
	public List<AudioMain> findAudiosByCondition(Map<String, Object> condition){
		List<AudioMain> audioMains = null;
		
		if(condition != null && condition.size() > 0) {
			
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoCredential credential = MongoCredential.createCredential(user, dbUserDefined, password.toCharArray());
			MongoClient mongoClient = new MongoClient(new ServerAddress(ip, port),
			                                         Arrays.asList(credential));
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase(db);
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("AudioMain");
			
			List<Bson> bsons = new ArrayList<Bson>(0);
			
			//分页的页码
			int page = 1;
			//分页每页条数
			int rows = 10;
			
			for(Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				
				//因为查询条件改为了and，所以当条件为空字符串的时候不向查询条件中拼写
				if(value != null && !"".equals(value.toString().trim())){
					if("id".equals(key)) {
						bsons.add(Filters.eq("_id", new ObjectId(value.toString())));
					}else if ("insertTimeStart".equals(key)) {
						bsons.add(Filters.gte("insertTime", value.toString()));
					}else if ("insertTimeEnd".equals(key)) {
						bsons.add(Filters.lte("insertTime", value.toString()));
					}else if ("userCode".equals(key)
							|| "validStatus".equals(key)) {
						bsons.add(Filters.eq(key, value.toString()));
					}else if ("page".equals(key)) {
						page = Integer.parseInt(value.toString());
					}else if ("rows".equals(key)) {
						rows = Integer.parseInt(value.toString());
					}else {
						//其他的进行右模糊查询
						//参考下regex的使用
						//i  　如果设置了这个修饰符，模式中的字母会进行大小写不敏感匹配。
						//m   默认情况下，PCRE 认为目标字符串是由单行字符组成的(然而实际上它可能会包含多行).如果目标字符串 中没有 "\n"字符，或者模式中没有出现“行首”/“行末”字符，设置这个修饰符不产生任何影响。
						//s    如果设置了这个修饰符，模式中的点号元字符匹配所有字符，包含换行符。如果没有这个修饰符，点号不匹配换行符。
						//x    如果设置了这个修饰符，模式中的没有经过转义的或不在字符类中的空白数据字符总会被忽略，并且位于一个未转义的字符类外部的#字符和下一个换行符之间的字符也被忽略。 这个修饰符使被编译模式中可以包含注释。 注意：这仅用于数据字符。 空白字符 还是不能在模式的特殊字符序列中出现，比如序列 。
						//注：JavaScript只提供了i和m选项，x和s选项必须使用$regex操作符
						
						//在命令行的时候pattern左右使用//包起来，是因为通过//来表示包起来的是pattern，但是如果java中再将//拼接到字符串中，那么//就会当做pattern的一部分去匹配，就会出现问题
						//debug的时候，发现pattern中居然包括//这是不对的
						bsons.add(Filters.regex(key, "" + value.toString() + ".*", "i"));
					}
				}
				
			}
			
			int limit = rows;
			int skip = 0;
			if(page >= 0){
				skip = (page - 1) * rows;
			}
			
			//如果要在find中传入bson数组，那么bson数组必须不能为空
			List<Document> docs = null;
			if(bsons != null && bsons.size() > 0){
				docs = collection.find(Filters.and(bsons)).limit(limit).skip(skip).sort(new Document("firstInterviewTime", -1)).into(new ArrayList<Document>());
			}else{
				docs = collection.find().limit(limit).skip(skip).sort(new Document("firstInterviewTime", -1)).into(new ArrayList<Document>());
			}
			
			if(docs != null){
				audioMains = new ArrayList<AudioMain>();
				
				for(Document doc : docs){
					AudioMain audioMain = SchemaUtil.documentToAudio(doc);
					
					audioMains.add(audioMain);
				}
			}
			mongoClient.close();
		}
		return audioMains;
	}
	
	public Long countAudiosByCondition(Map<String, Object> condition){
		long count = 0L;
		
		if(condition != null && condition.size() > 0) {
			
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoCredential credential = MongoCredential.createCredential(user, dbUserDefined, password.toCharArray());
			MongoClient mongoClient = new MongoClient(new ServerAddress(ip, port),
			                                         Arrays.asList(credential));
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase(db);
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("AudioMain");
			
			List<Bson> bsons = new ArrayList<Bson>(0);
			
			for(Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				
				//因为查询条件改为了and，所以当条件为空字符串的时候不向查询条件中拼写
				if(value != null && !"".equals(value.toString().trim())){
					if("id".equals(key)) {
						bsons.add(Filters.eq("_id", new ObjectId(value.toString())));
					}else if ("insertTimeStart".equals(key)) {
						bsons.add(Filters.gte("insertTime", value.toString()));
					}else if ("insertTimeEnd".equals(key)) {
						bsons.add(Filters.lte("insertTime", value.toString()));
					}else if ("userCode".equals(key)
							|| "validStatus".equals(key)) {
						bsons.add(Filters.eq(key, value.toString()));
					}else if ("page".equals(key) || "rows".equals(key)) {
						//这两个参数是分页参数，在分页查询数据时会用到，但是在查询总条数的时候并不会用到，但是也不能拼接到查询语句中
					}else {
						//其他的进行右模糊查询
						//参考下regex的使用
						//i  　如果设置了这个修饰符，模式中的字母会进行大小写不敏感匹配。
						//m   默认情况下，PCRE 认为目标字符串是由单行字符组成的(然而实际上它可能会包含多行).如果目标字符串 中没有 "\n"字符，或者模式中没有出现“行首”/“行末”字符，设置这个修饰符不产生任何影响。
						//s    如果设置了这个修饰符，模式中的点号元字符匹配所有字符，包含换行符。如果没有这个修饰符，点号不匹配换行符。
						//x    如果设置了这个修饰符，模式中的没有经过转义的或不在字符类中的空白数据字符总会被忽略，并且位于一个未转义的字符类外部的#字符和下一个换行符之间的字符也被忽略。 这个修饰符使被编译模式中可以包含注释。 注意：这仅用于数据字符。 空白字符 还是不能在模式的特殊字符序列中出现，比如序列 。
						//注：JavaScript只提供了i和m选项，x和s选项必须使用$regex操作符
						
						//在命令行的时候pattern左右使用//包起来，是因为通过//来表示包起来的是pattern，但是如果java中再将//拼接到字符串中，那么//就会当做pattern的一部分去匹配，就会出现问题
						//debug的时候，发现pattern中居然包括//这是不对的
						bsons.add(Filters.regex(key, "" + value.toString() + ".*", "i"));
					}
				}
				
			}
			
			//如果要在find中传入bson数组，那么bson数组必须不能为空
			if(bsons != null && bsons.size() > 0){
				count = collection.count(Filters.and(bsons));
			}else{
				count = collection.count();
			}
			mongoClient.close();
		}
		
		return count;
	}

	public String insertFile(String fileName, InputStream streamToUploadFrom) {
		String id = null;
		
		//To connect to a single MongoDB instance:
		//You can explicitly specify the hostname and the port:
		MongoCredential credential = MongoCredential.createCredential(user, dbUserDefined, password.toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(ip, port),
		                                         Arrays.asList(credential));
		//Access a Database
		MongoDatabase database = mongoClient.getDatabase(db);
		
		// Create a gridFSBucket using the default bucket name "fs"
		GridFSBucket gridFSBucket = GridFSBuckets.create(database);
		//GridFSBucket gridFSFilesBucket = GridFSBuckets.create(database, "files");
		
		// Get the input stream

		try {
		    //InputStream streamToUploadFrom = new FileInputStream(new File("/tmp/mongodb-tutorial.pdf"));
		    // Create some custom options
		    GridFSUploadOptions options = new GridFSUploadOptions()
		                                        .chunkSizeBytes(358400)
		                                        .metadata(new Document("type", "presentation"));

		    ObjectId fileId = gridFSBucket.uploadFromStream(fileName, streamToUploadFrom, options);
		    id = fileId.toString();
		} catch (Exception e){
		   // handle exception
		}finally {
			if(streamToUploadFrom != null) {
				try {
					streamToUploadFrom.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(mongoClient != null) {
				mongoClient.close();
			}
		}
		return id;
	}
	
	public void readFile(String fileId, OutputStream outputStream) {
		
		if(fileId == null || "".equals(fileId)) {
			return ;
		}
		
		//To connect to a single MongoDB instance:
		//You can explicitly specify the hostname and the port:
		MongoCredential credential = MongoCredential.createCredential(user, dbUserDefined, password.toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(ip, port),
		                                         Arrays.asList(credential));
		//Access a Database
		MongoDatabase database = mongoClient.getDatabase(db);
		
		// Create a gridFSBucket using the default bucket name "fs"
		GridFSBucket gridFSBucket = GridFSBuckets.create(database);
		//GridFSBucket gridFSFilesBucket = GridFSBuckets.create(database, "files");
		
		
		ObjectId fId = new ObjectId(fileId.toString()); //The id of a file uploaded to GridFS, initialize to valid file id
		try {
			//FileOutputStream streamToDownloadTo = new FileOutputStream("/tmp/mongodb-tutorial.pdf");
			gridFSBucket.downloadToStream(fId, outputStream);
			//streamToDownloadTo.close();
		} catch (Exception e) {
			// handle exception
			e.printStackTrace();
		}
	}
	
	
	public byte[] readFile(String fileId) {
		byte[] bytesToWriteTo = new byte[0];
		
		if(fileId == null || "".equals(fileId)) {
			return bytesToWriteTo;
		}
		
		//To connect to a single MongoDB instance:
		//You can explicitly specify the hostname and the port:
		MongoCredential credential = MongoCredential.createCredential(user, dbUserDefined, password.toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(ip, port),
		                                         Arrays.asList(credential));
		//Access a Database
		MongoDatabase database = mongoClient.getDatabase(db);
		
		// Create a gridFSBucket using the default bucket name "fs"
		GridFSBucket gridFSBucket = GridFSBuckets.create(database);
		//GridFSBucket gridFSFilesBucket = GridFSBuckets.create(database, "files");
		
		ObjectId fId = new ObjectId(fileId.toString()); //The id of a file uploaded to GridFS, initialize to valid file id
		try {
			
			GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fId);
			int fileLength = (int) downloadStream.getGridFSFile().getLength();
			bytesToWriteTo = new byte[fileLength];
			downloadStream.read(bytesToWriteTo);
			downloadStream.close();
		} catch (Exception e) {
			// handle exception
			e.printStackTrace();
		}
		mongoClient.close();
		
		return bytesToWriteTo;
	}

	@Override
	public void deleteFile(String id) {
		// TODO Auto-generated method stub

		//To connect to a single MongoDB instance:
		//You can explicitly specify the hostname and the port:
		MongoCredential credential = MongoCredential.createCredential(user, dbUserDefined, password.toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(ip, port),
		                                         Arrays.asList(credential));
		//Access a Database
		MongoDatabase database = mongoClient.getDatabase(db);
		
		GridFSBucket gridFSBucket = GridFSBuckets.create(database);
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("AudioMain");
		Bson bson = Filters.eq("_id", new ObjectId(id));
		Document doc = collection.find(bson).first();
		
		String fileId = (String) doc.getString("fileId");
		
		gridFSBucket.delete(new ObjectId(fileId));
		
		collection.deleteOne(bson);
		
		mongoClient.close();
	}
	
	
}
