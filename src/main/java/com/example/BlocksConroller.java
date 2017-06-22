package com.example;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@RestController
public class BlocksConroller {
	
	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class); //Получение контекста
	DB db = ctx.getBean(DB.class);//Получение экземпляра Базы Данных
	DBCollection collect = null; // Создание объекта коллекции (NoSQL)
	String DBforInitiatives = "INITIATIVES", DBforVotes = "VOTES", DBforBlocks = "BLOCKS"; //Названия коллекций
	Gson gson = new Gson(); //Json converter для записи в БД
	public static String goal = "0ff4fffaff3939fbca4eb074249dc7d39b1d1ee4fed2da3f87430703cac5d250a"; //Число для условия blockhash < goal
			
    
	//Запрос на получение числа goal
	@RequestMapping(path = "/getgoal")
	public String getGoal(){
		return goal;
	}

	//Запрос на получение последнего блока из цепочки
	@RequestMapping(value = "/getBlock", method={RequestMethod.POST,RequestMethod.GET})
	public Block getBlock(){
		collect = db.getCollection(DBforBlocks);
		System.err.println(collect.count());
		if (collect.count() == 0) {
			//Genesis block
			Initiative i=new Initiative("", new String[]{""});
			Vote v=new Vote(i,0,"");
			ArrayList<Vote> voteList=new ArrayList<Vote>();
			voteList.add(v);
			addNewBlock(new Block(voteList,goal));
		}
		System.err.println(collect.count());
		DBCursor cursor = collect.find();
		ArrayList<Block> blocks=new ArrayList<>();
		while(cursor.hasNext()){
			Block bb = gson.fromJson(gson.toJson(cursor.next()), Block.class);
			blocks.add(bb);
		}
		if(blocks.size()>0)
		return blocks.get(blocks.size()-1); // TODO: investigate it!
		else return null;
	}
	//Очистка базы данных с блоками TODO Убрать!
	@RequestMapping(value = "/cleanBlocksDB", method={RequestMethod.POST,RequestMethod.GET})
	public void cleanDB(){
		collect = db.getCollection(DBforBlocks);
		collect.drop();
	}

	 //Запрос на получение списка всех блоков
    @RequestMapping(value="/getAllBlocks",method={RequestMethod.POST,RequestMethod.GET})
    public List<Block> getAllBlocks(){
    	ArrayList<Block> blocks = new ArrayList<>();
    	collect = db.getCollection(DBforBlocks);
    	System.err.println("Collection size "+collect.count());
    	DBCursor cursor = collect.find();
    	while (cursor.hasNext()){
    		Block bb = gson.fromJson(gson.toJson(cursor.next()), Block.class);
			blocks.add(bb);
    	}
    	return blocks;
    }
    @RequestMapping(value="/getChain",method={RequestMethod.POST,RequestMethod.GET})
    public ArrayList<Block> getChain(){
    	ArrayList<Block> blocks = new ArrayList<>();
    	collect = db.getCollection(DBforBlocks);
    	String previousHash = "0";
    	BasicDBObject basicDBObject;
    	DBCursor cursor;
    	Block b;
    	for(int i = 0; i < collect.count(); i++){
    		basicDBObject = new BasicDBObject("previousHash",previousHash);
    		cursor = collect.find(basicDBObject);
    		b = gson.fromJson((gson.toJson(cursor.next())),Block.class);
    		blocks.add(b);
    		previousHash = b.getHash();
    	}
    	return blocks;
    	
    }
    

	//Добавление блока в БД
	@RequestMapping(value="/addblock",method=RequestMethod.POST)
	public void addNewBlock(@RequestBody Block block){
		collect = db.getCollection(DBforBlocks);
		DBCursor cursor = collect.find();
		Block lastBlock = null;
		while(cursor.hasNext()){
			lastBlock = gson.fromJson(gson.toJson(cursor.next()), Block.class);
		}
		if(collect.count() == 0 || Block.blockValidity(block, lastBlock, block.goal)){
			collect.insert((DBObject)JSON.parse(gson.toJson(block))); 				
		}
	}

}
