package com.didipark.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.CommentDao;
import com.didipark.dao.OrderDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Cid;
import com.didipark.pojo.Comment;
import com.didipark.pojo.Order;
import com.didipark.pojo.User;
import com.didipark.utils.MyConstant;

public class CommentImpl implements CommentDao {
	private HibernateTemplate ht = null;
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private HibernateTemplate getHibernateTemplate() {
		if (ht == null) {
			ht = new HibernateTemplate(sessionFactory);
		}
		return ht;
	}

	public int saveComment(Comment comment) {
		try {
			getHibernateTemplate().save(comment);
			getHibernateTemplate().flush();
		} catch (Exception e) {
		}
		return comment.getId();
	}

	public Comment finByCommentId(int commentId) {
		List<Comment> comment = null;
		Comment commentDef = new Comment();
		try {
			comment = getHibernateTemplate().find(
					"from Comment c where c.id=?", commentId);
		} catch (Exception e) {

		}
		if (comment.size() > 0)
			return comment.get(0);
		else
			return commentDef;
	}

	public List<Comment> finByCarportId(int carportId) {
		List<Comment> comment = null;
		Comment commentDef = new Comment();
		try {
			comment = getHibernateTemplate().find(
					"from Comment c where c.carport_id=?", carportId);
		} catch (Exception e) {
		}
		return comment;

	}

	public List<Comment> finByUserId(int userId) {
		List<Comment> comment = null;
		Comment commentDef = new Comment();
		try {
			comment = getHibernateTemplate().find(
					"from Comment c where c.user_id=?", userId);
		} catch (Exception e) {
		}
		return comment;
	}

}
