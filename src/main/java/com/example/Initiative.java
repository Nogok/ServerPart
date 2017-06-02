package com.example;

import java.util.Arrays;

public class Initiative {
   public String description;
   public String[] variants;
   public Initiative() {
   }
   public Initiative(String description,String[] variants){
	   this.description=description;
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
		if (initiative.description.equals(this.description) && Arrays.equals(this.variants ,initiative.variants)) return true;
		return false;
	}
		   
}
