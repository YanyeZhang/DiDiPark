package com.didipark.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.alibaba.fastjson.JSONObject;
import com.didipark.dao.CidDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.User;
import com.didipark.utils.MyConstant;
import com.opensymphony.xwork2.ActionSupport;

public class RegisterAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String nickname;
	private String password;
	private String imageUrl;
	private String clientid;
	private UserDao userDao;
	private CidDao cidDao;
	private String phone;

	public void setCidDao(CidDao cidDao) {
		this.cidDao = cidDao;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void resgister() {

		JSONObject json = new JSONObject();
		try {
			nickname = URLDecoder.decode(nickname, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (userDao.checkPhone(phone) == true) {
			boolean flag = false;
			flag = userDao.register(nickname, password, phone, imageUrl);
			if (flag == true) {
				json.put("message", "success");
				List<User> user = userDao.login(phone, password);
				cidDao.saveCid(clientid, user.get(0).getId());
				json.put("user", user.get(0));
			} else
				json.put("message", "register failed");
		} else {
			json.put("message", "phone non-uniqueness");
		}
		try {
			byte[] jsonBytes = json.toString().getBytes("utf-8");
			System.out.println(json.toString());
			response.setContentLength(jsonBytes.length);
			response.getOutputStream().write(jsonBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}