package com.zhzj.service.impl;

import com.zhzj.dao.DaoSupport;
import com.zhzj.service.UserService;
import com.zhzj.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;



/**
 * 用户接口实现类
 *
 * @author 张京辉
 *         创建时间:2015/9/9
 *         更新时间:
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    /**
     * 登录判断
     *
     * @param pd
     * @return
     * @throws Exception
     */
    public List<PageData> getUserByNameAndPwd(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("UserMapper.getUserInfo", pd);
    }

    @Override
    public PageData findByUsername(PageData pd) throws Exception {
        return (PageData) dao.findForObject("UserMapper.findByUsername", pd);
    }

    @Override
    public void addUser(PageData pd) throws Exception {
        dao.save("UserMapper.addUser", pd);
    }
}
