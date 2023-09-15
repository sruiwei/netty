package io.netty.example.study.httpclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author shangruiwei
 * @date 2023/9/11 21:22
 */
public class HttpClientWriteService {

    public ByteBuf write() {
        // 只是演示，所以写死了请求信息
        String content = "POST /api/nav/find HTTP/1.1\n" +
                "Accept: application/json, text/plain, */*\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Accept-Language: zh-CN,zh;q=0.9\n" +
                "Content-Length: 33\n" +
                "Content-Type: application/json;charset=UTF-8\n" +
                "Cookie: **注释：cookie是敏感信息，测试时，用自己的**\n" +
                "Host: devops-portal.iguming.net\n" +
                "Origin: http://devops-portal.iguming.net\n" +
                "Proxy-Connection: keep-alive\n" +
                "Referer: http://devops-portal.iguming.net/\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36\n" +
                "pg_header: 65\n" +
                "\n" +
                "{\"id\":\"64c1c8edc94f01de68fd69b1\"}";

        // 需要申请ByteBuf
        byte[] bytes = content.getBytes();
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);

        return buf;
    }
}
