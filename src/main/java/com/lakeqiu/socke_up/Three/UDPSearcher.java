package com.lakeqiu.socke_up.Three;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * UDP 搜索者，用于搜索服务支持方，即先发送后接收
 * @author lakeqiu
 */
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher启动了");

        // 1、作为搜索方，不需要绑定监听端口，
        // 发送端口由系统分配即可
        DatagramSocket ds = new DatagramSocket();

        // 2、构建发送信息
        String sendMsg = "Hello World";
        byte[] sendMsgBytes = sendMsg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendMsgBytes, sendMsgBytes.length);
        // 指定接收地址与端口（本机8090）
        sendPacket.setAddress(InetAddress.getLocalHost());
        System.out.println("向提供者发送了：" + sendMsg);
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
        System.out.println("UDPSearcher ip:" + ip + " port:" + port
                + " dataLen:" + dataLen + "\tdata:" + data);


        System.out.println("UDPSearcher结束了");
        ds.close();
    }
}
