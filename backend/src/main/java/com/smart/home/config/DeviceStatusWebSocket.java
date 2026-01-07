package com.smart.home.config;

import com.alibaba.fastjson.JSON;
import com.smart.home.model.dto.DeviceDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 设备状态WebSocket端点
 * 用于向前端推送设备状态更新
 *
 * @author lingma
 */
@ServerEndpoint("/ws")
@Component
public class DeviceStatusWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusWebSocket.class);

    // 静态变量，用来记录当前在线连接数
    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    // concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象
    private static CopyOnWriteArraySet<DeviceStatusWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this); // 加入set中
        int count = onlineCount.incrementAndGet(); // 在线数加1
        logger.info("有连接加入，当前连接数为：" + count);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); // 从set中删除
        int count = onlineCount.decrementAndGet(); // 在线数减1
        logger.info("有连接关闭，当前连接数为：" + count);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("来自客户端的消息：" + message);
        // 群发消息
        broadcast(message);
    }

    /**
     * 发生错误时调用
     *
     * @param session 会话
     * @param error   错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误", error);
    }

    /**
     * 群发消息
     *
     * @param message 消息内容
     */
    public static void broadcast(String message) {
        for (DeviceStatusWebSocket item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    /**
     * 发送消息
     *
     * @param message 消息内容
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.error("发送消息失败", e);
        }
    }

    /**
     * 发送设备状态更新消息
     *
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param status 状态数据
     */
    public static void sendDeviceStatusUpdate(String deviceId, String deviceType, Object status) {
        DeviceStatusUpdateMessage updateMessage = new DeviceStatusUpdateMessage();
        updateMessage.setType("device_status_update");
        
        DeviceStatusUpdatePayload payload = new DeviceStatusUpdatePayload();
        payload.setDeviceId(deviceId);
        payload.setDeviceType(deviceType);
        payload.setStatus(status);
        payload.setTimestamp(System.currentTimeMillis());
        
        updateMessage.setPayload(payload);
        
        String message = JSON.toJSONString(updateMessage);
        broadcast(message);
    }

    /**
     * 设备状态更新消息结构
     */
    public static class DeviceStatusUpdateMessage {
        private String type;
        private DeviceStatusUpdatePayload payload;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public DeviceStatusUpdatePayload getPayload() {
            return payload;
        }

        public void setPayload(DeviceStatusUpdatePayload payload) {
            this.payload = payload;
        }
    }

    /**
     * 设备状态更新消息载荷
     */
    public static class DeviceStatusUpdatePayload {
        private String deviceId;
        private String deviceType;
        private Object status;
        private Long timestamp;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}