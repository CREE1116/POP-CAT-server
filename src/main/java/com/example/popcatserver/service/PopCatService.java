package com.example.popcatserver.service;

import com.example.popcatserver.jpa.UserEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface PopCatService {
    public JSONObject getMyRanking(String id);
    public JSONObject returnTop10();

    public JSONObject addData(int count, String id);

    public void addNickName(String name, String id);

    public JSONObject login(String id);
}
