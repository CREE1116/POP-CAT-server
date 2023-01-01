package com.example.popcatserver.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserDto {
    @Id
    private String sessionId;
    @Column(nullable = false)
    private Integer count;
    @Column(nullable = false)
    private String nickname;
}
