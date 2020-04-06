package cn.zhiu.framework.base.api.core.compoment.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisCacheComponent<T> implements CacheComponent {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${redis.cache.expiretime:1500}")
    private int defaultRedisCacheExpireTime;

    @Override
    public <T> T get(String key, Class<T> t) {
        String val = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(val)) {
            return JSON.parseObject(val, t);
        }
        return null;
    }

    @Override
    public <T> T getOrAdd(String key, Supplier<T> supplier, Class<T> t) {
        return refreshCache(key, supplier, t, defaultRedisCacheExpireTime);
    }


    @Override
    public <T> T getOrAdd(String key, int expireTime, Supplier<T> supplier, Class<T> t) {
        return refreshCache(key, supplier, t, expireTime);
    }

    @Override
    public Long remove(String... keys) {
        return redisTemplate.delete(Lists.newArrayList(keys));
    }


    private <T> T refreshCache(String key, Supplier<T> supplier, Class<T> t, int defaultRedisCacheExpireTime) {
        String val = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(val)) {
            return JSON.parseObject(val, t);
        }

        if (Objects.nonNull(supplier)) {
            T t1 = supplier.get();
            redisTemplate.opsForValue().set(key, JSON.toJSONString(t1), defaultRedisCacheExpireTime, TimeUnit.SECONDS);
            return t1;
        }
        return null;
    }
}
