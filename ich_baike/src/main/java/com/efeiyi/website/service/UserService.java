package com.efeiyi.website.service;

import com.efeiyi.website.dao.FunctionDao;
import com.efeiyi.website.dao.UserDao;
import com.efeiyi.website.entity.Function;
import com.efeiyi.website.entity.User;
import com.efeiyi.website.service.inter.IUserService;
import com.efeiyi.website.util.ApplicationException;
import com.efeiyi.website.util.Util;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends BaseService<User> implements IUserService {

    public static final String COMMON_USER_ROLE_TYPE = "1";
    public static final String ARTIST_USER_ROLE_TYPE = "2";
    public static final String MANAGER_USER_ROLE_TYPE = "3";

    /**
     * 登录
     */
    public User login(String userName, String password, String ip, String sessionId) throws ApplicationException {

        UserDao dao = new UserDao();
        User entity = new User();
        try {
            dao.login(userName,  Util.encodePassword(password, "SHA"), ip, entity);
        } catch(Exception e) {
            throw new ApplicationException(ApplicationException.INNER_ERROR);
        }

        if(entity.identity() == null) {
            throw new ApplicationException(ApplicationException.INNER_ERROR);
        }
        Session session = new Session(sessionId);
        try {
            session.setAttribute("user", entity);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationException.INNER_ERROR);
        }
        try {
            entity = (User) session.getAttribute("user");
        } catch (Exception e){}
        return entity;
    }

    public void logout(String sessionId) throws Exception {
        Session session = new Session(sessionId);
        session.invalidate();
    }

    /**
     * 注册
     */
    public User signIn(User user) throws ApplicationException {
        UserDao userDao = new UserDao();
        try {
            userDao.add(user);
        } catch(Exception e) {
            throw new ApplicationException(ApplicationException.INNER_ERROR);
        }
            return user;
    }

    public boolean hasAuthority(String uri, User user) throws Exception {
        FunctionDao functionDao = new FunctionDao();
        Function function = functionDao.getByCode(uri, false);  //获取当前请求对应的function

        if (function == null || (function.identity() != null && function.getType().equals("0"))) {   //验证function
            return true;
        }

        if (user == null) {
            return false;
        }

        /*List<Function> functionList = user.getFunctionList();
        for (Function functionTemp : functionList) {
            if (functionTemp.getCode().toLowerCase().equals(uri.toLowerCase())) {
                return true;
            }
        }*/

        return false;
    }


    public User currentUser(String sessionId) {
        Session session = new Session(sessionId);
        User user = null;
        try {
            user = (User) session.getAttribute("user");
        } catch (Exception e) {
            return null;
        }
        return user;
    }


    /**
     * 通过id获得用户数据
     */
    public User getUser(String id) throws Exception {
        UserDao dao = new UserDao();
        User entity = new User();
        dao.get(id, entity);
        return entity;
    }


    public User updateUser(User user) throws Exception {
        UserDao userDao = new UserDao();
        userDao.update(user);
        return user;
    }

    public void deleteUser(String id) throws Exception {
        UserDao userDao = new UserDao();
        userDao.delete(id);
    }

}
