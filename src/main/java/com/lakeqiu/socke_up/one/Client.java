package com.lakeqiu.socke_up.one;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 客户端
 * @author lakeqiu
 */
public class Client {
    private final static int DEFAULT_SERVER_PORT = 8091;
    private final static String DEFAULT_SERVER_HOST = "127.0.0.1";
    private int serverPort;
    private String serveHost;
    private Socket socket;

    public Client() {
        this(DEFAULT_SERVER_PORT, DEFAULT_SERVER_HOST);
    }

    public Client(int serverPort, String serveHost) {
        this.serverPort = serverPort;
        this.serveHost = serveHost;
    }

    private void start() {
        try {
            socket = new Socket(serveHost, serverPort);
            // 这个用来设置与socket的inputStream相关的read操作阻塞的等待时间，超过设置的时间了，
            // 假如还是阻塞状态，会抛出异常java.net.SocketTimeoutException: Read timed out
            // 这里的阻塞不是指read的时间长短，可以理解为没有数据可读，线程一直在这等待
            socket.setSoTimeout(3000);

            // 这个写法等价于 socket = new Socket(serveHost, serverPort);
            /*socket.connect(new InetSocketAddress(serveHost, serverPort), 3000);*/

            System.out.println("已经连接服务器");
            System.out.println("客户端信息为：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
            System.out.println("服务器信息为：" + socket.getInetAddress() + " P:" + socket.getPort());

            work();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("客户端关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void work() throws IOException {
        // 构建键盘输入流
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        // 得到socket输入输出流，并转换为字符输入、输出流
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        // 是否继续通信
        boolean flag = true;

        do {
            // 等到客户端输入信息
            System.out.println("客户端:");
            String msg = consoleReader.readLine();

            if ("quit".equals(msg)) {
                // 退出通信
                flag = false;
            } else { // 发送服务器，等待服务器发送信息，打印
                writer.write(msg + "\n");
                writer.flush();

                // 获取服务器返回信息
                String returnMsg = reader.readLine();
                System.out.println("服务器：" + returnMsg);
            }
        } while (flag);

        // 退出通信，关闭资源
        consoleReader.close();
        reader.close();
        writer.close();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
