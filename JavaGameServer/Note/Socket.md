# TCP 编程

- 传输单位为字节
- 客户端：socket = new Socket(server, servPort);
- 服务器：
    - serverSocket = new ServerSocket(serverPort);
    - Socket clientSocket = serverSocket.accept();
- **TCPSocketServer：**

```

```

- **TCPSocketClient：**


```

```

# UDP 编程

- 传输单位为 packet
- 首先创建 DatagramSocket 对象，再创建 DatagramPacket 对象用于接收或者发送
- **UDPSocketServer：**

```

```

- **UDPSocketClient：**

```

```