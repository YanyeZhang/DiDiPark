package com.didipark.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.alibaba.fastjson.JSONObject;
import com.didipark.dao.CidDao;
import com.didipark.dao.CommentDao;
import com.didipark.dao.OrderDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Comment;
import com.didipark.pojo.User;
import com.didipark.utils.MyConstant;
import com.opensymphony.xwork2.ActionSupport;

public class CreateCommentAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String orderId, content;
	private int user_id, level, carport_id;
	private OrderDao orderDao;
	private String time;
	private CommentDao commentDao;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void createComment() {

		JSONObject json = new JSONObject();
		Comment comment = new Comment(content, user_id, carport_id, level,
				df.format(new Date()));
		commentDao.saveComment(comment);
		orderDao.updateById(orderId, commentDao.saveComment(comment));
		json.put("message", "success");

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