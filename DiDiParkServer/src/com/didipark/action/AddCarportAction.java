package com.didipark.action;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.alibaba.fastjson.JSONObject;
import com.didipark.dao.CarportDao;
import com.didipark.dao.PhotoDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Photo;
import com.didipark.utils.FileUtil;
import com.didipark.utils.ImageUtil;
import com.didipark.utils.MyConstant;
import com.opensymphony.xwork2.ActionSupport;

public class AddCarportAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String data;
	private String type;
	private PhotoDao photoDao;
	private CarportDao carportDao;
	private int id;
	private String describe, addr;
	private int perHoursMoney, num;
	private double longitude, latitude;

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public void setPerHoursMoney(int perHoursMoney) {
		this.perHoursMoney = perHoursMoney;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addCarport() {
		String imgeName = null;
		JSONObject json = new JSONObject();
		Carport carport = carportDao.saveCarport(describe, addr, perHoursMoney,
				num, latitude, longitude, id);
		if (carport != null) {
			try {

				imgeName = FileUtil.saveFile(type, data, MyConstant.DOMAIN_IMG);
				String url = MyConstant.IMAGE_URL + imgeName;
				//String url2 = MyConstant.IMAGE2_URL + imgeName;

				
				 // imgeName = FileUtil.saveFile(type, data, MyConstant.carport);
				  //String url = MyConstant.DOMAIN + imgeName; 
				 
				Photo photo = photoDao.savePhoto(url, carport, url);
				json.put("message", "success");
				json.put("carport", carport);
				json.put("photoUrl", photo.getPhotoUrl());
				json.put("photoUrl2", photo.getPhotoUrl2());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			json.put("name", "failed");
		}
		try {
			System.out.println(json.toString());
			byte[] jsonBytes = json.toString().getBytes("utf-8");
			response.setContentLength(jsonBytes.length);
			response.getOutputStream().write(jsonBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}