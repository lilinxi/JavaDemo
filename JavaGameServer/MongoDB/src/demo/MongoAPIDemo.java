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
