package com.efeiyi.website.controller;

import com.efeiyi.website.entity.Entity;
import com.efeiyi.website.util.ApplicationException;
import com.efeiyi.website.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.net.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.List;

public class BaseController {

    public BaseController() {

    }


    protected void HandleException(Exception e) {

    }

    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    public <T extends Entity> void responseContent(HttpServletRequest request, HttpServletResponse response, T entity) {
        JSONObject result = new JSONObject();
        result.element("sum", 1);
        result.element("data", entity.toJson());
        try {
            response.getWriter().write(checkJsonp(request, response, result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    public void responseEmpty(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.element("sum", 0);
        try {
            response.getWriter().write(checkJsonp(request, response, result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());

        }
    }

    public void responseContent(HttpServletRequest request, HttpServletResponse response, String content) {
        JSONObject result = new JSONObject();
        result.element("sum", 1);
        result.element("data", content);
        try {
            response.getWriter().write(checkJsonp(request, response, result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    public void responseJSONArray(HttpServletRequest request, HttpServletResponse response, JSONArray content, long sum) {
        JSONObject result = new JSONObject();
        result.element("sum", sum);
        result.element("data", content);
        try {
            response.getWriter().write(checkJsonp(request, response, result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    public void responseJSONObject(HttpServletRequest request, HttpServletResponse response, JSONObject content) {
        JSONObject result = new JSONObject();
        result.element("sum", 1);
        result.element("data", content);
        try {
            response.getWriter().write(checkJsonp(request, response, result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    public void responseException(HttpServletRequest request, HttpServletResponse response, ApplicationException ex) {
        try {
            response.getWriter().write(checkJsonp(request, response, ex.toJsonString()));
        } catch (IOException e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    public void responseException(HttpServletRequest request, HttpServletResponse response, int code) {
        ApplicationException ex = new ApplicationException(code);
        try {
            response.getWriter().write(checkJsonp(request, response, ex.toJsonString()));
        } catch (IOException e) {
            e.printStackTrace();
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    public void responseSuccess(HttpServletRequest request, HttpServletResponse response) {
       responseSuccess(request, response, null);
    }

    public void responseSuccess(HttpServletRequest request, HttpServletResponse response,  Entity entity ) {
        JSONObject result = new JSONObject();
        result.put("code", "0");

        if(entity != null) {
            entity.toJson().toString();
            result.put("data", entity.toJson().toString());
        }
        try {
            response.getWriter().write(checkJsonp(request, response, result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }



    public void responseJSONArray(HttpServletRequest request, HttpServletResponse response, JSONArray jsonArray) {
        JSONObject result = new JSONObject();
        result.element("sum", jsonArray.size());
        result.element("data", jsonArray);
        try {
            response.getWriter().write(checkJsonp(request, response, result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> void responseContent(HttpServletRequest request, HttpServletResponse response, List<T> entities) {
        JSONObject result = new JSONObject();
        result.element("sum", entities.size());
        result.element("data", Entity.toJson((List<Entity>) entities));
        try {
            response.getWriter().write(checkJsonp(request, response,result.toString()));
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
    }

    protected <T extends Entity> T parse(String jsonObjString) throws Exception {
        Class<T> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        JSONObject jobObj = JSONObject.fromObject(jsonObjString);
        ObjectMapper mapper = new ObjectMapper();
        T entity = mapper.readValue(jobObj.toString(), type);
        return entity;
    }

    private String checkJsonp(HttpServletRequest request, HttpServletResponse response, String data) {
        String callback = request.getParameter("callback");
        if (callback != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(callback);
            sb.append("(");
            sb.append(data);
            sb.append(")");
            response.setContentType("application/x-javascript");
            return sb.toString();
        }
        return data;
    }

    public JSONObject receiveJson(HttpServletRequest request) throws Exception {

        try (InputStream inputStream = request.getInputStream()) {
            request.setCharacterEncoding("utf-8");
            byte[] bytes = new byte[request.getContentLength()];
            inputStream.read(bytes);
            String param = new String(bytes, "UTF-8");
            JSONObject jsonObj = JSONObject.fromObject(param);
            inputStream.close();
            return jsonObj;
        } catch (Exception e) {
            throw new Exception();
        }
    }


}
