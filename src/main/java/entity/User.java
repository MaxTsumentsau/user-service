package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;

     private String name;

     @Column(nullable = false, unique = true, length = 150)
     private String email;

     private Integer age;

     @Column(name = "created_at", updatable = false)
     @CreationTimestamp
     private LocalDateTime createdAt;
}

