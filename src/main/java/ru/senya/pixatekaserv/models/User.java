package ru.senya.pixatekaserv.models;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString()
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String username, about, avatar, background, password;

}
