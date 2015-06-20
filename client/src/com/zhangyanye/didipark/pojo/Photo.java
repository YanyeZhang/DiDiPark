package com.zhangyanye.didipark.pojo;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Id;

public class Photo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private int db_id;
	private int carport_id;
	private String photoUrl;
	private String photoUrl2;

	public Photo() {
	}

	public Photo(int carport_id, String photoUrl, String photoUrl2) {
		super();
		this.carport_id = carport_id;
		this.photoUrl = photoUrl;
		this.photoUrl2 = photoUrl2;
	}

	public int getDb_id() {
		return db_id;
	}

	public void setDb_id(int db_id) {
		this.db_id = db_id;
	}

	public String getPhotoUrl2() {
		return photoUrl2;
	}

	public void setPhotoUrl2(String photoUrl2) {
		this.photoUrl2 = photoUrl2;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public int getCarport_id() {
		return carport_id;
	}

	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}

}
