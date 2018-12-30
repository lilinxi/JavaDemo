# 使用 HttpURLConnection 获取应用层数据的状态码和内容：

```

```

# 使用 Tomcat 的 HttpServlet 搭建服务器并使用 Get 和 Post 方法

When using Tomcat：

if Cannot find ./catalina.sh The file is absent or does not have execute permission
    cmd chmod +x *.sh in bin first

## 搭建 Servlet

继承自 HttpServlet 类即可，项目中导入 Tomcat 并设置 Deployment 为 "/"。

pic

Tomcat 7 中可以使用@WebServlet 配置 Servlet，更早版本需要在 web.xml 中完成以下配置：

```

```

```

```

## 使用 HttpURLConnection 发送 Get 和 Post 请求

### Get 请求直接将参数写入 URL 中

```

```

### Post 请求需要将参数写入输出流

doInput 默认为 true，doOutput 默认为 false，Post 实现需要设置 doOutput 为 true。

```

```