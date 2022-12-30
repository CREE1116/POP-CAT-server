package com.example.popcatserver.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.io.Serializable;

@Data
@Entity
@Table(name = "User")
public class UserEntity implements Serializable {
    @Id
    private String sessionId;
    @Column(nullable = false)
    private Integer count;

}
