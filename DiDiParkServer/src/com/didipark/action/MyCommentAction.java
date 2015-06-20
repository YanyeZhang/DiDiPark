package com.didipark.action;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.CommentDao;
import com.didipark.dao.PhotoDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Comment;
import com.didipark.pojo.Photo;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class MyCommentAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private int userId;
	private CommentDao commentDao;
	private PhotoDao photoDao;
	private CarportDao carportDao;




	public void setUserId(int userId) {
		this.userId = userId;
	}



	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}



	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}



	public void setCommentDao(CommentDao commentDao) {

		this.commentDao = commentDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void getMyComment() {
		JSONObject json = new JSONObject();
		List<Carport> carports = new ArrayList<Carport>();
		List<Photo> photos = new ArrayList<Photo>();
		List<Comment> comments = commentDao.finByUserId(userId);
		for (Comment temp : comments) {
			Carport carport = carportDao.findCarportByID(temp.getCarport_id());
			Photo photo = photoDao.findByCarportId(carport.getId());
			photos.add(photo);
			carports.add(carport);
		}
		json.put("comments", comments);
		json.put("carports", carports);
		json.put("photos", photos);
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