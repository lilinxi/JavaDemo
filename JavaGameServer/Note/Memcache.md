# 安装及配置

/**
 * 安装 Homebrew：https://brew.sh/index_zh-cn.html
 * 安装 Memcache
 * brew search memcache
 * 安装服务器：brew install memcached
 * 启动 ：/usr/local/Cellar/memcached/1.5.12/bin/memcached -d -m 1024 -l 127.0.0.1 -p 11211
 * <p>
 * 启动Memcache 常用参数
 * -p 设置TCP端口号(默认设置为: 11211)
 * -U UDP监听端口(默认: 11211, 0 时关闭)
 * -l 绑定地址(默认:所有都允许,无论内外网或者本机更换IP，有安全隐患，若设置为127.0.0.1就只能本机访问)
 * -c max simultaneous connections (default: 1024)
 * -d 以daemon方式运行
 * -u 绑定使用指定用于运行进程
 * -m 允许最大内存用量，单位M (默认: 64 MB)
 * -P 将PID写入文件，这样可以使得后边进行快速进程终止, 需要与-d 一起使用
 * <p>
 * 连接：
 * <p>
 * brew install telnet
 * telnet 127.0.0.1 11211
 *
 */
 
# 特点
 
1. Memory，内存存储。
2. 集中式 Cache，多个 Memcache 作为一个虚拟的 Cluster。
3. 分布式扩展，将一台服务器上的多个 Memcache 服务端部署到多个服务器上。
4. Socket 通信，尽量保存较小内容，且采用字符串传输，省去序列化的操作。
5. 内存分配机制，支持的最大存储对象为 1MB。
6. Cache 机制，没有同步、消息分发或两阶段提交，Key 命中则返回，不命中则需要到数据源中去读取，容许失效时间。
 
# 使用场景
 
1. 小对象缓存：用户 Token，权限，session，资源信息等。
2. 小的静态资源的缓存，如网站的首页。
3. 数据库 SQL 结果集的缓存，对于数据库的负载有很大的减缓作用。
 
# 使用示例
 
**创建一个 MemCachedClient 对象的同时创建一个同名的 SockIOPool 对象。**

## Memcache 工具类封装

```
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import java.util.Arrays;

/**
 * 安装 Homebrew：https://brew.sh/index_zh-cn.html
 * 安装 Memcache
 * brew search memcache
 * 安装服务器：brew install memcached
 * 启动 ：/usr/local/Cellar/memcached/1.5.12/bin/memcached -d -m 1024 -l 127.0.0.1 -p 11211
 * <p>
 * 启动Memcache 常用参数
 * -p 设置TCP端口号(默认设置为: 11211)
 * -U UDP监听端口(默认: 11211, 0 时关闭)
 * -l 绑定地址(默认:所有都允许,无论内外网或者本机更换IP，有安全隐患，若设置为127.0.0.1就只能本机访问)
 * -c max simultaneous connections (default: 1024)
 * -d 以daemon方式运行
 * -u 绑定使用指定用于运行进程
 * -m 允许最大内存用量，单位M (默认: 64 MB)
 * -P 将PID写入文件，这样可以使得后边进行快速进程终止, 需要与-d 一起使用
 * <p>
 * 连接：
 * <p>
 * brew install telnet
 * telnet 127.0.0.1 11211
 *
 * jar
 */
public class MemcachedCRUD {
    public static String poolName = "DBPool";
    protected static MemCachedClient memCachedClient;
    protected static MemcachedCRUD memcachedCRUD = new MemcachedCRUD();
    public static SockIOPool sockIoPool;

    static {
        sockIoPool = init(poolName, "cacheServer");
        memCachedClient = new MemCachedClient(poolName);
        // if true, then store all primitives as their string value.
        memCachedClient.setPrimitiveAsString(true);
    }

    public static SockIOPool init(String poolName, String confKey) {
        // 缓存服务器
        String server[] = {"127.0.0.1:11211"};
        // 创建一个连接池
        SockIOPool pool = SockIOPool.getInstance(poolName);
        System.out.println("连接池" + poolName + "缓存配置" + Arrays.toString(server));
        pool.setServers(server);// 缓存服务器
        pool.setInitConn(50); // 初始化链接数
        pool.setMinConn(50); // 最小链接数
        pool.setMaxConn(500); // 最大连接数
        pool.setMaxIdle(1000 * 60 * 60);// 最大处理时间
        pool.setMaintSleep(3000);// 设置主线程睡眠时,每3秒苏醒一次，维持连接池大小
        pool.setNagle(false);// 关闭套接字缓存
        pool.setSocketTO(3000);// 链接建立后超时时间
        pool.setSocketConnectTO(0);// 链接建立时的超时时间
        pool.initialize();
        return pool;
    }

    public static void destroy() {
        sockIoPool.shutDown();
    }

    MemcachedCRUD() {
    }

    public static MemcachedCRUD getInstance() {
        return memcachedCRUD;
    }

    private static final long INTERVAL = 100;

    public boolean add(String key, Object o) {
        return memCachedClient.add(key, o);
    }

    public boolean update(String key, Object o) {
        return memCachedClient.replace(key, o);
    }

    public boolean saveObject(String key, Object msg) {
        boolean o = memCachedClient.keyExists(key);
        if (o) {// 存在替换掉
            return memCachedClient.replace(key, msg);
        } else {
            return memCachedClient.add(key, msg);
        }
    }

    public boolean keyExist(String key) {
        return memCachedClient.keyExists(key);
    }

    /**
     * delete
     *
     * @param key
     */
    public boolean deleteObject(String key) {
        return memCachedClient.delete(key);
    }

    public Object getObject(String key) {
        Object obj = memCachedClient.get(key);
        return obj;
    }

    public static MemCachedClient getMemCachedClient() {
        return memCachedClient;
    }
}
```

## Memcache 工具类调用

```
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemcacheClientDemo {
    public static void main(String[] args) {
        MemcacheClientDemo demo = new MemcacheClientDemo();
        demo.run();
    }

    public void run() {
        /** memcache add **/
        MemcachedCRUD memcache = MemcachedCRUD.getInstance();
        System.out.println("===========memcache add==============");
        memcache.add("testKey1", 1);
        memcache.add("testKey2", "test value");
        List<String> list = new ArrayList<String>();
        list.add("string1");
        list.add("string2");
        list.add("string3");
        list.add("string4");
        memcache.add("testKey3", list);
        Map<Long, List<String>> map = new HashMap<Long, List<String>>();
        map.put(1l, list);
        map.put(2l, null);
        map.put(3l, list);
        memcache.add("testKey4", map);
        /** memcache get **/
        System.out.println("===========memcache get==============");
        int val1 = Integer.parseInt((String) memcache.getObject("testKey1"));
        System.out.println("val1:" + val1);
        String val2 = (String) memcache.getObject("testKey2");
        System.out.println("val2:" + val2);
        List<String> val3 = (List<String>) memcache.getObject("testKey3");
        System.out.print("val3:");
        for (String string : val3) {
            System.out.print(string + ",");
        }
        System.out.println();
        Map<Long, List<String>> val4 = (Map<Long, List<String>>) memcache.getObject("testKey4");
        System.out.print("val4:");
        for (Long key : val4.keySet()) {
            System.out.print("key:" + key + ",value:" + val4.get(key));
        }
        System.out.println();
        /** memcache update **/
        System.out.println("===========memcache update==============");
        memcache.saveObject("testKey2", "val after update");
        String updateVal = (String) memcache.getObject("testKey2");
        System.out.println("val2 after update:" + updateVal);
        /** memcache key Exist **/
        System.out.println("===========memcache key Exist==============");
        boolean flag = memcache.keyExist("testKey2");
        System.out.println("is testKey2 exist:" + flag);
        /** memcache delete **/
        System.out.println("===========memcache delete==============");
        memcache.deleteObject("testKey3");
        boolean delFlag = memcache.keyExist("testKey3");
        System.out.println("is testKey3 exist after delete:" + delFlag);
    }
}

```