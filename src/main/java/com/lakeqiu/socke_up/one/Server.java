package com.lakeqiu.socke_up.one;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author lakeqiu
 */
public class Server {
    private final static int DEFAULT_PORT = 8091;
    private int port;
    private ServerSocket serverSocket;
    private volatile boolean flag = true;

    public Server() {
    }

    public Server(int port) {
        this.port = port;
    }

    private void start() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);

            System.out.println("服务器已经启动");
            System.out.println("服务器信息：" + serverSocket.getLocalSocketAddress() + " P:" + serverSocket.getLocalPort());

            // 等待客户端连接
            for (;;) {
                Socket accept = serverSocket.accept();
                work(accept);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                System.out.println("服务器关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void work(Socket socket) {
        Runnable runnable = () -> {
            System.out.println("客户端[" + socket.getPort() + "]已经连接");

            // 创建输入输出流
            BufferedReader reader = null;
            BufferedWriter writer = null;

            Boolean flag = true;

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                do {
                    // 读取客户端发送的信息
                    String msg = reader.readLine();

                    if ("quit".equals(msg)) {
                        System.out.println("客户端[" + socket.getPort() + "]结束连接");
                    } else {
                        System.out.println("客户端[" + socket.getPort() + "]发送：" + msg);
                        String returnMsg = "服务器收到：" + msg;

                        // 回复客户端
                        writer.write(returnMsg + "\n");
                        writer.flush();
                    }
                } while (true);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };

        new Thread(runnable).start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
