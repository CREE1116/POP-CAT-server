package com.example.popcatserver.Thread;

import com.example.popcatserver.service.PopCatService;
import com.example.popcatserver.service.PopCatServiceImpl;
import com.example.popcatserver.websocketHandler.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class Top10Thread extends Thread{

    WebSocketHandler webSocketHandler;
    PopCatServiceImpl popCatServiceImpl;
    @Autowired
    public Top10Thread(WebSocketHandler webSocketHandler,PopCatServiceImpl popCatServiceImpl){
        this.webSocketHandler = webSocketHandler;
        this.popCatServiceImpl = popCatServiceImpl;
    }

    public void run (){
        while(true) {
            System.out.println("now running");
            JSONObject top10 = popCatServiceImpl.getTop10();
            System.out.println(top10);
            if (top10 != null) {
                webSocketHandler.sendMessageAll(top10.toJSONString());
                System.out.println("1");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("error");
                e.printStackTrace();
            }
        }
    }

}
