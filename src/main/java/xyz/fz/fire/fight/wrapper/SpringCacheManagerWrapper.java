package xyz.fz.fire.fight.wrapper;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

/**
 * 包装Spring cache抽象
 * <p>User: Zhang Kaitao</p>
 * <p>Date: 13-3-23 上午8:26</p>
 * <p>Version: 1.0</p>
 */
@Component
public class SpringCacheManagerWrapper implements CacheManager {

    /**
     * 缓存清除：
     * 方案A：暂且直接使用springCache.clear()清除全部缓存，在有帐号变更及权限变更时使用
     * 优点：简单直接，适合用在权限及帐号变动不频繁的系统
     * 缺点：无法清除指定帐号，如一天内多次产生多次密码变更或者权限调整将不是一个很好的方案
     * 方案B：指定清除
     * 优点：指定帐号进行清除，先对用户信息进行修改（一般为密码或权限），然后模拟登录再退出
     * 当发生权限调整时需要将所有受到影响的用户遍历做出以上操作
     * 缺点：相对来说实现较为麻烦，并且如果功能实现上有遗漏清除缓存的地方将有可能发生变更了却不生效的情况
     */

    private final org.springframework.cache.CacheManager cacheManager;

    @Autowired
    public SpringCacheManagerWrapper(@Qualifier("shiroRedisCacheManager") org.springframework.cache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        org.springframework.cache.Cache springCache = cacheManager.getCache(name);
        return new SpringCacheWrapper(springCache);
    }

    static class SpringCacheWrapper implements Cache {
        private org.springframework.cache.Cache springCache;

        SpringCacheWrapper(org.springframework.cache.Cache springCache) {
            this.springCache = springCache;
        }

        @Override
        public Object get(Object key) throws CacheException {
            Object value = springCache.get(key);
            if (value instanceof SimpleValueWrapper) {
                return ((SimpleValueWrapper) value).get();
            }
            return value;
        }

        @Override
        public Object put(Object key, Object value) throws CacheException {
            springCache.put(key, value);
            return value;
        }

        @Override
        public Object remove(Object key) throws CacheException {
            springCache.evict(key);
            return null;
        }

        @Override
        public void clear() throws CacheException {
            springCache.clear();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("invoke spring cache abstract size method not supported");
        }

        @Override
        public Set keys() {
            throw new UnsupportedOperationException("invoke spring cache abstract keys method not supported");
        }

        @Override
        public Collection values() {
            throw new UnsupportedOperationException("invoke spring cache abstract values method not supported");
        }
    }
}
