package com.yan.audio.mongo.config;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class MongoConfig {

	@Autowired
	private MongoSettingsProperties properties;
	 
	@Bean
	public MongoDbFactory mongoDbFactory() {
		// 客户端配置(暂未配置集群)
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(properties.getMaxConnectionsPerHost());
		builder.minConnectionsPerHost(properties.getMinConnectionsPerHost());
		builder.threadsAllowedToBlockForConnectionMultiplier(
				properties.getThreadsAllowedToBlockForConnectionMultiplier());
		builder.serverSelectionTimeout(properties.getServerSelectionTimeout());
		builder.maxWaitTime(properties.getMaxWaitTime());
		builder.maxConnectionIdleTime(properties.getMaxConnectionIdleTime());
		builder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime());
		builder.connectTimeout(properties.getConnectTimeout());
		builder.socketTimeout(properties.getSocketTimeout());
		// builder.socketKeepAlive(properties.getSocketKeepAlive());
		MongoClientOptions mongoClientOptions = builder.build();
		// MongoDB地址列表
		List<ServerAddress> serverAddresses = new ArrayList<>();
		String host = properties.getAddress();
		Integer port = properties.getPort();
		ServerAddress serverAddress = new ServerAddress(host, port);
		serverAddresses.add(serverAddress);
		// 连接认证
		MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(properties.getUsername(),
				properties.getAuthenticationDatabase() != null ? properties.getAuthenticationDatabase()
						: properties.getDatabase(),
				properties.getPassword().toCharArray());
		// 创建客户端和Factory
		MongoClient mongoClient = new MongoClient(serverAddresses, Arrays.asList(mongoCredential), mongoClientOptions);
		
		// construct a MongoClient with that credential. 
		// Using the new (since 3.7) MongoClient API:
//		MongoClient mongoClient1 = MongoClients.create(
//		        MongoClientSettings.builder()
//		                .applyToClusterSettings(builder -> 
//		                        builder.hosts(Arrays.asList(new ServerAddress("host1", 27017))))
//		                .credential(credential)
//		                .build());
		
		MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, properties.getDatabase());
		return mongoDbFactory;
	}
	
	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}
	
	
}
