package com.didipark.pojo;

import java.io.Serializable;

public class Order implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private int carport_id;
	private int user_id;
	private String time;
	private String type;
	private double money;
	private int commentId;

	public Order(String id, int carport_id, int user_id, String time,
			String type, double money) {
		super();
		this.id = id;
		this.carport_id = carport_id;
		this.user_id = user_id;
		this.time = time;
		this.type = type;
		this.money = money;
	}



	public int getCommentId() {
		return commentId;
	}



	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public double getMoney() {
		return money;
	}



	public void setMoney(double money) {
		this.money = money;
	}



	public Order() {
		super();
	}

	

	public String getId() {
		return id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public int getCarport_id() {
		return carport_id;
	}

	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
