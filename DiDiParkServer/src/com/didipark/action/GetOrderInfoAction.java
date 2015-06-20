package com.didipark.action;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.FavoriteDao;
import com.didipark.dao.PhotoDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Photo;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrderInfoAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private int userId, carportId;
	private UserDao userDao;
	private PhotoDao photoDao;
	private CarportDao carportDao;

	
	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setCarportId(int carportId) {
		this.carportId = carportId;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	public void getOrderInfo() {
		JSONObject json = new JSONObject();
		User user=userDao.findUserById(userId);
		Carport carport=carportDao.findCarportByID(carportId);
		Photo photo=photoDao.findByCarportId(carportId);
		json.put("photo", photo.getPhotoUrl2());
		json.put("user", user);
		json.put("carport", carport);
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