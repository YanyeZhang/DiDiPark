package com.zhangyanye.didipark.pojo;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Id;

public class Carport implements Serializable {
	@Id
	private int db_id;
	private static final long serialVersionUID = 1L;
	private int id;
	private String addr;
	private String describe;
	private int num;
	private double latitude, longitude;
	private int perHoursMoney;
	private int user_id;
	private int user_num;
	private String state;

	public Carport(){
		
	}
	


	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setlongitude(double longitude) {
		this.longitude = longitude;
	}

	

	public int getUser_id() {
		return user_id;
	}



	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getPerHoursMoney() {
		return perHoursMoney;
	}

	public void setPerHoursMoney(int perHoursMoney) {
		this.perHoursMoney = perHoursMoney;
	}

	public int getDb_id() {
		return db_id;
	}

	public void setDb_id(int db_id) {
		this.db_id = db_id;
	}

	public int getUser_num() {
		return user_num;
	}

	public void setUser_num(int user_num) {
		this.user_num = user_num;
	}
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
