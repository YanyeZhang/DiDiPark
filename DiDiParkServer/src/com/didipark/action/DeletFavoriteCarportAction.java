package com.didipark.action;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CidDao;
import com.didipark.dao.FavoriteDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class DeletFavoriteCarportAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private int user_id, carport_id;
	private FavoriteDao favoriteDao;

	public void setFavoriteDao(FavoriteDao favoriteDao) {
		this.favoriteDao = favoriteDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}

	public void deletFavoriteCarport() {
		JSONObject json = new JSONObject();
		favoriteDao.deletFavorite(user_id, carport_id);
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