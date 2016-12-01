package com.efeiyi.website.cache.redis;

import com.efeiyi.website.entity.User;
import com.efeiyi.website.util.Util;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class Client {


    /*public static void main(String[] args) {
        try {
            *//*Redis redis = RedisFactory.getInstance().getRedis();
            System.out.println(redis.get("foo"));*//*
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main1(String[] args) throws Exception {

        //测试redis接口 hSet
        //1.创建测试数据（User）创建一个方法可以随机生成一条user数据
        //2.创建一个Redis实例
        //3.创建一个线程池，最大线程数十100;
        //4.测试线程1000

        ExecutorService service = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Redis redis = null;
                    try {
                        redis = RedisFactory.getInstance().getRedis();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    User user = Client.generateUserObject();
                    try {
                        test(redis, user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "Thread-" + i);
            service.execute(thread);
        }
        service.shutdown();
    }

    public static void main2(String[] args) throws Exception {
        User user = generateUserObject();
        HashMap<String, String> hashMap = parseMap(user);
        printMap(hashMap);
    }

    public static void test(Redis redis, User user) throws Exception {
        long start = System.currentTimeMillis();
        HashMap<String, String> hashMap = parseMap(user);
        redis.setHash(user.getId(), hashMap);
        Map<String, String> reslut = redis.getHash(user.getId());
        redis.close();
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + ":" + user.getId() + ":" + (end - start) + ":" + reslut.get("userName"));
    }

    private static User generateUserObject() {
        User user = new User();
//        user.setId((long) (Math.random() * 10000000) + "");
//        user.setLastLoginDatetime(new Date());
//        user.setLastLoginIp("192.168.1.43");
//        user.setCreateDatetime(new Date());
//        double flag = (Math.random() * 100);
//        user.setStatus(flag > 50 ? "1" : "0");
//        user.setBirthday(new Date());
//        user.setEmail("wangtao@efeiyi.com");
//        user.setGender("1");
//        user.setName("wangtao");
//        user.setUserName("13693097151");
//        user.setPassword("1234567");
        return user;
    }

    private static HashMap<String, String> parseMap(User user) throws Exception {
        Field[] fields = user.getClass().getDeclaredFields();
        HashMap<String, String> hashMap = new HashMap<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            String setMethodName = "get" + Util.firstUpperCase(fieldName);
            Method fieldMethod;
            try {
                fieldMethod = user.getClass().getMethod(setMethodName);
            } catch (NoSuchMethodException e) {
                throw e;
            }
            Object object = null;
            try {
                object = fieldMethod.invoke(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (object != null) {
                hashMap.put(fieldName, object.toString());
            }
        }
        return hashMap;
    }

    public static void printMap(Map<String, String> map) {
        System.out.print("[");
        for (String key : map.keySet()) {
            System.out.print(key + ":" + map.get(key));
            System.out.print(" ; ");
        }
        System.out.println("]");
    }
*/
}
