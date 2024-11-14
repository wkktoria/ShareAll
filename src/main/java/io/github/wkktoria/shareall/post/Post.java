package io.github.wkktoria.shareall.post;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Post {
    @Id
    @GeneratedValue
    private long id;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
}
