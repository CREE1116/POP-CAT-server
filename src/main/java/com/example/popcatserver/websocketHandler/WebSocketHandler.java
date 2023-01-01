package com.example.popcatserver.websocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


import com.example.popcatserver.service.PopCatServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();
    PopCatServiceImpl popCatService;
    @Autowired
    public WebSocketHandler(PopCatServiceImpl popCatService){
        this.popCatService = popCatService;

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        CLIENTS.put(session.getId(), session);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",session.getId());
        jsonObject.put("type","id");
        sendMessage(jsonObject.toJSONString(),session.getId());
        sendMessage(popCatService.returnTop10().toJSONString(),session.getId());


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String id = session.getId();  //메시지를 보낸 아이디
        JSONObject JsonMessage = jsonToObjectParser(message.getPayload());
        System.out.println(message.getPayload());
        JSONObject forSend = new JSONObject();
        switch ( JsonMessage.get("type").toString()){
            case "count":  forSend = popCatService.addData(Integer.parseInt(JsonMessage.get("count").toString()),JsonMessage.get("id").toString());
                break;
            case "nickname": popCatService.addNickName(JsonMessage.get("name").toString(),JsonMessage.get("id").toString());
                break;
            case "login":forSend = popCatService.login(JsonMessage.get("id").toString());
                break;
            default:;
        }
        if(forSend !=null){
            sendMessage(forSend.toString(),id);
        }
        updateTop10();
    }
    private void updateTop10(){
        JSONObject top10 = popCatService.getTop10 ();
        if (top10 != null) {
            sendMessageAll(top10.toJSONString());
        }
    }

    private void sendMessage(String forSend, String id){
        CLIENTS.entrySet().forEach( arg->{
            if(arg.getKey().equals(id)) {  //같은 아이디로 응답합니다
                try {
                    arg.getValue().sendMessage(new TextMessage(forSend));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});
    }
    public void sendMessageAll (String forSend){
        CLIENTS.entrySet().forEach( arg->{
                try {
                    arg.getValue().sendMessage(new TextMessage(forSend));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }


    private static JSONObject jsonToObjectParser(String jsonStr) {
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }
}