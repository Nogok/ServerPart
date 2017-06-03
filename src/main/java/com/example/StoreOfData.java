package com.example;

import java.util.ArrayList;
import java.util.List;

public class StoreOfData {

	public static Vote v; //Голос, с которым идёт работа
	public static Block b = new Block(null); //Блок, с которым идёт работа
	public static List<Initiative> initiatives=new ArrayList<>(); //Список инициатив
    public static List<Vote> votes = new ArrayList<>(); //Список нераспределённых в блок голосов
    public static List<Block> chain = new ArrayList<>(); //Цепочка блоков
    public static String goal = "00007ffaff3939fbca4eb074249dc7d39b1d1ee4fed2da3f87430703cac5d250a"; //Число для условия blockhash < goal
}
