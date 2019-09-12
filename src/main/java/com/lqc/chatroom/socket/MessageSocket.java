package com.lqc.chatroom.socket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * @author liqicong@myhexin.com
 * @date 2019/9/3 11:28
 */
@ServerEndpoint("/message/{username}")
@Component
@Slf4j
public class MessageSocket {
    public static Map<String, MessageSocket> messageSocketMap = new ConcurrentHashMap<>();

    private String username;

    private Session session;

    /**
     * 用户尝试连接websocket
     * @param username 用户名,从websocket路径获取
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session)throws Exception {
        this.username = username;
        this.session = session;
        messageSocketMap.putIfAbsent(username, this);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("login",username+"上线了!");
        List<String> nameList=getUserList();
        jsonObject.put("nameList",nameList);
        broadCastMessage(jsonObject.toJSONString());
        //在线列表添加用户信息
//        JSONObject jsonObject=new JSONObject();


//        throw new Exception();
    }
    private List<String> getUserList(){
        List<String> nameList=new ArrayList<>();
        if(!messageSocketMap.isEmpty()){
            for (MessageSocket messageSocket : messageSocketMap.values()) {
                if(messageSocket.session.isOpen()) {
                    nameList.add(messageSocket.username);
                }
            }
        }
        return nameList;
    }

    @OnMessage
    public void onMessage(String message,Session session)throws IOException{
//        session.close();
//zz

    }
    @OnClose
    public void onClose(Session session){
        //当有用户下线或webSocket出异常
        //从保存的map里面把这个用户的session去掉,并发送全体消息
        if(messageSocketMap.size()!=0){
            messageSocketMap.remove(username);
            broadCastMessage(username+"下线了");
        }
        System.out.println(username+"下线了");
    }
    @OnError
    public void onError(Session session,Throwable error){
        log.info("发生错误:"+error.getMessage());
    }

    private void sendMessage(String message,String targetName){
        try {
            if (messageSocketMap.keySet().contains(targetName)) {
                messageSocketMap.get(targetName).session.getBasicRemote().sendText(message);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void broadCastMessage(String jsonString){
        //全体广播,把多个消息封装成不同的消息类型进行广播
        try {
            if (messageSocketMap.size() != 0) {
                for (MessageSocket messageSocket : messageSocketMap.values()) {
                    if(messageSocket.session.isOpen()) {
                        messageSocket.session.getBasicRemote().sendText(jsonString);
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
//    private void updateNameList(String jsonString){
//        try {
//            if (messageSocketMap.size() != 0) {
//                for (MessageSocket messageSocket : messageSocketMap.values()) {
//                    if(messageSocket.session.isOpen()) {
//                        messageSocket.session.getBasicRemote().sendText(message);
//                    }
//                }
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
}
