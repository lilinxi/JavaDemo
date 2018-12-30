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