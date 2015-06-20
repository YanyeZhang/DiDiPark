package com.didipark.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.FavoriteDao;
import com.didipark.dao.OrderDao;
import com.didipark.dao.PhotoDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Favorite;
import com.didipark.pojo.Order;
import com.didipark.pojo.Photo;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrderAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private CarportDao carportDao;
	private OrderDao orderDao;
	private UserDao userDao;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	private int userId;
	private PhotoDao photoDao;

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void getOrder() {
		JSONObject json = new JSONObject();
		List<Carport> carports = new ArrayList<Carport>();
		List<Order> orders = new ArrayList<Order>();
		List<String> carportAddrs = new ArrayList<String>();
		List<String> photos = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		carports = carportDao.findByUserIdAll(userId);
		for (Carport temp : carports) {
			String addrs=temp.getAddr();
			List<Order> orderTemp = orderDao.findByCarportId(temp.getId());
			if (orderTemp != null)
				for (Order temp2 : orderTemp) {
					orders.add(temp2);
					carportAddrs.add(addrs);
					User user=userDao.findUserById(temp2.getUser_id());
					names.add(user.getNickName());
					photos.add(user.getImageUrl());
				}
		}
		json.put("names", names);
		json.put("addrs", carportAddrs);
		json.put("photos", photos);
		json.put("orders", orders);
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