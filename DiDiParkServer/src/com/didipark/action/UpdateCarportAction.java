package com.didipark.action;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.alibaba.fastjson.JSONObject;
import com.didipark.dao.CarportDao;
import com.didipark.dao.CommentDao;
import com.didipark.dao.FavoriteDao;
import com.didipark.dao.OrderDao;
import com.didipark.dao.PhotoDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Photo;
import com.didipark.utils.FileUtil;
import com.didipark.utils.ImageUtil;
import com.didipark.utils.MyConstant;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateCarportAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String data;
	private String type;
	private PhotoDao photoDao;
	private CarportDao carportDao;
	private String state;
	private int id;
	private String describe, addr;
	private int perHoursMoney, num;

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

	public void setState(String state) {
		this.state = state;
	}

	public void updateCarport() {
		String imgeName = null;
		JSONObject json = new JSONObject();
		System.out.println(id+"zyy");
		if (state.equals("下架")) {
			Carport carport = carportDao.findCarportByID(id);
			carport.setState(state);
			carportDao.updateCarport(carport);
			json.put("message", "delet_success");
		} else {
			Carport carport = carportDao.findCarportByID(id);
			System.out.println("??");
			if (!describe.equals(""))
				carport.setDescribe(describe);
			if (!addr.equals(""))
				carport.setAddr(addr);
			if (!state.equals(""))
				carport.setState(state);
			
		    carport.setPerHoursMoney(perHoursMoney);
			carport.setNum(num);
			carport.setState(state);
			carportDao.updateCarport(carport);
			json.put("message", "update_success");
			json.put("carport", carport);
			if (type != null) {
				try {
					System.out.println("hah");
					photoDao.deletPhotoByCarport_id(carport.getId());
					System.out.println("hah2");
					imgeName = FileUtil
							.saveFile(type, data, MyConstant.DOMAIN_IMG);
					String url = MyConstant.IMAGE_URL + imgeName;
				/*	imgeName = FileUtil
							.saveFile(type, data, MyConstant.carport);
					String url = MyConstant.DOMAIN + imgeName;*/
					Photo photo = photoDao.savePhoto(url, carport, url);
					json.put("photoUrl", photo.getPhotoUrl());
					json.put("photoUrl2", photo.getPhotoUrl2());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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