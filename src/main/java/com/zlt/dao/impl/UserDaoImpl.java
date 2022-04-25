package com.zlt.dao.impl;

import com.zlt.dao.UserDao;
import com.zlt.entity.Pager;
import com.zlt.entity.User;
import com.zlt.utils.SqlUtil;

import java.util.List;
import java.util.Map;

public class UserDaoImpl implements UserDao {
    @Override
    public int insertUser(User user) {
        try {
            return SqlUtil.insert(user);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public User selectUserByUsername(String username) {
        return SqlUtil.selectOne(User.class,"select * from user where username=?",username);
    }

    @Override
    public List<User> selectAll() {
        return SqlUtil.select(User.class,"select * from user");
    }

    @Override
    public int updateLoginTime(User u) {
        return SqlUtil.update(u);
    }

    @Override
    public boolean deleteByName(String username) {
        String sql = "delete from user where username = ? ";
        int len = SqlUtil.update(sql,username);
        System.out.println(len);
        if (len>0)
            return true;
        return false;
    }

    @Override
    public User etUserByName(String username) {
        String sql = "select * from user where username = ? ";
        User user = SqlUtil.selectOne(User.class,sql,username);
        if (user != null)
            return user;
        return null;
    }

    @Override
    public boolean update(User user) {
        int len = SqlUtil.update(user);
        if (len>0)
            return true;
        return false;
    }

    @Override
    public Long selectCount() {
        String sql = "select count(*) c from user"; //返回数据数目的String类型，并别名命名为c
        List<Map<String, Object>> select = SqlUtil.select(sql);
        return (Long) select.get(0).get("c");
    }

    @Override
    public List<User> selectUser(Pager<User> pager) {
        String sql = "select * from user order by regTime limit ?,?";
        //limit分页公式：（页数-1）*每页大小 ， 每页大小
        return SqlUtil.select(User.class,sql,(pager.getPageNow() - 1) * pager.getPageSize(),pager.getPageSize());
    }
}
