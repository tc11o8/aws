package com.smalleyes.aws.redis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

@Service
public class RedisClient<K, V> {

    private RedisTemplate<K, V> redisTemplate;
    private final long DEFAULT_EXPIRE_TIME = 1 * 24 * 60 * 60;
    private KryoRedisSerializer<Object> kryoSerializer;
    private StringRedisSerializer stringSerializer;

    /**
     * 放入缓存服务器，默认当天存活
     *
     * @param key   缓存key值
     * @param value 缓存value值(object对象)
     */
    public void put(K key, V value) {
        redisTemplate.boundValueOps(key).set(value);

    }

    /**
     * eval:执行eval操作
     *
     * @param script
     * @param returnType
     * @param value
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Object eval(final String script, final ReturnType returnType, final String[] value) {
        return redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                final byte[][] keysAndArgs = keysAndArgs(stringSerializer, value);
                return connection.eval(script.getBytes(), returnType, value.length, keysAndArgs);
            }
        });
    }

    public Object eval(final String script, final ReturnType returnType, final List<String> keysAndArgs) {
        return eval(script, returnType, keysAndArgs.toArray(new String[0]));
    }

    public void evalAppend(final String key, final String value) {
        String lua = "redis.call('append', KEYS[1], KEYS[2])";
        eval(lua, ReturnType.BOOLEAN, new String[]{key, value});
    }

    /**
     * evalLrange:队列值
     *
     * @param key
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public List<String> evalLrange(final String key) {
        Object obj = this.eval("return redis.call('lrange',KEYS[1],0,-1)", ReturnType.MULTI, new String[]{key});
        if (obj != null) {
            List list = (List) obj;
            List<String> result = new ArrayList<String>();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    result.add(new String((byte[]) list.get(i)));
                }
            }
            return result;
        }
        return null;
    }

    /**
     * evalHget:
     *
     * @param key
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public String evalHget(final String key, final String field) {
        Object obj = this.eval("return redis.call('hget', KEYS[1], KEYS[2])", ReturnType.VALUE, new String[]{key, field});
        if (obj != null) {
            return new String((byte[]) obj);
        } else {
            return null;
        }
    }

    /**
     * keys:查询多个键值，慎用
     *
     * @param key
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Set<String> keys(K key) {
        return (Set<String>) redisTemplate.keys(key);
    }

    /**
     * evalGet:evalget
     *
     * @param key
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public String evalGet(final String key) {
        Object obj = this.eval("return redis.call('get',KEYS[1])", ReturnType.VALUE, new String[]{key});
        if (obj != null) {
            return new String((byte[]) obj);
        }
        return null;
    }

    public Long evalIncrby(final String key, String incrby) {
        return (Long) this.eval("return  redis.call('incrby',KEYS[1], KEYS[2]) ", ReturnType.VALUE, new String[]{key, incrby});
    }

    public Long evalIncrby(final String key, String incrby, String ttl) {
        return (Long) this.eval("local value=redis.call('incrby',KEYS[1], KEYS[2]) redis.call('expire',KEYS[1],KEYS[3]) return value", ReturnType.VALUE, new String[]{key, incrby, ttl});
    }

    /**
     * evalHincrby:hash增加
     *
     * @param key
     * @param field
     * @param incryBy
     * @param expireTime
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Boolean evalHincrby(final String key, final String field, int incryBy, long expireTime) {
        return (Boolean) this.eval("if redis.call('hincrby',KEYS[1], KEYS[2], KEYS[3])==1 then redis.call('expire',KEYS[1],KEYS[4]) return true end return false", ReturnType.BOOLEAN,
                new String[]{key, field, String.valueOf(incryBy), String.valueOf(expireTime)});
    }

    /**
     * evalHincrby:
     *
     * @param key
     * @param field
     * @param incryBy
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Long evalHincrby(final String key, final String field, int incryBy) {
        return (Long) this.eval("return redis.call('hincrby',KEYS[1], KEYS[2], KEYS[3])", ReturnType.VALUE, new String[]{key, field, String.valueOf(incryBy)});
    }

    /**
     * evalRpop:获取队列数据
     *
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public String evalRpop(final String key) throws UnsupportedEncodingException {
        String lua = "return redis.call('rPop',KEYS[1])";
        Object result = eval(lua, ReturnType.VALUE, new String[]{key});
        if (result != null) {
            return new String((byte[]) result, "UTF-8");
        } else {
            return null;
        }
    }

    /**
     * evalLpush:添加队列
     *
     * @param key
     * @param value
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Boolean evalLpush(final String key, final String value) {
        String lua = "local isOk=redis.call('lPush', KEYS[1], KEYS[2]) if(isOk>=1) then return 1 else return 0 end";
        return (Boolean) eval(lua, ReturnType.BOOLEAN, new String[]{key, value});
    }

    /**
     * evalHset:
     *
     * @param key
     * @param field
     * @param value
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public void evalHset(final String key, final String field, String value) {
        this.eval("return redis.call('hset',KEYS[1], KEYS[2], KEYS[3])", ReturnType.BOOLEAN, new String[]{key, field, value});
    }

    /**
     * evalHdel:删除key
     *
     * @param key
     * @param field
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public void evalHdel(final String key, final String field) {
        this.eval("return redis.call('HDEL',KEYS[1], KEYS[2])", ReturnType.BOOLEAN, new String[]{key, field});
    }

    public Set hkeys(final K key) {
        return redisTemplate.boundHashOps(key).keys();
    }

    private byte[][] keysAndArgs(RedisSerializer argsSerializer, Object[] args) {
        byte[][] keysAndArgs = new byte[args.length][];
        int i = 0;
        for (Object arg : args) {
            if (argsSerializer == null && arg instanceof byte[]) {
                keysAndArgs[i++] = (byte[]) arg;
            } else {
                keysAndArgs[i++] = argsSerializer.serialize(arg);
            }
        }
        return keysAndArgs;
    }

    /**
     * 放入缓存服务器
     *
     * @param key   缓存key值
     * @param value 缓存value值(object对象)
     * @param ttl   缓存有效时间,单位秒,-1表示永久缓存
     */
    public void put(K key, V value, long ttl) {
        BoundValueOperations<K, V> valueOper = redisTemplate.boundValueOps(key);
        valueOper.set(value);
        if (ttl > 0) {
            redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        }
    }

    /**
     * 保存对象对缓存服务器
     *
     * @param key         缓存key
     * @param value       缓存值
     * @param expiredDate 缓存截止日期
     */
    public void put(K key, V value, Date expiredDate) {
        BoundValueOperations<K, V> valueOper = redisTemplate.boundValueOps(key);
        valueOper.set(value);
        redisTemplate.expireAt(key, expiredDate);
    }

    /**
     * 获取缓存服务器存储结果值
     *
     * @param key 缓存key值
     * @return 返回结果对象
     */
    public Object get(K key) {
        BoundValueOperations<K, V> boundValueOper = redisTemplate.boundValueOps(key);
        if (boundValueOper == null) {
            return null;
        }
        return boundValueOper.get();
    }

    /**
     * 保存List类型数据,存活时间为24小时
     *
     * @param key  存储key
     * @param list 存储list值
     */
    @SuppressWarnings("unchecked")
    public void putList(final K key, final V list) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.watch(rawKey(key));
                operations.multi();
                operations.delete(key);
                BoundListOperations<K, V> listOper = operations.boundListOps(key);
                listOper.leftPush(list);
                operations.expire(key, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * 将值放到队列中去
     *
     * @param key
     * @param value
     * @return
     */
    public Long putToQueue(final K key, final V value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public Long putToQueue(K key, V[] values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * putToQueue:失效时间为秒
     *
     * @param key
     * @param value
     * @param ttl
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Long putToQueue(final K key, final V value, long ttl) {
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 获取队列大小
     *
     * @param key
     * @return
     */
    public Long getQueueSize(final K key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 从队列中取值
     *
     * @param key
     * @return
     */
    public Object pollFromQueue(K key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    /**
     * 从队列中取值
     *
     * @param key
     * @return
     */
    public Object pullFirstFromQueue(K key,V value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 保存list类型数据
     *
     * @param key  存储key
     * @param list 存储list值
     * @param ttl  存活时间,单位秒
     */
    @SuppressWarnings("unchecked")
    public void putList(final K key, final V list, final long ttl) {

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.watch(rawKey(key));
                operations.multi();
                operations.delete(key);
                BoundListOperations<K, V> listOper = operations.boundListOps(key);
                listOper.leftPush(list);
                operations.expire(key, ttl, TimeUnit.SECONDS);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * hexists:是否含有制定field
     *
     * @param key
     * @param field
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public boolean hexists(final K key, final V field) {
        return redisTemplate.boundHashOps(key).hasKey(field);
    }

    /**
     * hset:hset
     *
     * @param key
     * @param field
     * @param value
     * @param ttl
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public void hset(final K key, final V field, final V value, long ttl) {
        redisTemplate.boundHashOps(key).put(field, value);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    /**
     * hget:hget
     *
     * @param key
     * @param field
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Object hget(final K key, final V field) {
        return redisTemplate.boundHashOps(key).get(field);
    }

    /**
     * hset:hset
     *
     * @param key
     * @param field
     * @param value
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public void hset(final K key, final V field, final V value) {
        redisTemplate.boundHashOps(key).put(field, value);
    }

    public void hdel(final K key, final V field) {
        redisTemplate.boundHashOps(key).delete(field);
    }

    /**
     * 保存list类型数据
     *
     * @param key         存储key
     * @param list        存储list值
     * @param expiredDate 存活截止日期
     */
    public void putList(final K key, final V list, final Date expiredDate) {

        redisTemplate.execute(new SessionCallback<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.watch(rawKey(key));
                operations.multi();
                operations.delete(key);
                BoundListOperations<K, V> listOper = operations.boundListOps(key);
                listOper.leftPush(list);
                operations.expireAt(key, expiredDate);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * 获取List值
     *
     * @param key
     * @return 获取结果为List类型的值
     */
    public List<? extends Object> getList(K key) {
        BoundListOperations<K, V> listOper = redisTemplate.boundListOps(key);
        if (listOper == null) {
            return null;
        }
        return listOper.range(0, -1);
    }

    public void remove(K key) {
        redisTemplate.delete(key);
    }

    public void remove(Collection<K> ks) {
        redisTemplate.delete(ks);
    }

    public long getExpire(K key) {
        return redisTemplate.getExpire(key);
    }

    public boolean containsKey(K key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 保存Set类型数据
     *
     * @param key   存储key
     * @param value 存储值
     * @param ttl   存活时间,单位秒
     */
    @SuppressWarnings("unchecked")
    public void putSet(final K key, final V value, final long ttl) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                BoundSetOperations<K, V> setOper = operations.boundSetOps(key);
                setOper.add(value);
                operations.expire(key, ttl, TimeUnit.SECONDS);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * 保存Set类型数据
     *
     * @param key         存储key
     * @param Set         存储值
     * @param expiredDate 存活截止日期
     */
    @SuppressWarnings("unchecked")
    public void putSet(final K key, final V value, final Date expiredDate) {

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                operations.delete(key);
                BoundSetOperations<K, V> setOper = operations.boundSetOps(key);
                setOper.add(value);
                operations.expireAt(key, expiredDate);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * 保存set类型的值，默认有效期1天
     *
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void putSet(final K key, final V value) {

        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.watch(key);
                operations.multi();
                BoundSetOperations<K, V> setOper = operations.boundSetOps(key);
                setOper.add(value);
                operations.expire(key, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
                operations.exec();
                return null;
            }
        });
    }

    /**
     * 获取Set类型值
     *
     * @param key
     * @return
     */
    public Set<V> getSet(K key) {
        BoundSetOperations<K, V> setOper = redisTemplate.boundSetOps(key);
        return setOper.members();
    }

    public void putHash(final K key, final Map<? extends V, ? extends Object> value) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public Object execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.watch(rawKey(key));
                operations.multi();
                operations.delete(key);
                BoundHashOperations<K, Object, Object> hashOper = redisTemplate.boundHashOps(key);
                hashOper.putAll(value);
                operations.expire(key, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
                operations.exec();
                return null;
            }
        });
    }

    public void putHash(final K key, final K field, V value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public void putHash(final K key, final K field, V value, long ttl) {
        redisTemplate.opsForHash().put(key, field, value);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    public void putHashNoExpire(final K key, final Map<? extends K, ? extends Object> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }

    /**
     * 获取hash值
     *
     * @param key
     * @return 获取结果为hash类型的值
     */
    public Map<Object, Object> getHash(K key) {
        BoundHashOperations<K, Object, Object> hashOper = redisTemplate.boundHashOps(key);
        if (hashOper == null) {
            return null;
        }
        return hashOper.entries();
    }

    /**
     * 获取hash值
     *
     * @param key
     * @param field
     * @return
     * @author <a href="mailto:284040429@xingbook.com" >张明俊</a>
     */
    public Object getHash(K key, K field) {
        BoundHashOperations<K, Object, Object> hashOper = redisTemplate.boundHashOps(key);
        if (hashOper == null) {
            return null;
        }
        return hashOper.get(field);
    }

    /**
     * 批量获取hash值
     *
     * @param key
     * @param fieldList
     * @return
     * @author <a href="mailto:284040429@xingbook.com" >张明俊</a>
     */
    public List<Object> getHash(K key, List<Object> fieldList) {
        BoundHashOperations<K, Object, Object> hashOper = redisTemplate.boundHashOps(key);
        if (hashOper == null) {
            return null;
        }
        return hashOper.multiGet(fieldList);
    }

    /**
     * 删除hash值
     *
     * @param key
     * @param field
     * @author <a href="mailto:284040429@xingbook.com" >张明俊</a>
     */
    public void removeHash(K key, Object... field) {
        BoundHashOperations<K, Object, Object> hashOper = redisTemplate.boundHashOps(key);
        if (hashOper == null) {
            return;
        }
        hashOper.delete(field);
    }

    public void kset(final K key, V value, final long ttl) {
        final byte[] bytes = kryoSerializer.serialize(value);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(rawKey(key), ttl, bytes);
                return null;
            }
        });
    }

    public void kset(final K key, V value) {
        final byte[] bytes = kryoSerializer.serialize(value);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(rawKey(key), DEFAULT_EXPIRE_TIME, bytes);
                return null;
            }
        });
    }

    public Object kget(final K key) {
        Object result = redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] bytes = connection.get(rawKey(key));
                if (bytes == null) {
                    return null;
                }
                return kryoSerializer.deserialize(bytes);
            }
        });
        return result;
    }

    public long ttl(final K key) {
        Object obj = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ttl(rawKey(key));
            }
        });
        return (Long) obj;
    }

    public long incrementBy(final K key, final long incrBy, final long ttl) {
        Object obj = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                long ret = connection.incrBy(rawKey(key), incrBy);
                connection.expire(rawKey(key), ttl);
                return ret;
            }
        });
        return (Long) obj;
    }

    public long incrementBy(final K key, final long incrBy) {
        Object obj = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                long ret = connection.incrBy(rawKey(key), incrBy);
                return ret;
            }
        });
        return (Long) obj;
    }

    public long decrement(final K key, final long ttl) {
        Object obj = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                long ret = connection.decr(rawKey(key));
                connection.expire(rawKey(key), ttl);
                return ret;
            }
        });
        return (Long) obj;
    }

    private byte[] rawKey(K key) {
        return key.toString().getBytes();
    }

    public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setKryoSerializer(KryoRedisSerializer<Object> kryoSerializer) {
        this.kryoSerializer = kryoSerializer;
    }

    /**
     * zrem:移除
     *
     * @param key
     * @param value
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public long zrem(K key, V value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    public boolean putZset(K key, V value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 添加批量插入
     *
     * @param key
     * @param tuples
     * @return
     * @author <a href="mailto:284040429@qq.com" >张明俊</a>
     */
    public Long putZsetBatch(K key, Set<TypedTuple<V>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }

    public Set<V> reverseRangeByScore(K key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    public Set<V> rangeByScore(K key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    public Set<V> range(K key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<V> rangeByScore(K key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    public Set<V> rangeByScoreAndRemove(K key, final double min, final double max, final long offset, final long count) {
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();

        final byte[] rawKey = keySerializer.serialize(key);

        Set<byte[]> rawValues = redisTemplate.execute(new RedisCallback<Set<byte[]>>() {

            @Override
            public Set<byte[]> doInRedis(RedisConnection connection) {
                Set<byte[]> rawValues = connection.zRangeByScore(rawKey, min, max, offset, count);
                for (Iterator<byte[]> iter = rawValues.iterator(); iter.hasNext(); ) {
                    byte[] rawValue = iter.next();
                    long elementNum = connection.zRem(rawKey, rawValue);
                    if (elementNum == 0) {
                        iter.remove();
                    }
                }
                return rawValues;
            }
        }, true);
        Set<V> set = new LinkedHashSet<V>();
        for (byte[] rawValue : rawValues) {
            V value = (V) keySerializer.deserialize(rawValue);
            set.add(value);
        }
        return set;
    }

    public long getZsetsize(K key) {
        return redisTemplate.opsForZSet().size(key);
    }

    public Long removeZsetByScore(K key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    public Long removeRange(K key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    public Object pop(K key) {
        return redisTemplate.opsForSet().pop(key);
    }

    public Long sadd(K key, V values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Boolean isMembers(K key, V value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long removeZset(K key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * add by zhouc
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean setNX(K key, V value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * expire:设置过期时间
     *
     * @param key
     * @param ttl
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public boolean expire(K key, long ttl) {
        return redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 当KEY不存在时设置value，并且返回true同时设置超时时间 否则返回false
     *
     * @param key
     * @param value
     * @param ttl   单位毫秒
     * @return
     * @author <a href="mailto:zhaihuilin@qq.com" >翟惠林</a>
     */
    public Boolean setNX(K key, V value, long ttl) {
        boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (flag) {
            redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
        }
        return flag;
    }

    /**
     * add by zhouc
     *
     * @param key
     * @param value
     * @return
     */
    public V getAndSet(K key, V value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * evalSetNx:分布式锁
     *
     * @param key
     * @param ttl
     * @return
     * @author <a href="mailto:xupengcheng@qq.com" >徐鹏程</a>
     */
    public Boolean evalSetNx(String key, long ttl) {
        String lua = "if redis.call('setnx',KEYS[1],1)==1 then redis.call('expire',KEYS[1],KEYS[2]) return true end return false";
        return (boolean) eval(lua, ReturnType.BOOLEAN, new String[]{key, String.valueOf(ttl)});
    }

    public Boolean evalSet(String key, String value) {
        return (Boolean) eval("redis.call('set',KEYS[1], KEYS[2]) return true", ReturnType.BOOLEAN, new String[]{key, value});
    }

    public Boolean evalSet(String key, String value, String ttl) {
        return (Boolean) eval("redis.call('set',KEYS[1], KEYS[2]) redis.call('expire',KEYS[1],KEYS[3]) return true", ReturnType.BOOLEAN, new String[]{key, value, ttl});
    }

    /**
     * getLockByLong:TODO redis分布式锁
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     * @author <a href="mailto:284040429@qq.com" >张明俊</a>
     */
    public Boolean getLockByLong(K key, V value, long timeout) {
        if (!(value instanceof Long)) {
            return false;
        }
        Boolean sendSMS = setNX(key, value);
        if (!sendSMS) {
            Long sendTime = (Long) get(key);
            if (sendTime != null) {
                if (System.currentTimeMillis() - sendTime >= timeout) {
                    Long nowTime = (Long) getAndSet(key, value);
                    if (nowTime == null || nowTime.equals(sendTime)) {
                        sendSMS = true;
                    }
                }
            } else {
                Long nowTime = (Long) getAndSet(key, value);
                if (nowTime == null) {
                    sendSMS = true;
                }
            }
        }
        return sendSMS;
    }

    /**
     * 获取第一个zset的元素
     *
     * @param key
     * @return
     * @author <a href="mailto:zhaihuilin@qq.com" >翟惠林</a>
     */
    public V rangeZSetFirst(K key) {
        Set<V> set = redisTemplate.opsForZSet().range(key, 0, 1);
        if (set != null && set.size() > 0) {
            return set.iterator().next();
        }
        return null;
    }

    public Long getLong(final K key) {
        Long result = redisTemplate.execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] bytes = connection.get(rawKey(key));
                if (bytes == null) {
                    return null;
                }

                return new GenericToStringSerializer<Long>(Long.class).deserialize(bytes);
            }
        });

        return result;
    }

    public void setLong(final K key, final Long value, final long ttl) {
        Long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(rawKey(key), value.toString().getBytes());
                connection.expire(rawKey(key), ttl);
                return null;
            }
        });

    }

    public Long getTtl(final K key) {
        Long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ttl(rawKey(key));
            }
        });
        return result;

    }

    public long getVal(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.get();
    }

    // 自增计数器
    public long getValAndAdd(String key, int step) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return counter.addAndGet(step);
    }

    /**
     * 批量插入数据到队列中
     *
     * @param key
     * @param values
     * @return
     * @author <a href="mailto:284040429@qq.com" >张明俊</a>
     */
    public boolean evalHsetBatch(final String key, Map<String, String> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return false;
        }

        StringBuffer lua = new StringBuffer();
        lua.append("local result=redis.call('HMSET', KEYS[1],");
        for (Entry<String, String> entity : dataMap.entrySet()) {
            lua.append(" '" + entity.getKey() + "','" + entity.getValue() + "',");
        }
        lua.deleteCharAt(lua.length() - 1);
        lua.append(") return result");
        byte[] result = (byte[]) eval(lua.toString(), ReturnType.VALUE, new String[]{key});
        if (result == null) {
            return false;
        }

        if ("OK".equals(new String(result))) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有的hash值
     *
     * @param key
     * @return
     * @author <a href="mailto:284040429@qq.com" >张明俊</a>
     */
    public Map<String, String> evalHgetAll(final String key) {
        Map<String, String> resultMap = new HashMap<String, String>(4);
        String lua = "local result = redis.call('HGETALL',KEYS[1]) return result";
        Object obj = eval(lua, ReturnType.MULTI, new String[]{key});
        if (obj == null) {
            return null;
        }

        List<Object> list = (List<Object>) obj;
        for (int i = 0; i < list.size(); i = i + 2) {
            resultMap.put(new String((byte[]) list.get(i)), new String((byte[]) list.get(i + 1)));
        }
        return resultMap;
    }

    public boolean evalLpushBatch(final String key, List<String> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return false;
        }

        StringBuffer lua = new StringBuffer();
        lua.append(" local isOk=redis.call('LPUSH', KEYS[1],");
        for (String value : valueList) {
            lua.append(" '" + value + "',");
        }
        lua.deleteCharAt(lua.length() - 1);
        lua.append(")");
        lua.append(" if(isOk>0) then return 1 else return 0 end");

        Boolean result = (Boolean) eval(lua.toString(), ReturnType.BOOLEAN, new String[]{key});
        return result;
    }
    
    public Long putToQueueBatch(K key, V... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }
    
    /**
     * 获取并删除数据
     * @author <a href="mailto:284040429@qq.com" >张明俊</a>
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Object[] rangeAndremoveByScore(String key,long min,long max) {
        Object obj = this.eval("local dataSet=redis.call('ZRANGEBYSCORE',KEYS[1],KEYS[2],KEYS[3]) redis.call('ZREMRANGEBYSCORE',KEYS[1],KEYS[2],KEYS[3]) return dataSet", ReturnType.MULTI, new String[]{key,min+"",max+""});
        if (obj != null) {
            List list = (List) obj;
            if (list == null||list.isEmpty()) {
            	return null;
            }
            int size=list.size();
            Object[] result = new Object[size];
            for (int i = 0; i < size; i++) {
            	Object data=redisTemplate.getValueSerializer().deserialize((byte[])list.get(i));
            	result[i]=data;
            }
            return result;
        }
        return null;
    }

    /**
     * 根据给定的布隆过滤器添加值
     */
    public <T> void addByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, K key, T value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            redisTemplate.opsForValue().setBit(key, i, true);
        }
        
    }

    /**
     * 根据给定的布隆过滤器判断值是否存在
     */
    public <T> boolean includeByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, K key, T value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            if (!redisTemplate.opsForValue().getBit(key, i)) {
                return false;
            }
        }

        return true;
    }
    
    
    public void multiSetBit(String name, boolean value, long... offsets) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (long offset : offsets) {
                connection.setBit(name.getBytes(), offset, value);
            }
            return null;
        });
    }
    
    public List<Boolean> multiGetBit(String name, long... offsets) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (long offset : offsets) {
                connection.getBit(name.getBytes(), offset);
            }
            return null;
        });
        List<Boolean> list = new ArrayList<>();
        results.forEach(obj -> {
            list.add((Boolean) obj);
        });
        return list;
    }
    
    
    /**
     * @param key
     * @param step
     * @return
     */
    public boolean hasKey(final K key) {
        return redisTemplate.hasKey(key);
    }

    public StringRedisSerializer getStringSerializer() {
        return stringSerializer;
    }

    public void setStringSerializer(StringRedisSerializer stringSerializer) {
        this.stringSerializer = stringSerializer;
    }

    public RedisTemplate<K, V> getRedisTemplate() {
        return redisTemplate;
    }

    public KryoRedisSerializer<Object> getKryoSerializer() {
        return kryoSerializer;
    }

    
   
}
