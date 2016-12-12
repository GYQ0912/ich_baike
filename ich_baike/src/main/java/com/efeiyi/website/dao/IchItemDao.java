package com.efeiyi.website.dao;

import com.efeiyi.website.cache.redis.Redis;
import com.efeiyi.website.cache.redis.RedisFactory;
import com.efeiyi.website.entity.IchItem;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */
public class IchItemDao extends BaseDao<IchItem> {
    @Override
    protected BaseRdDao getRdDao() {
        return new IchItemRdDao();
    }

    @Override
    protected BaseDbDao getDbDao() {
        return new IchItemDbDao();
    }

    private class IchItemRdDao extends BaseRdDao<IchItem> {}

    private class IchItemDbDao extends BaseDbDao<IchItem> {}

}
