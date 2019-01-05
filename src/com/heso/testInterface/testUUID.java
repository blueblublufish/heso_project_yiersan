package com.heso.testInterface;

import java.util.UUID;

import org.omg.CORBA.PUBLIC_MEMBER;
 
public class testUUID {
	     
	public static void main(String[] args) {
	     /* Thread t = new Thread(){
	    	  public void run() {
	    		  world();
			}
	      };
	      t.run();
	      System.out.println("hello");*/
		String s;
		//System.out.println("s="+s);
		
	}
	 public  void change(String str,char ch[]){
		str = "test ok";
		ch[0] ='g';
		
		
	}
	static void world(){
		System.out.println("world");
	}
	static int getvalue(int i){
		int result = 0;
		switch (i) {
		case 1:
			
			result = result +i;
		case 2:
			
			result = result +i*2;
		case 3:
	
			result = result +i*3;

		 
		}
		return result;
		
	}
	
}
