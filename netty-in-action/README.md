# Netty-In-Action
```
@author 鲁伟林
记录《Netty 实战》中各章节学习过程，写下一些自己的思考和总结，帮助使用Netty框架的开发技术人员们，能够有所得，避免踩坑。
本博客目录结构将严格按照书本《Netty 实战》，可能出现跳小章节，省略与Netty无关的内容。
GitHub地址: https://github.com/thinkingfioa/netty-learning/tree/master/netty-in-action
本人博客地址: https://blog.csdn.net/thinking_fioa
```

# 1. Netty - 异步和事件驱动
- 1.Netty 能够帮助搭建允许系统能够扩展到支持150000名并发用户。
- 2.Netty 设计关键: 异步 + 事件驱动

## 1.1 Java网络编程(BIO)
典型的BIO服务端:

- 1.一个主线程在某个port监听，等待客户端连接。
- 2.当接收到客户端发起的连接时，创建一个新的线程去处理客户端请求。
- 3.主线程重新回到port监听，等待下一个客户端连接。

缺点:

- 1.每个新的客户端Socket都需要创建一个新的Thread处理，将会导致大量的线程处于休眠状态。
- 2.每个线程都有调用栈的内存分配，连接数非常多时，耗费较多内存。
- 3.连接数比较多时，创建大量线程，上下文切换所带来的开销较大。

##### 代码:
```java
public void serve(int port) throws IOException {
	// 创建Socket
    ServerSocket serverSocket = new ServerSocket(port);
    // 等待客户端连接
    Socket clientSocket = serverSocket.accept();
    // 创建输入流
    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    String request, response;
    while((request = in.readLine()) != null) {
        if("Done".equals(request)) {
            break;
        }
        response = processRequest(request);
        out.println(response);
    }
}
```

## 1.2 Java NIO
- 1.使用Selector来实现Java的非阻塞I/O操作。将多个Socket的读写状态绑定到Selector上，允许在任何时间检查任意的读操作/写操作的完成状态。
- 2.允许单个线程处理多个并发的连接。

## 1.3 Netty的核心组件
Netty的主要构件块:

- 1.Channel
- 2.回调
- 3.Future
- 4.事件和ChannelHandler

### 1.3.1 Channel
Channel是传入(入站)或者传出(出站)数据的载体(如一个文件、一个Socket或一个硬件设备)。可以被打开或者被关闭，连接或断开连接。

### 1.3.2 回调
回调只是：先写一段代码，该段代码在将来某个适当的时候会被执行。Netty大量使用了回调，比如：某ChannelHandler中的channelActive()方法则是一个回调，表示连接建立时，请执行该段回调代码。

### 1.3.3 Future
异步操作占位符。在操作完成时，提供结果的访问。

##### JDK提供的Future和ChannelFuture对比:
- 1.JDK提供的Future需要手动检查对应的操作是否完成，或一直阻塞直到它完成
- 2.ChannelFuture能够注册Listener监听器，监听器的回调函数operationComplete()能异步的在操作完成时被调用。

##### 代码:
```java
public static void connect() {
    Channel channel = CHANNEL_FROM_SOMEWHERE;

    ChannelFuture future = channel.connect(new InetSocketAddress("127.0.0.1", 9080));
    future.addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if(future.isSuccess()) {
                ByteBuf buf = Unpooled.copiedBuffer("hello", Charset.defaultCharset());
                ChannelFuture wf = future.channel().writeAndFlush(buf);
                // ...
            } else {
            		// 失败后可尝试重连／切换链路
                future.cause().printStackTrace();
            }
        }
    })
}
```

### 1.3.4 事件和ChannelHandler
- 1.事件：发生某种事件触发适当的动作。比如入站触发事件: 链路激活(channelActive)／数据可读(channelRead)/发生异常(exceptionCaught)/...
- 2.Channelhandler：一组为了响应特定事件而被执行的回调函数。如:ChannelInboundHanderAdapter.java是一个入站事件

### 1.3.5 Channel和EventLoop关系:
Channel和EventLoop都是Netty核心概念，而且有一些约定俗成的规定，能帮助编程和理解:

- 1. 单个Channel只会映射到单个EventLoop
- 2. 单个EventLoop可以处理多个Channel(1:n关系)
- 3. 一个EventLoop在其生命周期内只能绑定到一个线程上
- 4. 由于单个Channel在其生命周期中只会有一个I/O线程，所以ChannelPipeline中多个ChannelHandler无需关心同步互斥问题

# 2. 第一款Netty应用程序
- 1.ChannelHandler用于构建应用业务逻辑。往往封装了为响应特定事件而编写的回调函数
- 2.本节主要讲解一个超级简单的Netty应用程序，回显服务: 客户端建立连接后，发送一个或多个消息。服务端收到后，将消息返回。

## 2.3 编写Echo服务器
Netty服务端至少需要两个部分: 一个ChannelHandler + 引导(Bootstrap)

### 2.3.1 ChannelHandler和业务逻辑


























