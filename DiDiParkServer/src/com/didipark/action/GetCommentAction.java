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

public class GetCommentAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private int carportId;
	private CommentDao commentDao;
	private UserDao userDao;

	public void setCarportId(int carportId) {
		this.carportId = carportId;
	}


	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setCommentDao(CommentDao commentDao) {

		this.commentDao = commentDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void getComment() {
		System.out.println(carportId);
		JSONObject json = new JSONObject();
		List<User> users=new ArrayList<User>();
		List<Comment> comments = commentDao.finByCarportId(carportId);
		System.out.println(comments.size());
		for (Comment temp : comments) {
			System.out.println(temp.getUser_id());
            User user=userDao.findUserById(temp.getUser_id());
           if(user!=null)
             users.add(user);
		}
		json.put("comments", comments);
		json.put("users", users);
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