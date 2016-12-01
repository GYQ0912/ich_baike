package com.efeiyi.website.service.inter;

import com.efeiyi.website.entity.User;

/**
 * Created by Administrator on 2016/8/12 0012.
 */
public interface IUserService {

    User login(String username, String password, String ip, String sessionId) throws Exception;

    void logout(String sessionId) throws Exception;

    /**
     * 注册
     */
    User signIn(User user) throws Exception;

    /**
     * 判断当前用户是否有访问 此uri的权限
     *
     * @param uri
     * @param user
     * @return
     * @throws Exception
     */
    boolean hasAuthority(String uri, User user) throws Exception;

    /**
     * 获得当前访问的用户信息
     *
     * @param sessionId
     * @return
     * @throws Exception
     */
    User currentUser(String sessionId);

    /**
     * 通过id获得用户数据
     */
    User getUser(String id) throws Exception;

    User updateUser(User user) throws Exception;

    void deleteUser(String id) throws Exception;

}
