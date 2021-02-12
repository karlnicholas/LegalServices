package opca.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import opca.model.User;

@Service
public class UserDao {
	public User findByEmail(String email) {
		// TODO: implement stub method
		return null;
		
	}
	public Long countByEmail(String email) {
		// TODO: implement stub method
		return null;
		
	}
	public List<User> findUnverified() {
		// TODO: implement stub method
		return null;
		
	}
	public List<User> findUnwelcomed() {
		// TODO: implement stub method
		return null;
		
	}
	public Long count() {
		// TODO Auto-generated method stub
		return null;
	}
	public User save(User user) {
		// TODO Auto-generated method stub
		return null;
	}
	public void saveAndFlush(User user) {
		// TODO Auto-generated method stub
		
	}
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
	}
	public User getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
