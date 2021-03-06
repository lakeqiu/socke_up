package com.lakeqiu.socke_up.Three;

import java.io.IOException;
import java.net.*;

/**
 * UDP 提供者，用于提供服务
 * @author lakeqiu
 */
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider 启动");

        // 1、作为接收者，指定一个端口作为用于数据接收
        DatagramSocket ds = new DatagramSocket(8090);

        // 2、构建接收实体
        byte[] buf = new byte[1024];
        DatagramPacket receiverPack = new DatagramPacket(buf, buf.length);

        // 3、监听端口，接收数据
        ds.receive(receiverPack);
        System.out.println("开始接收搜索者信息");

        // 4、接收到数据了，获取数据信息
        // 发送者的ip
        String ip = receiverPack.getAddress().getHostAddress();
        // 发送者端口
        int port = receiverPack.getPort();
        // 接收到的数据长度
        int dataLen = receiverPack.getLength();
        // 接收到的数据
        String data = new String(receiverPack.getData(), 0, dataLen);
        // 打印信息
        System.out.println("UDPSearcher ip:" + ip + " port:" + port
                + " dataLen:" + dataLen + "\tdata:" + data);


        // 5、回复搜索者信息
        // 构建回复信息
        String returnMsg = "接收到的数据长度为：" + dataLen;
        // 构建容器并放入信息
        byte[] returnMsgBytes = returnMsg.getBytes();
        // 根据搜索者信息构建回复实体
        DatagramPacket returnPack = new DatagramPacket(returnMsgBytes, returnMsgBytes.length,
                receiverPack.getAddress(), receiverPack.getPort());
        // 回复
        ds.send(receiverPack);

        System.out.println("回复数据成功");
        // 关闭资源
        ds.close();
    }
}
