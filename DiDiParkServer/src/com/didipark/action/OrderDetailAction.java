package com.didipark.action;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.PhotoDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Photo;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class OrderDetailAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private UserDao userDao;
	private int customer_id;
	private int carport_id;
	private CarportDao carportDao;
	private PhotoDao photoDao;

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}

	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}

	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void orderDetail() {
		JSONObject json = new JSONObject();
		System.out.println(customer_id + "  " + carport_id);
		if (customer_id != 0) {
			User user = userDao.findUserById(customer_id);
			json.put("user", user);
		}
		if (carport_id != 0) {
			Carport carport = carportDao.findCarportByID(carport_id);
			Photo photo = photoDao.findByCarportId(carport_id);
			json.put("photo", photo.getPhotoUrl2());
			json.put("carport", carport);
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