# MongoDB 安装

## 进入 /usr/local
cd /usr/local

## 下载
sudo curl -O https://fastdl.mongodb.org/osx/mongodb-osx-x86_64-3.4.2.tgz

## 解压
sudo tar -zxvf mongodb-osx-x86_64-3.4.2.tgz

## 重命名为 mongodb 目录

sudo mv mongodb-osx-x86_64-3.4.2 mongodb

# MongoDB 设置环境变量

cd ~/

touch .bash_profile

open -e .bash_profile

PATH=$PATH:/usr/local/mongodb/bin
export PTAH

source .bash_profile

# MongoDB 使用

sudo mkdir -p /data/db

启动 mongodb，默认数据库目录即为 /data/db：sudo mongod

# 如果没有创建全局路径 PATH，需要进入以下目录

cd /usr/local/mongodb/bin
sudo ./mongod

打开另一个命令行作为客户端：

$ cd /usr/local/mongodb/bin 
$ ./mongo
MongoDB shell version v3.4.2
connecting to: mongodb://127.0.0.1:27017
MongoDB server version: 3.4.2
Welcome to the MongoDB shell.
……
> 1 + 1
2
> 


# idea下的插件安装及配置

FileSettings…Plugins

Browse repositories…

Mongo Plugin

# 简要介绍

首先创建 MongoClient 和 MongoDatabase，操纵 MongoCollection<Document> 对象插入 Document（Bson）。

**示例代码**：

```
package demo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.net.UnknownHostException;

/**
 * 保存对象API
 */
public class MongoAPIDemo {
    public static void main(String[] args) throws UnknownHostException {
        // 第一：实例化mongo对象，连接mongodb服务器 包含所有的数据库
        // 默认构造方法，默认是连接本机，端口号默认是27017
        // 获取DBOptions
        MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        build.connectionsPerHost(50); // 与目标数据库能够建立的最大connection数量为50
        build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
        build.maxWaitTime(120000);
        build.connectTimeout(60000);
        MongoClientOptions myOptions = build.build();
        // 获取ServerAddress
        ServerAddress addr = new ServerAddress();
        String hosts = "127.0.0.1:27017";
        for (String host : hosts.split("&")) {
            String ip = host.split(":")[0];
            String port = host.split(":")[1];
            addr = new ServerAddress(ip, Integer.parseInt(port));
        }
        // new Mongo实例
        MongoClient mongo = new MongoClient(addr, myOptions);
        // 第二：连接具体的数据库
        // 其中参数是具体数据库的名称，若服务器中不存在，会自动创建
        MongoDatabase db = mongo.getDatabase("war");
        // 第三：操作具体的表
        // 在mongodb中没有表的概念，而是指集合
        // 其中参数是具体集合的名称，若服务器中不存在，会自动创建
        MongoCollection<Document> collection = db.getCollection("collect1");
        // 添加操作
        // 在mongodb中没有行的概念，而是指文档
//        BasicDBObject 不推荐使用
        Document document = new Document("name", "mongo")
                .append("count", 1)
                .append("info", new Document("x", 100).append("y", 200));
        // 然后保存到集合中
        collection.insertOne(document);
        // 第二种：直接把json存到数据库中
        String jsonTest = "{'id':1,'name':'小明',"
                + "'address':{'city':'beijing','code':'065000'}" + "}";
        document = Document.parse(jsonTest);
        collection.insertOne(document);
//        查找操作
        System.out.println("find all");
        FindIterable<Document> documents = collection.find();
        for (Document d : documents) {
            System.out.println(d);
        }
        System.out.println("find condition");
        Document query = new Document("id", 1);
        documents=collection.find(query);
        for (Document d : documents) {
            System.out.println(d);
        }
    }

}

```

# 工具类封装

## Document 和 JavaBean 之间的相互转换，借助 Gson（Json）

```
import com.google.gson.Gson;
import org.bson.Document;

/**
 * Document 和 JavaBean 之间的相互转换，中间借助 Gson 作为两者转换的媒介
 *
 * @author ilala
 */
public class DocumentUtil {
    private static Gson gson = new Gson();
    /**
     * 把实体bean对象转换成Document
     */
    public static <T> Document bean2Document(T bean) {
        if (bean == null) {
            return null;
        }
        // 使用Gson转换javabean为json字符串
        String json = new Gson().toJson(bean);
        // 使用Mongo提供的Json工具类转为Document对象
        return Document.parse(json);
    }

    /**
     * 把Document转换成bean对象
     */
    public static <T> T document2Bean(Document document, Class<T> clz) {
        if (document == null) {
            return null;
        }
        // 使用Gson把Document对象转化为json字符串
        String json = gson.toJson(document);
        // 使用Gson把Json字符串转换为Javabean
        T obj = gson.fromJson(json, clz);
        return obj;
    }

}

```

## Mongo API 封装

```
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoUtil {
    private MongoClient mongo = null;
    private MongoDatabase db = null;
    private static final Map<String, MongoUtil> instances = new ConcurrentHashMap<String, MongoUtil>();

    /**
     * 实例化
     *
     * @return MongoDBManager对象
     */
    static {
        getInstance("db");// 初始化默认的MongoDB数据库
    }

    public static MongoUtil getInstance() {
        return getInstance("db");// 配置文件默认数据库前缀为db
    }

    public static MongoUtil getInstance(String dbName) {
        MongoUtil mongoMgr = instances.get(dbName);
        if (mongoMgr == null) {
            mongoMgr = buildInstance(dbName);
            if (mongoMgr == null) {
                return null;
            }
            instances.put(dbName, mongoMgr);
        }
        return mongoMgr;
    }

    private static synchronized MongoUtil buildInstance(String dbName) {
        MongoUtil mongoMgr = new MongoUtil();
        try {
            mongoMgr.mongo = new MongoClient(getServerAddress(dbName), getDBOptions(dbName));
            mongoMgr.db = mongoMgr.mongo.getDatabase("war");
            System.out.println("connect to MongoDB success!");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mongoMgr;
    }

    /**
     * 获取集合（表）
     *
     * @param collection
     */
    public MongoCollection<Document> getCollection(String collection) {
        return db.getCollection(collection);
    }

    /**
     * 插入
     *
     * @param collection
     * @param o
     */
    public void insert(String collection, Document o) {
        getCollection(collection).insertOne(o);
    }

    /**
     * 批量插入
     *
     * @param collection
     * @param list
     */
    public void insertBatch(String collection, List<Document> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        getCollection(collection).insertMany(list);
    }

    /**
     * 删除
     *
     * @param collection
     * @param query
     */
    public List<Document> delete(String collection, Bson query) {
        List<Document> list = find(collection, query);
        getCollection(collection).deleteOne(query);
        return list;
    }

    /**
     * 批量删除
     *
     * @param collection
     * @param list
     */
    public void deleteBatch(String collection, List<Bson> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            // 批量条件删除
            delete(collection, list.get(i));
        }
    }

    /**
     * 计算集合总条数
     *
     * @param collection
     */
    public long getCount(String collection) {
        long count = getCollection(collection).countDocuments();
        return count;
    }

    /**
     * 计算满足条件条数
     *
     * @param collection
     * @param query
     */
    public long getCount(String collection, Bson query) {
        long count = getCollection(collection).countDocuments(query);
        return count;
    }

    /**
     * 更新
     *
     * @param collection
     * @param query      查询条件
     * @param setFields  更新对象
     * @return List<Document> 更新后的对象列表
     */
    public List<Document> update(String collection, Bson query, Bson setFields) {
        List<Document> list = find(collection, query);
        getCollection(collection).updateMany(query, setFields);
        return list;
    }

    /**
     * @param collection
     * @param query
     * @param setFields
     * @return DBObject
     * @Title: updateOne
     * @Description: 更新一条数据
     */
    public Document updateOne(String collection, Bson query, Bson setFields) {
        Document document = getCollection(collection).findOneAndUpdate(query, setFields);
        return document;
    }

    /**
     * @param collection
     * @param id
     * @param setFields
     * @return DBObject
     * @Title: updateOne
     * @Description: 根据id更新一条数据
     */
    public Document updateOne(String collection, long id, Bson setFields) {
        return updateOne(collection, new Document("id", id), setFields);
    }

    private <T> List<T> toArray(MongoIterable<T> it) {
        if (it != null) {
            List<T> list = new ArrayList<>();
            for (T i : it) {
                list.add(i);
            }
            return list;
        } else {
            return null;
        }
    }

    /**
     * 查找（返回一个List<Document>）
     *
     * @param collection
     * @param query      查询条件
     */
    public List<Document> find(String collection, Bson query) {
        FindIterable<Document> documents = getCollection(collection).find(query);
        return toArray(documents);
    }

    /**
     * 查找集合所有对象
     *
     * @param collection
     */
    public List<Document> findAll(String collection) {
        return find(collection, new Document());
    }

    /**
     * 按顺序查找集合所有对象
     *
     * @param collection 数据集
     * @param orderBy    排序
     */
    public List<Document> findAll(String collection, Bson orderBy) {
        FindIterable<Document> documents = getCollection(collection).find().sort(orderBy);
        return toArray(documents);
    }

    /**
     * distinct操作
     *
     * @param collection 集合
     * @param field      distinct字段名称
     */
    public List<String> distinct(String collection, String field) {
        DistinctIterable ret = getCollection(collection).distinct(field, String.class);
        return toArray(ret);
    }

    /**
     * distinct操作
     *
     * @param collection 集合
     * @param field      distinct字段名称
     * @param query      查询条件
     */
    public List<String> distinct(String collection, String field, Bson query) {
        DistinctIterable ret = getCollection(collection).distinct(field, query, String.class);
        return toArray(ret);
    }

    /**
     * @param collection 集合
     * @param map        map javascript函数字符串，如：function(){ for(var key in this) {
     *                   emit(key,{count:1}) } }
     * @param reduce     reduce Javascript函数字符串，如：function(key,emits){ total=0; for(var
     *                   i in emits){ total+=emits[i].count; } return {count:total}; }
     * @param orderBy    排序
     */
    public List<Document> mapReduce(String collection, String map, String reduce, Bson orderBy) {
        MapReduceIterable ret = getCollection(collection).mapReduce(map, reduce).sort(orderBy);
        return toArray(ret);
    }

    /**
     * @param dbName
     * @return List<ServerAddress>
     * @Title: getServerAddress
     * @Description: 获取数据库服务器列表
     */
    private static List<ServerAddress> getServerAddress(String dbName) {
        List<ServerAddress> list = new ArrayList<>();
        String hosts = "127.0.0.1:27017";
        for (String host : hosts.split("&")) {
            String ip = host.split(":")[0];
            String port = host.split(":")[1];
            list.add(new ServerAddress(ip, Integer.parseInt(port)));
        }
        return list;
    }

    /**
     * @return MongoClientOptions
     * @Title: getDBOptions
     * @Description: 获取数据参数设置
     */
    private static MongoClientOptions getDBOptions(String dbName) {
        MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        build.connectionsPerHost(50); // 与目标数据库能够建立的最大connection数量为50
        build.threadsAllowedToBlockForConnectionMultiplier(50); // 如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
        build.maxWaitTime(120000);
        build.connectTimeout(60000);
        MongoClientOptions myOptions = build.build();
        return myOptions;
    }

    public static void main(String[] args) {
        System.out.println("=================insert==============");
        getInstance().insert(
                "user",
                new Document().append("name", "admin1")
                        .append("type", "13").append("score", 70)
                        .append("level", 2)
                        .append("inputTime", System.currentTimeMillis()));
        getInstance().insert(
                "user",
                new Document().append("name", "admin2")
                        .append("type", "2").append("score", 70)
                        .append("level", 6)
                        .append("inputTime", System.currentTimeMillis()));
        getInstance().insert(
                "user",
                new Document().append("name", "admin3")
                        .append("type", "2").append("score", 70)
                        .append("level", 2)
                        .append("inputTime", System.currentTimeMillis()));
        for (Document d : getInstance().findAll("user")) {
            System.out.println(d);
        }
        System.out.println("=================update==============");
        getInstance().update("user",
                Filters.eq("name", "admin1"),
                new Document("$set", new Document("name", "newAdmin")));
        for (Document d : getInstance().findAll("user")) {
            System.out.println(d);
        }
        System.out.println("=================distinct==============");
        for (String o : getInstance().distinct("user", "name")) {
            System.out.println(o);
        }
        System.out.println("=================delete==============");
        getInstance().delete("user", new Document("name", "admin1"));
        for (Document d : getInstance().findAll("user")) {
            System.out.println(d);
        }
        System.out.println("=================map reduce==============");
//        按照 type 分类，score 求和，level 求平均值，按 level 排序
        List<Document> list = getInstance().mapReduce(
                "user",
                "function(){emit({type:this.type},{type:this.type,score:this.score,level:this.level});}",
                "function(key,values){var result={type:key.type,score:0,level:0};var count=0;values.forEach(function(value){result.score+=value.score;result.level+=value.level;count++});result.level=result.level/count;return result;}",
                new Document("level", 1));
        for (Document d : list) {
            System.out.println(d);
        }
    }
}

```

# 教程链接

https://blog.csdn.net/mrhelicopter/article/category/7327531

http://www.runoob.com/mongodb/mongodb-java.html
