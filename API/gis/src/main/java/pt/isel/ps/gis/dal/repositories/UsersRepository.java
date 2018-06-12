package pt.isel.ps.gis.dal.repositories;

import org.springframework.data.repository.CrudRepository;
import pt.isel.ps.gis.model.Users;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<Users, Long>, UsersRepositoryCustom {

    boolean existsByUsersUsername(String username);

    Optional<Users> findByUsersUsername(String username);
}
