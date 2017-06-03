package com.example;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RestController
public class AddDataController {

	
	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
	DB db = ctx.getBean(DB.class);
	DBCollection collect = null;
	String DBforInitiatives = "INITIATIVES", DBforVotes = "VOTES", DBforBlocks = "BLOCKS";
	Gson gson = new Gson();
	
	//Добавление инициативы, пришедшей от пользователя.
	@RequestMapping(value="/addinitiative",method=RequestMethod.POST)
    public void addNewInitive(@RequestBody Initiative initiative){
    	
    	collect = db.getCollection(DBforInitiatives);
    	BasicDBObject object = new BasicDBObject();
    	object.put("description", initiative.description);
    	object.put("variants", initiative.variants);
    	BasicDBObject whereQuery = new BasicDBObject();
    	whereQuery.put("description", initiative.description);
    	DBCursor cursor = collect.find(whereQuery);
    	if (!cursor.hasNext()){
    		collect.insert(object);
    	}
    	
    	
    }
	
	//Создание голоса, пришедшего от пользователя. Проверка на достоверность, добавление в список нераспределённых голосов
		@RequestMapping( path ="/addvote", method=RequestMethod.POST)
		public void voteCreator(@RequestBody Vote vote){
			boolean voteIsFirst = true;
			collect = db.getCollection(DBforVotes);
			BasicDBObject query = new BasicDBObject();
			List<BasicDBObject> listForQuery = new ArrayList<>();
			listForQuery.add(new BasicDBObject("dsaSign", vote.dsaSign));
			listForQuery.add(new BasicDBObject("initiative", vote.initiative));
			query.put("$and", listForQuery);
			if (collect.find(query) != null) {
				voteIsFirst = false;
			}	
			if (voteIsFirst){
				
				String tmpDsaSign=vote.dsaSign;
				byte[] sign=Base64.getMimeDecoder().decode(vote.dsaSign);
				byte[] pubKey=Base64.getMimeDecoder().decode(vote.publicKey);
				vote.dsaSign=null;
				boolean valid = false;
				try {
					valid = DigitalSign.verifySig(gson.toJson(vote).getBytes(), DigitalSign.convertKey(pubKey) , sign);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.err.println(valid);
				if(valid){
					vote.dsaSign=tmpDsaSign;
					GetDataConroller.votes.add(vote);
					BasicDBObject obj = new BasicDBObject();
					BasicDBObject object = new BasicDBObject();
			    	object.put("description", vote.initiative.description);
			    	object.put("variants", vote.initiative.variants);
					obj.put("dsaSign", vote.dsaSign);
					obj.put("initiative", object);
					obj.put("publicKey",vote.publicKey);
					obj.put("variant",vote.variant);
					collect.insert(obj);
				}  
			}	
		}
		
		@RequestMapping(value="/addblock",method=RequestMethod.POST)
		public void addNewBlock(@RequestBody Block block){
			collect = db.getCollection(DBforBlocks);
			DBCursor cursor = collect.find();
			Block lastBlock = new Block();
			while(cursor.hasNext()){
				lastBlock = gson.fromJson(gson.toJson(cursor.next()), Block.class);
			}
			if(Block.blockValidity(block, lastBlock, GetDataConroller.goal))
					GetDataConroller.b = block;
				
				
				
				BasicDBObject object = new BasicDBObject();
				object.put("index", GetDataConroller.b.getIndex());
				object.put("timestamp",GetDataConroller.b.getTimestamp());
				object.put("voteHash",GetDataConroller.b.getVoteHash());
				object.put("votes",GetDataConroller.b.votes);
				object.put("hash",GetDataConroller.b.getHash());
				object.put("previousHash",GetDataConroller.b.getPreviousHash());
				object.put("previousBlock",GetDataConroller.b.getPreviousBlock());
				object.put("nonce",GetDataConroller.b.nonce);
				collect.insert(object);
				
		}
		
}
