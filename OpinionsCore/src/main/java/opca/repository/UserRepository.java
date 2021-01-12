package opca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import opca.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);
	Long countByEmail(String email);
	@Query(nativeQuery = true)
	List<User> findUnverified();
	@Query(nativeQuery = true)
	List<User> findUnwelcomed();

}
