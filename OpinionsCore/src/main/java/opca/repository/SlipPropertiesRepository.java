package opca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import opca.model.SlipProperties;

public interface SlipPropertiesRepository extends JpaRepository<SlipProperties, Integer>{
	@Query(nativeQuery = true)
	List<SlipProperties> findAll();
			
}
