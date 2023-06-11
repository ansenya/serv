package ru.senya.pixatekaserv.repo;

import org.springframework.data.repository.CrudRepository;
import ru.senya.pixatekaserv.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
