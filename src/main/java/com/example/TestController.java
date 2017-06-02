package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RestController
public class TestController {
	public static int counter = 0;

	@RequestMapping("/")
	public Long test(@RequestParam(value = "name", required = false, defaultValue = "EmptyName") String name) {
		// получаем экземпляр БД
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		DB db = ctx.getBean(DB.class);
		// создаем пробный объект
		TestData test = new TestData(name, counter++);
		DBCollection collect = null;
		try {
			collect = db.getCollection("TestData");
			BasicDBObject obj = new BasicDBObject();
			obj.put("name", name);
			obj.put("id", counter);
			collect.insert(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collect != null ? collect.count() : 0;
	}

	@RequestMapping("/list")
	public String list() {
		String result = "";
		// получаем экземпляр БД
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		DB db = ctx.getBean(DB.class); 
		DBCollection collect = null;
		collect = db.getCollection("TestData");
		DBCursor cursor = collect.find();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			result += obj.get("name") + "<BR/>";
		}
		return result;
	}

}
