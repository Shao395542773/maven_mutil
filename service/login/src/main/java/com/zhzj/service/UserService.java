package com.zhzj.service;



import com.zhzj.util.PageData;

import java.util.List;


/**
 * 用户接口类
 * @author 张京辉
 * 创建时间:2015/9/9
 * 更新时间:
 */
public interface UserService {
	
	/**登录判断
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getUserByNameAndPwd(PageData pd)throws Exception;

	public PageData findByUsername(PageData pd)throws Exception;

	public void addUser(PageData pd)throws Exception;
}
