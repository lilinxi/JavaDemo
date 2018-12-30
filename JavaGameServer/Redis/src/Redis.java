import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Mac
 * 下载：https://redis.io/download
 * 解压：tar zxvf redis-5.0.2.tar.gz
 * 移动到： mv redis-5.0.2 /usr/local/
 * 切换到：cd /usr/local/redis-5.0.2/
 * 编译测试 sudo make test
 * 编译安装 sudo make install
 * 启动：redis-server
 * <p>
 * For idea：Iedis
 * <p>
 * Redis 命令参考：http://redisdoc.com/index.html
 */
public class Redis {
    private static Redis instance;
    public static Logger log = LoggerFactory.getLogger(Redis.class);
    public static final int DB = 0;// 默认数据库

    public static Redis getInstance() {
        if (instance == null) {
            instance = new Redis();
            instance.init();
        }
        return instance;
    }

    private JedisPool pool;
    public String host;
    public int port;

    private void init() {
        host = "127.0.0.1";
        port = 6379;
        log.info("Redis at {}:{}", host, port);
        pool = new JedisPool(host, port);
    }

    public static void destroy() {
        getInstance().pool.destroy();
    }

    //    hash 与 object 之间的相互转换
    public static Map<String, String> objectToHash(Object obj)
            throws IntrospectionException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Map<String, String> map = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            if (!property.getName().equals("class")) {
                map.put(property.getName(), ""
                        + property.getReadMethod().invoke(obj));
            }
        }
        return map;
    }

    public static void hashToObject(Map<?, ?> map, Object obj)
            throws IllegalAccessException, InvocationTargetException {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getValue().equals("null")) {
                entry.setValue(null);
            }
        }
        BeanUtils.populate(obj, map);
    }

    @SuppressWarnings("unchecked")
    public static <T> T hashToObject(Map<?, ?> map, Class c)
            throws IllegalAccessException, InvocationTargetException,
            InstantiationException {
        Object obj;
        try {
            obj = c.getDeclaredConstructor().newInstance();
            hashToObject(map, obj);
            return (T) obj;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    Redis 哈希操作：hmget，hmset，hexist，hdel，hgetAll，hget，hset
    public List<String> hmget(int db, String key, String... fields) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        List<String> ret = redis.hmget(key, fields);
        redis.select(db);
        redis.close();
        return ret;
    }

    public String hmset(int db, String key, Object object)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, IntrospectionException {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        String ret = redis.hmset(key, objectToHash(object));
        redis.close();
        return ret;
    }

    public String hmset(int db, String key, Map<String, String> fields)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, IntrospectionException {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        String ret = redis.hmset(key, fields);
        redis.close();
        return ret;
    }

    public boolean hexist(int db, String key, String field) {
        if (key == null) {
            return false;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        boolean ret = redis.hexists(key, field);
        redis.close();
        return ret;
    }

    public Long hdel(int db, String key, String... fields) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Long cnt = redis.hdel(key, fields);
        redis.close();
        return cnt;
    }

    public Map<String, String> hgetAll(int db, String key) {
        if (key == null) {
            return null;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Map<String, String> ret = redis.hgetAll(key);
        redis.close();
        return ret;
    }

    public String hget(int db, String key, String field) {
        if (key == null) {
            return null;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        String ret = redis.hget(key, field);
        redis.close();
        return ret;
    }

    public void hset(int db, String key, String field, String value) {
        if (field == null || field.length() == 0) {
            return;
        }
        if (value == null || value.length() == 0) {
            return;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        redis.hset(key, field, value);
        redis.close();
    }

    //    Redis 字符串操作：set，get
    public void set(int db, String key, String value) {
        if (value == null || key == null) {
            return;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        redis.set(key, value);
        redis.close();
    }

    public String get(int db, String key) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        String ret = redis.get(key);
        redis.close();
        return ret;
    }

    //    Redis 集合操作：sadd，smove，sremove，sget，scard，sismember

    /**
     * 添加元素到集合中
     *
     * @param key
     * @param element
     */
    public boolean sadd(int db, String key, String... element) {
        if (element == null || element.length == 0) {
            return false;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        boolean success = redis.sadd(key, element) == 1;
        redis.close();
        return success;
    }

    public boolean smove(int db, String oldKey, String newKey, String element) {
        if (element == null) {
            return false;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        boolean success = (redis.smove(oldKey, newKey, element) == 1);
        redis.close();
        return success;
    }

    /**
     * 删除指定set内的元素
     */
    public boolean sremove(int db, String key, String... element) {
        if (element == null) {
            return false;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        boolean success = (redis.srem(key, element) == 1);
        redis.close();
        return success;
    }

    public Set<String> sget(int db, String key) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<String> m = redis.smembers(key);
        redis.close();
        return m;
    }

    /**
     * 返回set的的元素个数
     *
     * @param key
     * @return
     * @Title: scard
     */
    public long scard(int db, String key) {
        Jedis redis = this.pool.getResource();
        long size = redis.scard(key);
        redis.close();
        return size;
    }

    /**
     * 测试元素是否存在
     *
     * @param key
     * @param element
     * @return
     */
    public boolean sismember(int db, String key, String element) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        boolean ret = redis.sismember(key, element);
        redis.close();
        return ret;
    }

    //    Redis 列表操作：laddList，lpush，rpush，lrange，lgetList，（lexist），lpop，lrem
    public void laddList(int db, String key, String... elements) {
        if (elements == null || elements.length == 0) {
            return;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        redis.lpush(key, elements);
        redis.close();
    }

    /**
     * 依次插入表头
     *
     * @param key
     * @param elements
     * @Title: lpush
     */
    public void lpush(int db, String key, String... elements) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        redis.lpush(key, elements);
        redis.close();
    }


    /**
     * 依次插入表尾
     *
     * @param db
     * @param key
     * @param elements
     */
    public void rpush(int db, String key, String... elements) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        redis.rpush(key, elements);
        redis.close();
    }

    /**
     * 返回指定区间内的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @Title: lrange
     */
    public List<String> lrange(int db, String key, int start, int end) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        List<String> list = redis.lrange(key, start, end);
        redis.close();
        return list;
    }

    public List<String> lgetList(int db, String key) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        long len = redis.llen(key);
        List<String> ret = redis.lrange(key, 0, len - 1);
        redis.close();
        return ret;
    }

    /**
     * 列表list中是否包含value
     *
     * @param key
     * @param value
     * @return
     */
    public boolean lexist(int db, String key, String value) {
        List<String> list = lgetList(db, key);
        return list.contains(value);
    }

    public List<String> lgetList(int db, String key, long len) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        long max = redis.llen(key);
        long l = max > len ? len : max;
        List<String> ret = redis.lrange(key, 0, l - 1);
        redis.close();
        return ret;
    }

    public String lpop(int db, String key) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        String value = redis.lpop(key);
        redis.close();
        return value;
    }

    /**
     * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count。
     * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
     * count = 0 : 移除表中所有与 value 相等的值。
     *
     * @param db
     * @param key
     * @param count
     * @param value
     */
    public void lrem(int db, String key, int count, String value) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        redis.lrem(key, count, value);
        redis.close();
    }

    //    Redis 键操作：del，keys（delKeyLikes），exists
    public Long del(int db, String... keys) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Long cnt = redis.del(keys);
        redis.close();
        return cnt;
    }

    /**
     * 模糊删除
     *
     * @param key
     * @return
     */
    public Long delKeyLikes(int db, String key) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<String> keys = redis.keys(key);
        Long cnt = redis.del(keys.toArray(new String[keys.size()]));
        redis.close();
        return cnt;
    }

    /**
     * 判断某一个key值的存储结构是否存在
     *
     * @param key
     * @return
     * @Title: exists
     */
    public boolean exists(int db, String key) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        boolean yes = redis.exists(key);
        redis.close();
        return yes;
    }

    //    Redis 有序集合操作：zadd，zincrby，zscore，zrevrank。。。

    public long zadd(int db, String key, int score, String member) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        long ret = 0;
        try {
            ret = redis.zadd(key, score, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redis.close();
        }
        return ret;
    }

    /**
     * 添加 分数，并返回修改后的值
     *
     * @param key
     * @param update
     * @param member
     * @return
     */
    public double zincrby(int db, String key, int update, String member) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        double ret = redis.zincrby(key, update, member);
        redis.close();
        return ret;
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值,存在返回score，不存在返回-1
     *
     * @param key
     * @param member
     * @return
     */
    public double zscore(int db, String key, String member) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Double ret = redis.zscore(key, member);
        redis.close();
        if (ret == null) {
            return -1;
        }
        return ret;
    }

    /**
     * 按从大到小的排名，获取 member的排名，最大排名为 0
     *
     * @param key
     * @param member
     * @return
     */
    public long zrevrank(int db, String key, String member) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        long ret = redis.zrevrank(key, member);
        redis.close();
        return ret;
    }

    /**
     * 按照score的值从小到大排序，返回member的排名，最小排名为 0
     *
     * @param key
     * @param member
     * @return 设置为名次从1开始。返回为-1，表示member无记录
     * @Title: zrank
     */
    public long zrank(int db, String key, String member) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        long ret = -1;
        Long vv = redis.zrank(key, member);
        if (vv != null) {
            ret = vv.longValue();
        }
        redis.close();
        if (ret != -1) {
            ret += 1;
        }
        return ret;
    }

    /**
     * 【min， max】从小到大排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     * @Title: zrangebyscore
     */
    public Set<String> zrangebyscore(int db, String key, long min, long max) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<String> ss = redis.zrangeByScore(key, min, max);
        redis.close();
        return ss;
    }

    public Set<String> zrange(int db, String key, long min, long max) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<String> ss = redis.zrange(key, min, max);
        redis.close();
        return ss;
    }

    /**
     * min 和 max 都是score的值，获得一个包含了score的元组集合。元组（Tuple）
     * 笛卡尔积中每一个元素（d1，d2，…，dn）叫作一个n元组（n-tuple）或简称元组
     *
     * @param key
     * @param min
     * @param max
     * @return
     * @Title: zrangebyscorewithscores
     */
    public Set<Tuple> zrangebyscorewithscores(int db, String key, long min, long max) {
        Jedis redis = this.pool.getResource();
        if (redis == null) {
            return null;
        }
        redis.select(db);
        Set<Tuple> result = null;
        try {
            result = redis.zrangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redis.close();
        }
        return result;
    }

    /**
     * zrevrangeWithScores ： 从大到小排序 zrangeWithScores ： 从小到大排序
     *
     * @param key
     * @param start ： （排名）0表示第一个元素，-x：表示倒数第x个元素
     * @param end   ： （排名）-1表示最后一个元素（最大值）
     * @return 返回 排名在start 、end之间带score元素
     * @Title: zrangeWithScores
     */
    public Map<String, Double> zrevrangeWithScores(int db, String key, long start, long end) {
        Jedis redis = this.pool.getResource();
        if (redis == null) {
            return null;
        }
        redis.select(db);
        Set<Tuple> result = null;
        try {
            result = redis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redis.close();
        }
        return tupleToMap(result);
    }

    public Map<String, Double> tupleToMap(Set<Tuple> tupleSet) {
        if (tupleSet == null)
            return null;
        Map<String, Double> map = new LinkedHashMap<>();
        for (Tuple tup : tupleSet) {
            map.put(tup.getElement(), tup.getScore());
        }
        return map;
    }

    /**
     * 删除key中的member
     *
     * @param key
     * @param member
     * @return
     * @Title: zrem
     */
    public long zrem(int db, String key, String member) {
        Jedis redis = this.pool.getResource();
        if (redis == null) {
            return -1;
        }
        redis.select(db);
        long result = -1;
        try {
            result = redis.zrem(key, member);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redis.close();
        }
        return result;
    }

    /**
     * 从高到低排名，返回前 num 个score和member
     *
     * @param key
     * @param num
     * @return
     */
    public Set<Tuple> ztopWithScore(int db, String key, int num) {
        if (num <= 0) {
            return null;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<Tuple> ret = redis.zrevrangeWithScores(key, 0, num - 1);
        redis.close();
        return ret;
    }

    /**
     * 返回score区间的member
     *
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> zrevrangeByScore(int db, String key, int max, int min) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<String> ret = redis.zrevrangeByScore(key, max, min);
        redis.close();
        return ret;
    }

    /**
     * 从高到低排名，返回前 num 个
     *
     * @param key
     * @param num
     * @return
     */
    public Set<String> ztop(int db, String key, int num) {
        if (num <= 0) {
            return null;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<String> ret = redis.zrevrange(key, 0, num - 1);
        redis.close();
        return ret;
    }

    /**
     * 从高到低排名，返回start到end的前 num 个
     */
    public Set<String> ztop(int db, String key, int start, int end) {
        if (end <= start) {
            return null;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        Set<String> ret = redis.zrevrange(key, start, end - 1);
        redis.close();
        return ret;
    }

    /**
     * 返回zset的的元素个数
     *
     * @param key
     * @return
     * @Title: zcard_
     */
    public long zcard(int db, String key) {
        Jedis redis = this.pool.getResource();
        redis.select(db);
        long size = redis.zcard(key);
        redis.close();
        return size;
    }

    //    发布，订阅

    /**
     * 将信息 message 发送到指定的频道 channel。
     *
     * @param db
     * @param channel
     * @param message
     */
    public void publish(int db, String channel, String message) {
        if (channel == null || message == null) {
            return;
        }
        Jedis redis = this.pool.getResource();
        redis.select(db);
        redis.publish(channel, message);
        redis.close();
    }
}
