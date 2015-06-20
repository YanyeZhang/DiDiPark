package com.didipark.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CommentDao;
import com.didipark.dao.FavoriteDao;
import com.didipark.dao.OrderDao;
import com.didipark.dao.PhotoDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Comment;
import com.didipark.pojo.Favorite;
import com.didipark.pojo.Order;
import com.didipark.pojo.Photo;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class GetRecordAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private CarportDao carportDao;
	private OrderDao orderDao;
	private int userId;
	private PhotoDao photoDao;
	private CommentDao commentDao;

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
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

	public void getRecord() {
		JSONObject json = new JSONObject();
		List<Carport> carports = new ArrayList<Carport>();
		List<Photo> photos = new ArrayList<Photo>();
		List<Order> orders = orderDao.findByUserId(userId);
		List<Comment> comments = new ArrayList<Comment>();
		for (Order temp : orders) {
			Comment commentTemp = commentDao
					.finByCommentId(temp.getCommentId());
			comments.add(commentTemp);
			Carport carportTemp = carportDao.findCarportByID(temp
					.getCarport_id());
			Photo photoTemp = photoDao.findByCarportId(carportTemp.getId());
			carports.add(carportTemp);
			photos.add(photoTemp);
		}
		json.put("comments", comments);
		json.put("carports", carports);
		json.put("orders", orders);
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