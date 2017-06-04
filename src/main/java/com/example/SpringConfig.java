package com.example;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;

@Configuration
public class SpringConfig {
	//БД NoSQL
    @Bean
    public DB getDb() throws UnknownHostException {
    	MongoClientURI uri = new MongoClientURI(System.getenv("MONGODB_URI"));
    	 MongoClient mongoClient = new MongoClient(uri);
         
         String dbname = uri.getDatabase();
         DB db = mongoClient.getDB(dbname);

         MongoCredential credential = MongoCredential.createCredential(uri.getUsername(),dbname,uri.getPassword());
         MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
         return db;
    }
}

