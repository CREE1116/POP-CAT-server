package com.example.popcatserver.service;

import com.example.popcatserver.dto.UserDto;
import com.example.popcatserver.jpa.UserEntity;
import com.example.popcatserver.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
@Service
@Slf4j

public class PopCatServiceImpl implements PopCatService{
    UserRepository userRepository;
    @Autowired
    public PopCatServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public JSONObject getMyRanking(String id) {
        Iterable<UserEntity> All = userRepository.findAllByOrderByCountDesc();

        UserEntity my = userRepository.findBySessionId(id);
        int i =0;
        for(UserEntity userEntity:All ){
            if(userEntity.getCount() <= my.getCount()){
                HashMap<String,String> member= new HashMap<>();
                member.put("ranking",String.valueOf(i+1));
                return new JSONObject(member);
            }
            i++;
        }
        return null;
    }

    @Override
    public JSONArray getTop10() {
        Iterable<UserEntity> Top10 = userRepository.findTop10ByOrderByCountDesc();
        JSONArray jsonArray = new JSONArray();
        HashMap<String,String> member= new HashMap<>();
        for(UserEntity userEntity:Top10){
            member.put("id",userEntity.getSessionId());
            member.put("count",String.valueOf(userEntity.getCount()));
            JSONObject temp = new JSONObject(member);
            jsonArray.add(temp);
        }
        return jsonArray;
    }

    @Override
    public JSONObject addData(int count, String id) {
        UserEntity userEntity = userRepository.findBySessionId(id);
        if(null ==userEntity){
            UserDto userDto = new UserDto();
            userDto.setSessionId(id);
            userEntity = new ModelMapper().map(userDto,UserEntity.class);
        }
        userEntity.setCount(count);
        userRepository.save(userEntity);
        return getMyRanking(id);

    }

}
