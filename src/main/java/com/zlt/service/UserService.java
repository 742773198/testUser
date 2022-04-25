package com.zlt.service;

import com.zlt.entity.Pager;
import com.zlt.entity.User;

import java.util.List;

public interface UserService {

    boolean reg(User user);

    User login(String username, String password);

    List<User> selectAll();

    boolean deleteByName(String username);

    User getUserByName(String username);

    boolean update(User user);

    Pager<User> selectUser(Pager<User> pager);
}
