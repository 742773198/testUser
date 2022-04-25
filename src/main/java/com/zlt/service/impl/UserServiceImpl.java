package com.zlt.service.impl;

import com.zlt.dao.UserDao;
import com.zlt.dao.impl.UserDaoImpl;
import com.zlt.entity.Pager;
import com.zlt.entity.User;
import com.zlt.service.UserService;
import com.zlt.utils.MD5Util;
import com.zlt.utils.StringUtil;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserDao userDao = new UserDaoImpl();



    @Override
    public boolean reg(User user) {
        user.setSalt(StringUtil.randomStr(4));
        user.setPassword(MD5Util.getMD5(user.getPassword(),user.getSalt(),10));
        user.setRegTime(StringUtil.getCurrentTime());

        return userDao.insertUser(user) > 0;
    }

    @Override
    public User login(String username, String password) {
        User user = userDao.selectUserByUsername(username);
        if(user == null){
            System.out.println("未找到该用户名");
            return null;
        }
        String p = MD5Util.getMD5(password,user.getSalt(),10);
        if(!p.equals(user.getPassword())){
            System.out.println(p);
            System.out.println("账号密码错误，加密解密不匹配。");
            return null;
        }
        //修改登录时间
        User u = new User();
        u.setUid(user.getUid());
        u.setLoginTime(StringUtil.getCurrentTime());
        userDao.updateLoginTime(u);
        return user;
    }

    @Override
    public List<User> selectAll() {
        return userDao.selectAll();
    }

    @Override
    public boolean deleteByName(String username) {
        return userDao.deleteByName(username);
    }

    @Override
    public User getUserByName(String username) {
        return userDao.etUserByName(username);
    }

    @Override
    public boolean update(User user) {
        return userDao.update(user);
    }

    @Override
    public Pager<User> selectUser(Pager<User> pager) {
        pager.setTotalCount(userDao.selectCount());  //设置总数据数
        pager.setPageCount((int) ((pager.getTotalCount() - 1) / pager.getPageSize() + 1));  //设置最大页数
        pager.setData(userDao.selectUser(pager));  //将对应页数的几组数据存到Pager对象中。
        return pager;
    }
}
