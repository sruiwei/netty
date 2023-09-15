package io.netty.example.study.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author shangruiwei
 * @date 2023/9/09 10:17
 */
public class NioClientApplication {

    private static final int PORT = 8082;

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", PORT));

            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);

            while (true) {
                selector.select(1000);

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
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
            // 判断是否连接成功
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(selector, SelectionKey.OP_READ);

                    // 发送hello
                    send(sc);
                } else {
                    System.out.println("连接失败了");
                }
            }
            if (key.isReadable()) {
                // 读取server发送的消息
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("NIO client read: " + body);

                    // 等待3秒后，再向server发送消息
                    send(sc);
                } else if (readBytes < 0) {
                    // 对端链路关闭
                    key.cancel();
                    sc.close();
                } else {
                    System.out.println("server 发送了0个字节");
                }
            }
        }
    }

    private static void send(SocketChannel sc) throws IOException {
        byte[] req = String.format("client send time:%s content:%s", new Date(), "hello server").getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
    }
}
