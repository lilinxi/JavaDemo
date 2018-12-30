**IoFilterChain**：是 Mina 处理流程的扩展点，如果想要增加流程而不影响后续的业务逻辑代码，只需要向 Chain 中添加 IoFilter 即可。

**IoHandler**：Mina 中要实现的业务逻辑都是在 IoHandler 中完成的。IoHandler 是 Mina 处理流程的终点，在最后一个 IoFilter 中调用，所以只要要有一个 IoFilter。并且每个 IoService 都要指定一个 IoHandler。

**IoSession**：每个 IoSession 对应一个客户端与服务端的底层 IO 连接。

- **MinaServer**：

```

```

- **MinaServerHandler**：

```

```

- **MinaClient**：

```

```

- **MinaClientHandler**：

```

```