package com.didipark.action;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletResponseAware;
import com.didipark.dao.UserDao;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateInfoAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private UserDao userDao;
	private int id;
	private String password;
	private String nickName;
	private String phone;

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void updateUser() {
		JSONObject json = new JSONObject();
		User user = userDao.findUserById(id);
		if (password != null)
			user.setPassword(password);
		else if (nickName != null)
			user.setNickName(nickName);
		else if (phone != null)
			user.setPhone(phone);
		userDao.updateUser(user);
		json.put("user", user);
		try {
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