package com.github.karlnicholas.legalservices.user.security.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.model.RoleSave;
import com.github.karlnicholas.legalservices.user.model.UserSave;
import com.github.karlnicholas.legalservices.user.security.dao.RoleDao;
import com.github.karlnicholas.legalservices.user.security.dao.UserDao;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

//@Stateless
@Service
public class UserService {
    private final RoleSingletonBean roleBean;
    private final UserDao userDao;
    private final RoleDao roleDao;

	public UserService(RoleSingletonBean roleBean, UserDao userDao, RoleDao roleDao) {
		super();
		this.roleBean = roleBean;
		this.userDao = userDao;
		this.roleDao = roleDao;
	}

	/**
     * Register new users. Encodes the password and adds the "USER" role to the user's roles.
     * Returns null if user already exists.
     * @param userSave new user.
     * @return user with role added and password encoded, unless user already exists, then null.
     * @throws NoSuchAlgorithmException if SHA-256 not available. 
     */
    @PermitAll
    public UserSave encodeAndSave(UserSave userSave) throws NoSuchAlgorithmException {
//        // sanity check to see if user already exists.
////        TypedQuery<Long> q = userRepository.createNamedQuery(User.COUNT_EMAIL, Long.class).setParameter("email", user.getEmail());
////        if ( q.getSingleResult().longValue() > 0L ) {
////            // show error condition
////            return null;
////        }
//    	Long existingUser = userDao.countByEmail(user.getEmail());
//		if ( existingUser > 0L ) {
//			// show error condition
//			return null;
//		}
//        // Encode password
//        byte[] hash = MessageDigest.getInstance("SHA-256").digest(user.getPassword().getBytes());
//        user.setPassword( DatatypeConverter.printBase64Binary(hash) );
//        // Add role "USER" to user.
//        Role role = roleBean.getUserRole();
//        List<Role> roles = new ArrayList<Role>();
//        roles.add(roleDao.save(role));
//        user.setRoles(roles);
//        // Persist user.
//        userDao.saveAndFlush(user);
//        return user;
    	return null;
    }
    
    /**
     * Return the number of registered Users
     * @return number of registered Users
     */
    @PermitAll
    public Long userCount() {
        return userDao.count();
    }

    /**
     * Update the User's password
     * @param userSave to update.
     * @return Updated User
     * @throws NoSuchAlgorithmException if SHA-256 not available.
     */
    @RolesAllowed({"USER"})
    public UserSave updatePassword(UserSave userSave) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(userSave.getPassword().getBytes());
        userSave.setPassword( DatatypeConverter.printBase64Binary(hash) ); 
        return userDao.save(userSave);
    }

    /**
     * Merge user with Database
     * @param userSave to merge.
     * @return Merged User
     */
    @PermitAll
    public UserSave merge(UserSave userSave) {
        return userDao.save(userSave);
    }
    
    /**
     * return User for email.
     * @param email to search for.
     * @return User found, else runtime exception.
     */
    @PermitAll
    public UserSave findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * return User for email.
     * @param email to search for.
     * @param verifyKey for user
     * @return User found, else runtime exception.
     */
    @PermitAll
    public UserSave verifyUser(String email, String verifyKey) {
//        List<User> users = em.createNamedQuery(User.FIND_BY_EMAIL, User.class)
//            .setParameter("email", email)
//            .getResultList();
    	UserSave userSave = userDao.findByEmail(email); 
        if ( userSave != null && userSave.getVerifyKey().equals(verifyKey)) {
        	userSave.setVerified(true);
        	userSave.setStartVerify(false);
        	userSave.setVerifyErrors(0);
        	userSave.setVerifyCount(0);
        }
        return userSave;
    }

    @PermitAll
    public UserSave checkUserByEmail(String email) {
    	return userDao.findByEmail(email);
    }
    /**
     * Delete User by Database Id
     * @param id to delete.
     */
//    @RolesAllowed({"ADMIN"})
    @PermitAll // because welcoming service uses it. 
    public void delete(Long id) {
        userDao.deleteById(id);
    }
    
    /**
     * Remove verification flag for user id.
     * @param id to find.
     */
    @RolesAllowed({"ADMIN"})    
	public void unverify(Long id) {
        UserSave userSave = userDao.getOne(id);
        userSave.setVerified(false);
	}
    /**
     * Find User by Database Id
     * @param id to find.
     * @return User or null if not exists
     */
    @RolesAllowed({"ADMIN"})
    public UserSave findById(Long id) {
        return userDao.getOne(id);
    }
    
    /**
     * Get List of all Users
     * @return List of all Users
     */
    // @RolesAllowed({"ADMIN"})
    @PermitAll // because court Report service uses it. 
    public List<UserSave> findAll() {
        return userDao.findAll();
    }
    
    /**
     * Promote User by Database Id by adding "ADMIN" role to user.
     * @param id to promote.
     * @return User or null if not exists
     */
    @RolesAllowed({"ADMIN"})
    public UserSave promoteUser(Long id) {
        UserSave userSave = userDao.getOne(id);
        userSave.getRoles().add(roleBean.getAdminRole());
//        return em.merge( user );
        return userSave;
    }
    
    /**
     * Demote User by Database Id by removing "ADMIN" role from user.
     * @param id of user to demote.
     * @return User or null if not exists
     */
    @RolesAllowed({"ADMIN"})
    public UserSave demoteUser(Long id) {
        UserSave userSave = userDao.getOne(id);
        Iterator<RoleSave> rIt = userSave.getRoles().iterator();
        while ( rIt.hasNext()  ) {
            RoleSave roleSave = rIt.next();
            if ( roleSave.getRole().equals("ADMIN")) rIt.remove();
        }
        return userDao.save(userSave);
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
	public void incrementVerifyCount(UserSave userSave) {
		userSave.setVerifyCount( userSave.getVerifyCount() + 1);
        userDao.save(userSave);
	}
    @PermitAll
	public void incrementVerifyErrors(UserSave userSave) {
		userSave.setVerifyErrors( userSave.getVerifyErrors() + 1);
        userDao.save(userSave);
	}
    @PermitAll
	public List<UserSave> findAllUnverified() {
        return userDao.findUnverified();
	}

    @PermitAll
	public void incrementWelcomeErrors(UserSave userSave) {
		userSave.setWelcomeErrors( userSave.getWelcomeErrors() + 1);
        userDao.save(userSave);
	}

    @PermitAll
	public void setWelcomedTrue(UserSave userSave) {
		userSave.setWelcomed( true );
        userDao.save(userSave);
	}

    @PermitAll
	public void setOptOut(UserSave userSave) {
		userSave.setOptout( true );
        userDao.save(userSave);
	}

    @PermitAll
	public void clearOptOut(UserSave userSave) {
		userSave.setOptout( false );
        userDao.save(userSave);
	}

    @PermitAll
    public List<UserSave> findAllUnWelcomed() {
        return userDao.findUnwelcomed();
	}

}