package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User getUserById(int id);

    User findByUsername(String username);

    int findIdByUsername(String username);

    int findAccountIdByUserId(int id);

    boolean create(String username, String password);

    String findUsernameByAccountId(int id);
}
