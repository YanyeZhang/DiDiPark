package com.didipark.pojo;

import java.io.Serializable;

public class Carport implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String addr;
	private String describe;
	private int num;
	private double latitude, longitude;
	private int perHoursMoney;
	private int user_id;
	private String state;

  public Carport() {
	
}
	/*
	 * param 地址，描述，数量，维度，经度，时价，用户
	 */
	public Carport(String addr, String describe, int num, double latitude,
			double longitude, int perHoursMoney,int user_id,String state) {
		super();
		this.addr = addr;
		this.describe = describe;
		this.num = num;
		this.latitude = latitude;
		this.longitude = longitude;
		this.perHoursMoney = perHoursMoney;
		this.user_id=user_id;
		this.state=state;
	}
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
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

}
