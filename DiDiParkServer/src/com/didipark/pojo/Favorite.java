package com.didipark.pojo;

public class Favorite {
	private int id;
	private int userId;
	private int carportId;

	public Favorite() {
	}

	public Favorite( int userId, int carportId) {
		super();
		this.userId = userId;
		this.carportId = carportId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCarportId() {
		return carportId;
	}

	public void setCarportId(int carportId) {
		this.carportId = carportId;
	}
}
