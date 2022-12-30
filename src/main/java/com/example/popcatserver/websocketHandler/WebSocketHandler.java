package com.example.popcatserver.websocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.example.popcatserver.service.PopCatServiceImpl;
import org.json.simple.JSONArray;
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

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String id = session.getId();  //메시지를 보낸 아이디
        JSONObject forSend = new JSONObject();
        if(message.getPayload().equals("top")) {
            int count = Integer.parseInt(message.getPayload());
            sendMessage( popCatService.addData(count, id),id);
        }else{
            sendMessage(popCatService.getTop10(),id);
       }
    }

    private void sendMessage(JSONObject forSend, String id){
        CLIENTS.entrySet().forEach( arg->{
            if(arg.getKey().equals(id)) {  //같은 아이디로 응답합니다
                try {
                    arg.getValue().sendMessage(new TextMessage(forSend.toJSONString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});
    }
    private void sendMessage(JSONArray forSend, String id){
        CLIENTS.entrySet().forEach( arg->{
            if(arg.getKey().equals(id)) {  //같은 아이디로 응답합니다
                try {
                    arg.getValue().sendMessage(new TextMessage(forSend.toJSONString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});
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