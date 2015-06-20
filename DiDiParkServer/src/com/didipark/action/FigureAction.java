package com.didipark.action;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.alibaba.fastjson.JSONObject;
import com.didipark.dao.UserDao;
import com.didipark.utils.FileUtil;
import com.didipark.utils.MyConstant;
import com.opensymphony.xwork2.ActionSupport;


public class FigureAction extends ActionSupport implements ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String data;
	private String type;
	private UserDao userDao;
	private int id;

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
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

	public void figure() throws InterruptedException {
		String imgeName = null;
		JSONObject json = new JSONObject();
		try {
			imgeName = FileUtil.saveFile(type, data, MyConstant.DOMAIN_FIGURE);
			//imgeName = FileUtil.saveFile(type, data, MyConstant.carport2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (imgeName != null) {
			String url = MyConstant.FIGURE_URL + imgeName;
			//String url = MyConstant.DOMAIN2 + imgeName;
			userDao.updateFigure(url, id);
			json.put("message", "success");
			json.put("figure", url);
		} else {
			json.put("message", "failed");
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