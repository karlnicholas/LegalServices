package opca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import opca.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
