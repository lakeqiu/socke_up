package com.lakeqiu.socke_up.Three;

import java.io.IOException;
import java.net.*;

/**
 * UDP 提供者，用于提供服务
 * @author lakeqiu
 */
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider启动了");

        // 1、作为搜索方（发送方），不需要绑定监听端口，
        // 发送端口由系统分配即可
        DatagramSocket ds = new DatagramSocket();

        // 2、构建发送信息
        String sendMsg = "Hello World";
        byte[] sendMsgBytes = sendMsg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendMsgBytes, sendMsgBytes.length);
        // 指定接收地址与端口（本机8090）
        sendPacket.setAddress(InetAddress.getLocalHost());
        sendPacket.setPort(8090);

        // 3、发送
        ds.send(sendPacket);

        // 4、接收回复信息
        // 构建接收实体
        byte[] buf = new byte[1024];
        DatagramPacket receiverPack = new DatagramPacket(buf, buf.length);
        // 接收
        ds.receive(receiverPack);

        // 5、解析并打印
        String ip = receiverPack.getAddress().getHostAddress();
        int port = receiverPack.getPort();
        int dataLen = receiverPack.getLength();
        String data = new String(receiverPack.getData(), 0, dataLen);
        System.out.println("UDPService ip:" + port + " port:" + port
                + " dataLen:" + dataLen + "\tdata:" + data);


        System.out.println("UDPProvider结束了");
        ds.close();
    }
}
