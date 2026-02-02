package entity;

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
@Table(name = "users")
public class User {
     @Id
     @GeneratedValue
     private UUID id;

     private String name;

     @Column(nullable = false, unique = true, length = 150)
     private String email;

     private Integer age;

     @Column(name = "created_at", updatable = false)
     @CreationTimestamp
     private LocalDateTime createdAt;
}

