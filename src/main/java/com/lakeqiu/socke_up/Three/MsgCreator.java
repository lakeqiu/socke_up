package com.lakeqiu.socke_up.Three;

/**
 * @author lakeqiu
 */
public class MsgCreator {
    private static final String SN_HEADER = "收到信息，我是（SN）:";
    private static final String PORT_HEADER = "这是消息，收到请请回端口（Port）:";

    /**
     * 将端口整合进消息中
     * @param port 端口
     * @return 要发送的消息
     */
    public static String buildWithPort(int port) {
        return SN_HEADER + port;
    }

    /**
     * 解析出接收到的信息中的端口信息
     * @return 端口
     */
    public static int parsePort(String data) {
        if (data.startsWith(PORT_HEADER)) {
            return Integer.parseInt(data.substring(PORT_HEADER.length()));
        }
        return -1;
    }

    /**
     * 将身份标识整合进回复信息中
     * @param sn 身份标识
     * @return 要回复的信息
     */
    public static String buildWithSn(String sn) {
        return SN_HEADER + sn;
    }

    /**
     * 解析接收到的回复信息中的身份标识
     * @param data 接收到的回复信息
     * @return 身份标识
     */
    public static String parseSn(String data) {
        if (data.startsWith(SN_HEADER)) {
            return data.substring(SN_HEADER.length());
        }
        return null;
    }
}
