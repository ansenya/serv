package ru.senya.pixatekaserv.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString()
@AllArgsConstructor
@Builder
@Table(name = "Images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String name, description, tags, color, path;
}
