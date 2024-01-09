package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

//    public UserController(){
//        UserDao userDao1 = new JdbcUserDao(new JdbcTemplate());
//        userDao = userDao1;
//    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public  List <User>  getAllUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable int id) {
        return userDao.getUserById(id);
    }

    @RequestMapping(path = "/users/{id}/account", method = RequestMethod.GET)
    public int getUserAccountId(@PathVariable int id) {
        return userDao.findAccountIdByUserId(id);
    }
}
