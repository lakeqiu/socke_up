package com.lakeqiu.socke_up.Three;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 服务搜索者，即客户端
 * @author lakeqiu
 */
public class UDPSearcher2 {
    // 默认监听端口
    private static final int LISTEN_PORT = 10000;

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("服务搜索者（客户端）启动");
        // 先启动监听端口，保证能够接收到服务器的消息
        Listener listen = listen();
        // 发送消息
        sendBroadcast();

        // 读取任意键盘信息后可以退出
        System.in.read();
        List<Device> devices = listen.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println(device.toString());
        }

        System.out.println("服务搜索者（客户端）关闭");
    }

    /**
     * 启动监听回送端口的线程
     * @return 监听回送端口的线程
     */
    private static Listener listen() throws InterruptedException {
        System.out.println("服务搜索者回送消息监听线程启动");
        // 用于等待监听线程启动，开始监听
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();

        countDownLatch.await();
        return listener;
    }

    /**
     * 广播消息给服务器
     */
    public static void sendBroadcast() throws IOException {
        System.out.println("服务搜索者（客户端）发送消息");

        // 1、系统自动分配发送端口
        DatagramSocket ds = new DatagramSocket();

        // 2、创建发送实体
        String msg = MsgCreator.buildWithPort(LISTEN_PORT);
        byte[] buf = msg.getBytes();
        DatagramPacket sendPack = new DatagramPacket(buf, buf.length);
        // 地址：进行广播，服务器端口：8091
        sendPack.setAddress(InetAddress.getByName("255.255.255.255"));
        sendPack.setPort(8091);

        // 3、发送
        ds.send(sendPack);

        ds.close();
        System.out.println("发送消息完成");
    }

    /**
     * 负责监听来自服务器的信息,打印出来，并存储服务器信息
     */
    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final ArrayList<Device> devices = new ArrayList<>();
        private volatile boolean done = false;
        private DatagramSocket ds = null;

        private Listener(int listenPort, CountDownLatch countDownLatch) {
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            // 1、通知主线程已经开始监听端口了，可以搜索服务者发送消息了
            countDownLatch.countDown();
            try {
                // 2、监听回送端口
                ds = new DatagramSocket(listenPort);

                while (done) {
                    // 3、构建回收实体
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);

                    // 4、等待接收端口信息
                    ds.receive(packet);

                    // 5、接收到回复了，解析消息
                    String ip = packet.getAddress().getHostAddress();
                    int port = packet.getPort();
                    int dataLen = packet.getLength();
                    String data = new String(packet.getData(), 0, dataLen);
                    System.out.println("服务器信息 -> ip:" + ip
                            + "\tport:" + port + "\tdata:" + data);

                    // 存储服务信息
                    // 解析服务器标识
                    String sn = MsgCreator.parseSn(data);
                    if (sn != null) {
                        devices.add(new Device(port, ip, sn));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }

            System.out.println("搜索者监听线程关闭");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        /**
         * 获取服务器信息并关闭服务搜索者
         * @return 服务器信息
         */
        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }

    /**
     * 存放服务器的信息
     */
    private static class Device {
        private final int port;
        private final String ip;
        private final String sn;

        public Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }
}
