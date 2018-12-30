# Redis 的特点

1. 速度快，运行在内存中，执行速度非常快。
2. 数据类型丰富：
    1. String，上限512MB
    2. List
    3. set
    4. sorted set，每个元素关联一个 score，以提供排序依据
    5. hash，字符串与字符串之间的映射
3. 操作原子性，所有的 Redis 操作都是原子的，保证多个客户端并发访问时获取到 Redis 服务器的值为最新值。
4. 持久化，与 Memcache 不同的是，Redis 提供了数据持久化的功能，提供 AOF 和 RDB 两种持久化模式。
    1. AOF，只追加数据到文件，记录所有的写指令。
    2. RDB，先将数据写到临时文件，再用临时文件替换上次持久化好的文件，会单独 fork 一个进程进行持久化，而主进程不进行任何 IO 操作。
5. 应用场景丰富：缓存、消息、队列（Redis 原生支持订阅/发布）、数据库等。

> Pub/Sub功能（means Publish, Subscribe）即发布及订阅功能。基于事件的系统中，Pub/Sub是目前广泛使用的通信模型，它采用事件作为基本的通信机制，提供大规模系统所要求的松散耦合的交互模式：订阅者(如客户端)以事件订阅的方式表达出它有兴趣接收的一个事件或一类事件；发布者(如服务器)可将订阅者感兴趣的事件随时通知相关订阅者。

# Redis 的主从复制

Redis 的主从同步是异步执行的，因此主从同步并不会减少 Redis 的运行性能。给 Redis 主服务器添加一个服务器，只需要在从服务器的 redis.conf 中添加 slaveof masterip masterpot 命令，先启动主服务器，再启动从服务器，从服务器会根据配置中的主服务器的 IP 端口对主服务器发出同步命令，然后进行数据同步。

多组主从同步及主从切换可以通过 Redis Sentinel 进行管理，Sentinel 能对 Redis 集群中的主服务器及从服务器进行管理，当主节点宕机时，Sentinel 能自动切换到从服务器。

# Redis 安装配置

 * Mac
 * 
 * 下载：https://redis.io/download
 * 解压：tar zxvf redis-5.0.2.tar.gz
 * 移动到： mv redis-5.0.2 /usr/local/
 * 切换到：cd /usr/local/redis-5.0.2/
 * 编译测试 sudo make test
 * 编译安装 sudo make install
 * 启动：redis-server
 * 
 * For idea：Iedis
 * 
 * Redis 命令参考：http://redisdoc.com/index.html

# Redis 示例

## Redis 工具类封装

```
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

```

## Redis 使用

```
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisDemo {
    public static void main(String[] args) {
        RedisDemo demo = new RedisDemo();
        demo.run();
    }

    public void run() {
        Redis redis = Redis.getInstance();
        /** redis save **/
        System.out.println("=============redis save==============");
        // string save
        System.out.println("string save:调用set时，若key不存在则添加key，否则为修改key对应的值");
        redis.set(Redis.DB, "testKey1", "test string val1");
        // set save
        System.out.println("set save:set中的元素不允许出现重复且无序");
        redis.sadd(Redis.DB, "testKey2", "test set val1");
        redis.sadd(Redis.DB, "testKey2", "test set val2");
        redis.sadd(Redis.DB, "testKey2", "test set val3");
        // hash save
        System.out.println("hash save:调用hset时，若key不存在则创建key，若hash中存在这个hashkey，则修改其值，不存在则添加一条hash数据");
        redis.hset(Redis.DB, "testKey3", "hashKey1", "hashVal1");
        redis.hset(Redis.DB, "testKey3", "hashKey2", "hashVal2");
        redis.hset(Redis.DB, "testKey3", "hashKey3", "hashVal3");
        redis.hset(Redis.DB, "testKey3", "hashKey4", "hashVal4");
        // list save
        System.out.println("list save:数据在链表中是有序的，并可以重复添加数据");
        redis.lpush(Redis.DB, "testKey4", "test list val1");
        redis.lpush(Redis.DB, "testKey4", "test list val2");
        redis.lpush(Redis.DB, "testKey4", "test list val3");
        // sorted set save
        System.out.println("sorted set save:有序set中的元素是有序的");
        redis.zadd(Redis.DB, "testKey5", 1, "test zset val1");
        redis.zadd(Redis.DB, "testKey5", 2, "test zset val2");
        redis.zadd(Redis.DB, "testKey5", 3, "test zset val3");
        redis.zadd(Redis.DB, "testKey5", 4, "test zset val4");
        /** redis get **/
        System.out.println("=============redis get==============");
        // string get
        String stringRet = redis.get(Redis.DB, "testKey1");
        System.out.println("string get:" + stringRet);
        // set get
        Set<String> setRet = redis.sget(Redis.DB, "testKey2");
        System.out.print("set get:");
        for (String string : setRet) {
            System.out.print(string + ",");
        }
        System.out.println();
        // hash get
        String hashKeyRet = redis.hget(Redis.DB, "testKey3", "hashKey2");
        System.out.println("hash key get:" + hashKeyRet);
        Map<String, String> hashRet = redis.hgetAll(Redis.DB, "testKey3");
        System.out.print("hash get:");
        for (String string : hashRet.keySet()) {
            System.out.print("key[" + string + "]" + "value[" + hashRet.get(string) + "],");
        }
        System.out.println();
        // list get
        List<String> listRet = redis.lgetList(Redis.DB, "testKey4");
        System.out.print("list get:");
        for (String string : listRet) {
            System.out.println(string + ",");
        }
        // zset get
        long val2Rank = redis.zrank(Redis.DB, "testKey5", "test zset val2");
        System.out.println("zset get val2 rank:" + val2Rank);
        Set<String> zsetRet = redis.zrange(Redis.DB, "testKey5", 0, 3);
        System.out.print("zset get range:");
        for (String string : zsetRet) {
            System.out.println(string + ",");
        }
        /** redis delete **/
        System.out.println("=============redis delete==============");
        // string delete
        System.out.println("string delete:调用Redis的del方法，可直接删除key，对于所有的数据类型来说，都可以通过这种方式直接删除整个key");
        redis.del(Redis.DB, "testKey1");
        // set delete
        System.out.println("set delete:删除set中的val3");
        redis.sremove(Redis.DB, "testKey2", "test set val3");
        // hash delete
        System.out.println("hash delete:删除hash中key为hashKey4的元素");
        redis.hdel(Redis.DB, "testKey3", "hashKey4");
        // list delete
        System.out.println("list delete:删除list中值为test list val3的元素，其中count参数，0代表删除全部，正数代表正向删除count个此元素，负数代表负向删除count个此元素");
        redis.lrem(Redis.DB, "testKey4", 0, "test list val3");
        // zset delete
        System.out.println("zset delete:同set删除元素的方式相同");
        redis.zrem(Redis.DB, "testKey5", "test zset val4");
        System.out.println("除了以上常用api之外，还有更多api，在Redis类中都有列出，请参考Redis类，或直接参照Jedis的官方文档");
    }
}

```