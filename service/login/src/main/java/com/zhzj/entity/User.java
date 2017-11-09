package com.zhzj.entity;


/**
 * 
* 类名称：用户
* 类描述： 
* @author 张京辉[]
* 作者单位： 
* 联系方式：
* 创建时间：2014年6月28日
* @version 1.0
 */
public class User {
	private String id;        //用户id
	private String username;    //用户名
	private String password;    //密码
	private String create_time;
	private String update_time;

	public String getId() {
		return id;
	}

	public void setId(String uid) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User() {

	}
}
