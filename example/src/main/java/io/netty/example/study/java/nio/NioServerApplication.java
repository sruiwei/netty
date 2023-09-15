package io.netty.example.study.java.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author shangruiwei
 * @date 2023/9/09 10:17
 */
public class NioServerApplication {

    private static final int PORT = 8082;

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            // 绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

            // selector
            Selector selector = Selector.open();
            // 将serverSocketChannel注册到selector中
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                // 等待client连接
                selector.select();

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    handleInput(selectionKey, selector);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleInput(SelectionKey key, Selector selector) throws Exception {

        if (key.isValid()) {
            // 处理新接入的请求消息
            if (key.isAcceptable()) {
                // 接受一个新的client的连接
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                // 接受新连接后是一个SocketChannel，是使用SocketChannel和客户端进行通信的
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                // 将SocketChannel注册到selector中
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                // 表示可读了
                SocketChannel socketChannel = (SocketChannel) key.channel();
                // 注释：使用多大的ByteBuffer的承接数据，是个非常难确定的事情
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("NIO server read: " + body);

                    // 向客户端发送消息
                    byte[] sendBytes = String.format("server send time:%s content:%s", new Date(), "hello client").getBytes();
                    ByteBuffer writeBuffer = ByteBuffer.allocate(sendBytes.length);
                    writeBuffer.put(sendBytes);
                    writeBuffer.flip();
                    socketChannel.write(writeBuffer);
                } else if (readBytes < 0) {
                    // 对端链路关闭
                    key.cancel();
                    socketChannel.close();
                } else {
                    System.out.println("client 发送了0个字节");
                }
            }
        }
    }
}
