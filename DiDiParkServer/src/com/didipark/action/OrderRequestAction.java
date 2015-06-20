package com.didipark.action;

import java.text.SimpleDateFormat;
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
import com.didipark.push.PushOrder;
import com.opensymphony.xwork2.ActionSupport;

public class OrderRequestAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	// userId为车位主人，id为申请停车
	private int userId, id, carportId;
	private CidDao cidDao;
	private UserDao userDao;

	public void setCarportId(int carportId) {
		this.carportId = carportId;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setCidDao(CidDao cidDao) {
		this.cidDao = cidDao;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void orderRequest() throws Exception {
		JSONObject json = new JSONObject();
		User user = userDao.findUserById(id);
		String phone = user.getPhone().substring(user.getPhone().toString().length()-4,
				user.getPhone().toString().length());
		System.out.println(phone);
		PushOrder.pushOrderToCarportUser(cidDao.findCidById(userId),
				phone, id, carportId);
		json.put("message", "success");
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