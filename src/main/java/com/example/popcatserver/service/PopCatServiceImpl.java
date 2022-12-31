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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j

public class PopCatServiceImpl implements PopCatService{
    UserRepository userRepository;
    Iterable<UserEntity> Top10;
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
                member.put("type","ranking");
                member.put("data",String.valueOf(i+1));
                return new JSONObject(member);
            }
            i++;
        }
        return null;
    }
    public boolean isSameIterable(Iterable<UserEntity> prev, Iterable<UserEntity> current){
        if(prev == null && current== null) return true;
        if(prev == null || current == null) return false;
        List<UserEntity> prevList = new ArrayList<UserEntity>();
        List<UserEntity> currentList = new ArrayList<UserEntity>();
        prev.forEach(prevList::add);
        current.forEach(currentList::add);

        for(int i = 0; i<currentList.size();i++){
            if(!prevList.get(i).getSessionId().equals(currentList.get(i).getSessionId())){
                System.out.println("user is change");
                return false;
            }
            if(!prevList.get(i).getCount().equals(currentList.get(i).getCount())){
                System.out.println("count is change");
                return false;

            }
        }
        return true;
    }
    private void printIterable(Iterable<UserEntity> temp){
        int i = 1;
        for(UserEntity userEntity : temp){
            System.out.println(i+"| count: "+userEntity.getCount()+"  id:"+userEntity.getSessionId());
        i++;
        }
    }
    public JSONObject returnTop10( Iterable<UserEntity> current){
        JSONObject forSend = new JSONObject();
        List<JSONObject> list  = new ArrayList<>();
        forSend.put("type", "top10");
        int i = 1;
        for (UserEntity userEntity : current) {
            JSONObject temp = new JSONObject();
            temp.put("ranking", String.valueOf(i));
            temp.put("id", userEntity.getSessionId());
            temp.put("count", userEntity.getCount());
            list.add(temp);
            i++;
        }
        forSend.put("data",list);
        return forSend;
    }
    public JSONObject returnTop10(){
        Iterable<UserEntity> current = userRepository.findTop10ByOrderByCountDesc();
        return returnTop10(current);
    }
    public JSONObject getTop10(){
        Iterable<UserEntity> current = userRepository.findTop10ByOrderByCountDesc();
        System.out.println("in get10 -->");
        printIterable(current);
        if(current == null) return new JSONObject();
        if(Top10 == null)Top10 = current;
        if(!isSameIterable(Top10,current)){
            Top10 = current;
            return returnTop10(current);
        }
        return null;
    }

    @Override
    public JSONObject addData(int count, String id) {
        if(id==null || id.length()<1) return null;
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
