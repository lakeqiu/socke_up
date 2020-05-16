package com.lakeqiu.socke_up.Three;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * 服务提供者 即服务器
 * @author lakeqiu
 */
public class UDPProvider2 {
    public static void main(String[] args) throws IOException {
        Provider provider = new Provider();
        provider.start();

        // 控制台上有输入，就关闭服务
        System.in.read();
        provider.exit();
    }

    private static class Provider extends Thread {
        private final String sn;
        private DatagramSocket ds;
        // 是否结束程序
        private volatile boolean done = false;

        private Provider() {
            this.sn = UUID.randomUUID().toString();
        }

        @Override
        public void run() {
            System.out.println("服务提供者（服务器）启动");

            try {
                // 绑定监听端口
                ds = new DatagramSocket(8091);
                while (!done) {
                    // 构建接收实体
                    byte[] buf = new byte[1024];
                    DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

                    // 接收信息
                    ds.receive(receivePack);
                    System.out.println("接收到了信息,为");

                    // 解析消息
                    String ip = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();
                    String data = new String(receivePack.getData(), 0, dataLen);
                    System.out.println("搜索者信息 -> ip:" + ip
                            + "\tport:" + port + "\tdata:" + data);

                    // 解析搜索者端口号
                    int returnPort = MsgCreator.parsePort(data);
                    // 解析出端口号，进行回复
                    if (returnPort != -1) {
                        String returnMsg = MsgCreator.buildWithSn(sn);
                        byte[] returnMsgBytes = returnMsg.getBytes();
                        DatagramPacket returnPack = new DatagramPacket(returnMsgBytes,
                                returnMsgBytes.length, receivePack.getAddress(), returnPort);
                        // 回复
                        ds.send(returnPack);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }

            System.out.println("服务器提供者关闭");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        private void exit() {
            done = true;
            close();
        }
    }
}
