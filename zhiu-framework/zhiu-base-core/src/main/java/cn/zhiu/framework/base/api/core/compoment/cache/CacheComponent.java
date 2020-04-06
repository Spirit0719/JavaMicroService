package cn.zhiu.framework.base.api.core.compoment.cache;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * The interface Cache component.
 *
 * @author zhuzz
 * @time 2019 /05/29 16:47:06
 */
public interface CacheComponent extends Serializable {

    /**
     * Get t.
     *
     * @param <T> the type parameter
     * @param key the key
     * @param t   the t
     *
     * @return the t
     *
     * @author zhuzz
     * @time 2019 /05/29 16:47:06
     */
    <T> T get(String key, Class<T> t);

    /**
     * Gets or add.
     *
     * @param <T>      the type parameter
     * @param key      the key
     * @param supplier the supplier
     * @param t        the t
     *
     * @return the or add
     *
     * @author zhuzz
     * @time 2019 /05/29 16:47:06
     */
    <T> T getOrAdd(String key, Supplier<T> supplier, Class<T> t);

    /**
     * Gets or add.
     *
     * @param <T>        the type parameter
     * @param key        the key
     * @param expireTime the expire time
     * @param supplier   the supplier
     * @param t          the t
     *
     * @return the or add
     *
     * @author zhuzz
     * @time 2019 /05/29 16:47:06
     */
    <T> T getOrAdd(String key, int expireTime, Supplier<T> supplier, Class<T> t);

    /**
     * Remove boolean.
     *
     * @param keys the key
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /05/29 16:47:06
     */
    Long remove(String... keys);

}
