package com.didipark.utils;

public class TimeUtil {
	static long startTime=0;
	static long endTime=0;
	
	public static void start(){
		startTime=System.currentTimeMillis();
	}
	public static  void finish(String name){
		endTime=System.currentTimeMillis();
		System.out.println(name+(endTime-startTime)/1000+"s");
	}
}
