package com.zlt.dao;

import com.zlt.entity.Pager;
import com.zlt.entity.User;

import java.util.List;

public interface UserDao {
    int insertUser(User user);

    User selectUserByUsername(String username);

    List<User> selectAll();

    int updateLoginTime(User u);

    boolean deleteByName(String username);

    User etUserByName(String username);

    boolean update(User user);

    Long selectCount();

    List<User> selectUser(Pager<User> pager);
}
