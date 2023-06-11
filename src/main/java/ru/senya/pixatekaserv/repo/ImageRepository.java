package ru.senya.pixatekaserv.repo;

import org.springframework.data.repository.CrudRepository;
import ru.senya.pixatekaserv.models.Image;

public interface ImageRepository extends CrudRepository<Image, Long> {
}
