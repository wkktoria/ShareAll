package io.github.wkktoria.shareall.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
class User {
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String displayName;
    
    private String password;
}
