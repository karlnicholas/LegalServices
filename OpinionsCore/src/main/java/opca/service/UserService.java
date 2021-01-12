package opca.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Service;

import opca.model.Role;
import opca.model.User;
import opca.repository.RoleRepository;
import opca.repository.UserRepository;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

//@Stateless
@Service
public class UserService {
    private final RoleSingletonBean roleBean;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

	public UserService(RoleSingletonBean roleBean, UserRepository userRepository, RoleRepository roleRepository) {
		super();
		this.roleBean = roleBean;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	/**
     * Register new users. Encodes the password and adds the "USER" role to the user's roles.
     * Returns null if user already exists.
     * @param user new user.
     * @return user with role added and password encoded, unless user already exists, then null.
     * @throws NoSuchAlgorithmException if SHA-256 not available. 
     */
    @PermitAll
    public User encodeAndSave(User user) throws NoSuchAlgorithmException {
        // sanity check to see if user already exists.
//        TypedQuery<Long> q = userRepository.createNamedQuery(User.COUNT_EMAIL, Long.class).setParameter("email", user.getEmail());
//        if ( q.getSingleResult().longValue() > 0L ) {
//            // show error condition
//            return null;
//        }
    	Long existingUser = userRepository.countByEmail(user.getEmail());
		if ( existingUser > 0L ) {
			// show error condition
			return null;
		}
        // Encode password
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(user.getPassword().getBytes());
        user.setPassword( DatatypeConverter.printBase64Binary(hash) );
        // Add role "USER" to user.
        Role role = roleBean.getUserRole();
        List<Role> roles = new ArrayList<Role>();
        roles.add(roleRepository.save(role));
        user.setRoles(roles);
        // Persist user.
        userRepository.saveAndFlush(user);
        return user;
    }
    
    /**
     * Return the number of registered Users
     * @return number of registered Users
     */
    @PermitAll
    public Long userCount() {
        return userRepository.count();
    }

    /**
     * Update the User's password
     * @param user to update.
     * @return Updated User
     * @throws NoSuchAlgorithmException if SHA-256 not available.
     */
    @RolesAllowed({"USER"})
    public User updatePassword(User user) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(user.getPassword().getBytes());
        user.setPassword( DatatypeConverter.printBase64Binary(hash) ); 
        return userRepository.save(user);
    }

    /**
     * Merge user with Database
     * @param user to merge.
     * @return Merged User
     */
    @PermitAll
    public User merge(User user) {
        return userRepository.save(user);
    }
    
    /**
     * return User for email.
     * @param email to search for.
     * @return User found, else runtime exception.
     */
    @PermitAll
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * return User for email.
     * @param email to search for.
     * @param verifyKey for user
     * @return User found, else runtime exception.
     */
    @PermitAll
    public User verifyUser(String email, String verifyKey) {
//        List<User> users = em.createNamedQuery(User.FIND_BY_EMAIL, User.class)
//            .setParameter("email", email)
//            .getResultList();
    	User user = userRepository.findByEmail(email); 
        if ( user != null && user.getVerifyKey().equals(verifyKey)) {
        	user.setVerified(true);
        	user.setStartVerify(false);
        	user.setVerifyErrors(0);
        	user.setVerifyCount(0);
        }
        return user;
    }

    @PermitAll
    public User checkUserByEmail(String email) {
    	return userRepository.findByEmail(email);
    }
    /**
     * Delete User by Database Id
     * @param id to delete.
     */
//    @RolesAllowed({"ADMIN"})
    @PermitAll // because welcoming service uses it. 
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Remove verification flag for user id.
     * @param id to find.
     */
    @RolesAllowed({"ADMIN"})    
	public void unverify(Long id) {
        User user = userRepository.getOne(id);
        user.setVerified(false);
	}
    /**
     * Find User by Database Id
     * @param id to find.
     * @return User or null if not exists
     */
    @RolesAllowed({"ADMIN"})
    public User findById(Long id) {
        return userRepository.getOne(id);
    }
    
    /**
     * Get List of all Users
     * @return List of all Users
     */
    // @RolesAllowed({"ADMIN"})
    @PermitAll // because court Report service uses it. 
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Promote User by Database Id by adding "ADMIN" role to user.
     * @param id to promote.
     * @return User or null if not exists
     */
    @RolesAllowed({"ADMIN"})
    public User promoteUser(Long id) {
        User user = userRepository.getOne(id);
        user.getRoles().add(roleBean.getAdminRole());
//        return em.merge( user );
        return user;
    }
    
    /**
     * Demote User by Database Id by removing "ADMIN" role from user.
     * @param id of user to demote.
     * @return User or null if not exists
     */
    @RolesAllowed({"ADMIN"})
    public User demoteUser(Long id) {
        User user = userRepository.getOne(id);
        Iterator<Role> rIt = user.getRoles().iterator();
        while ( rIt.hasNext()  ) {
            Role role = rIt.next();
            if ( role.getRole().equals("ADMIN")) rIt.remove();
        }
        return userRepository.save(user);
    }

    /**
     * Manually encode a password
     * @param args not user.
     * @throws Exception if any.
     */
    public static void main(String[] args) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest("karl.nicholas@outlook.com".getBytes());
        System.out.println( DatatypeConverter.printBase64Binary(hash) );
    }

    @PermitAll
	public void incrementVerifyCount(User user) {
		user.setVerifyCount( user.getVerifyCount() + 1);
        userRepository.save(user);
	}
    @PermitAll
	public void incrementVerifyErrors(User user) {
		user.setVerifyErrors( user.getVerifyErrors() + 1);
        userRepository.save(user);
	}
    @PermitAll
	public List<User> findAllUnverified() {
        return userRepository.findUnverified();
	}

    @PermitAll
	public void incrementWelcomeErrors(User user) {
		user.setWelcomeErrors( user.getWelcomeErrors() + 1);
        userRepository.save(user);
	}

    @PermitAll
	public void setWelcomedTrue(User user) {
		user.setWelcomed( true );
        userRepository.save(user);
	}

    @PermitAll
	public void setOptOut(User user) {
		user.setOptout( true );
        userRepository.save(user);
	}

    @PermitAll
	public void clearOptOut(User user) {
		user.setOptout( false );
        userRepository.save(user);
	}

    @PermitAll
    public List<User> findAllUnWelcomed() {
        return userRepository.findUnwelcomed();
	}

}