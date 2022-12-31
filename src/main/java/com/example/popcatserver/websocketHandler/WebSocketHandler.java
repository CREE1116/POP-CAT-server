package com.example.popcatserver.websocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.example.popcatserver.Thread.Top10Thread;
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
        Top10Thread top10Thread = new Top10Thread(this,popCatService);
        if(!top10Thread.isAlive()) {
            top10Thread.start();
        }

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
        System.out.println(message.getPayload());
        JSONObject JsonMessage = jsonToObjectParser(message.getPayload());
        JSONObject forSend = popCatService.addData(Integer.parseInt(JsonMessage.get("count").toString()),JsonMessage.get("id").toString());
        if(forSend !=null){
        sendMessage(forSend.toString(),id);}
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