package com.example.popcatserver.jpa;

import org.apache.catalina.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<UserEntity,Long> {
     Iterable<UserEntity> findAllByOrderByCountDesc();
     Iterable<UserEntity> findTop10ByOrderByCountDesc();
     UserEntity findBySessionId(String id);



}
