package com.example.popcatserver.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface PopCatService {
    public JSONObject getMyRanking(String id);
    public JSONArray getTop10();

    public JSONObject addData(int count, String id);

}