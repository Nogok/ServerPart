package com.example;

import java.util.Arrays;

public class Initiative {
  
   public String description;
   public String name;
   public String[] variants;
   
   public Initiative() {
   }
   
   public Initiative(String description,String name,String[] variants){
	   this.description=description;
	   this.name = name;
	   this.variants=variants;
   }
   
   @Override
	public boolean equals(Object obj) {
	   if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Initiative initiative = (Initiative) obj;
		if (initiative.description.equals(this.description) &&
				Arrays.equals(this.variants ,initiative.variants)
				&& initiative.name.equals(this.name)) return true;
		return false;
	}
		   
}
