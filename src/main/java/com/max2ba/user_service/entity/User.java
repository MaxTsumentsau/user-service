package com.max2ba.user_service.entity;

import com.max2ba.user_service.listener.UserCrudListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(UserCrudListener.class)
@Table(name = "users")
public class User {
     @Id
     @GeneratedValue
     private UUID id;

     @Column(nullable = false)
     private String name;

     @Column(nullable = false, unique = true, length = 150)
     private String email;

     private Integer age;

     @Column(name = "created_at", updatable = false, nullable = false)
     @CreationTimestamp
     private LocalDateTime createdAt;
}

