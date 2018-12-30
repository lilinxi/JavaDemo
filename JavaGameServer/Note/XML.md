# 待测试的 Java 对象

- **RootElement**：

```

```

- **SubElement**:

```

```

# JDK 自带的 Marshaller 和 Unmarshaller

使用 JAXBContext 创建 Marshaller 对象和 Unmarshaller 对象。

- 使用 Marshaller 对象完成 JavaBean 到 XML 文档的转换，会写入空行。
- 使用 Unmarshaller 对象完成 XML 文档到 JavaBean 的转换，空行无影响。

- **Marshaller+Unmarshaller**：

```

```

# DOM （基于 XML 文档树结构的解析）

使用 org.w3c.dom.Document 对象完成 XML 文档的写入和解析。

- 写入 XML 文档：创建 Document 对象，由 Transformer 将 DOMSource 转化为 StreamResult，会写入空行。
- 解析 XML 文档：DocumentBuilderFactory 创建 DocumentBuilder，DocumentBuilder.parse(filename)，会解析空行。

- **DOM**：

```

```

# SAX（基于事件流的解析）

使用 SAXParser 和 DefaultHandler 基于事件流完成对 XML 文档的解析，会解析空行。

- **SAX**：

```

```

# DOM4J

基于 Document 写入 XML 文档，基于 SAX 完成对 XML 文档的解析，不会写入空行，会解析空行。

- **DOM4j**：

```

```

# JDOM

基于 Document 写入 XML 文档，基于 SAX 完成对 XML 文档的解析，**不会写入空行，不会解析空行**。

- **JDOM**

```

```


