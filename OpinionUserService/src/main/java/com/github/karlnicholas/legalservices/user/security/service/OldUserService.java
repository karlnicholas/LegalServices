package com.github.karlnicholas.legalservices.user.security.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.model.RoleSave;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.security.dao.RoleDao;
import com.github.karlnicholas.legalservices.user.security.dao.UserDao;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

//@Stateless
//@Service
public class OldUserService {
//    private final RoleSingletonBean roleBean;
    private final UserDao userDao;
    private final RoleDao roleDao;
//
	public OldUserService(UserDao userDao, RoleDao roleDao) {
		super();
		this.userDao = userDao;
		this.roleDao = roleDao;
	}
//
//	/**
//     * Register new users. Encodes the password and adds the "USER" role to the user's roles.
//     * Returns null if user already exists.
//     * @param User new user.
//     * @return user with role added and password encoded, unless user already exists, then null.
//     * @throws NoSuchAlgorithmException if SHA-256 not available. 
//     */
//    @PermitAll
//    public User encodeAndSave(User User) throws NoSuchAlgorithmException {
////        // sanity check to see if user already exists.
//////        TypedQuery<Long> q = userRepository.createNamedQuery(User.COUNT_EMAIL, Long.class).setParameter("email", user.getEmail());
//////        if ( q.getSingleResult().longValue() > 0L ) {
//////            // show error condition
//////            return null;
//////        }
////    	Long existingUser = userDao.countByEmail(user.getEmail());
////		if ( existingUser > 0L ) {
////			// show error condition
////			return null;
////		}
////        // Encode password
////        byte[] hash = MessageDigest.getInstance("SHA-256").digest(user.getPassword().getBytes());
////        user.setPassword( DatatypeConverter.printBase64Binary(hash) );
////        // Add role "USER" to user.
////        Role role = roleBean.getUserRole();
////        List<Role> roles = new ArrayList<Role>();
////        roles.add(roleDao.save(role));
////        user.setRoles(roles);
////        // Persist user.
////        userDao.saveAndFlush(user);
////        return user;
//    	return null;
//    }
//    
//    /**
//     * Return the number of registered Users
//     * @return number of registered Users
//     */
//    @PermitAll
//    public Long userCount() {
//        return userDao.count();
//    }
//
//    /**
//     * Update the User's password
//     * @param User to update.
//     * @return Updated User
//     * @throws NoSuchAlgorithmException if SHA-256 not available.
//     */
//    @RolesAllowed({"USER"})
//    public User updatePassword(User User) throws NoSuchAlgorithmException {
//        byte[] hash = MessageDigest.getInstance("SHA-256").digest(User.getPassword().getBytes());
//        User.setPassword( DatatypeConverter.printBase64Binary(hash) ); 
//        return userDao.save(User);
//    }
//
//    /**
//     * Merge user with Database
//     * @param User to merge.
//     * @return Merged User
//     */
//    @PermitAll
//    public User merge(User User) {
//        return userDao.save(User);
//    }
//    
//    /**
//     * return User for email.
//     * @param email to search for.
//     * @return User found, else runtime exception.
//     */
//    @PermitAll
//    public User findByEmail(String email) {
//        return userDao.findByEmail(email);
//    }
//
//    /**
//     * return User for email.
//     * @param email to search for.
//     * @param verifyKey for user
//     * @return User found, else runtime exception.
//     */
//    @PermitAll
//    public User verifyUser(String email, String verifyKey) {
////        List<User> users = em.createNamedQuery(User.FIND_BY_EMAIL, User.class)
////            .setParameter("email", email)
////            .getResultList();
//    	User User = userDao.findByEmail(email); 
//        if ( User != null && User.getVerifyKey().equals(verifyKey)) {
//        	User.setVerified(true);
//        	User.setStartVerify(false);
//        	User.setVerifyErrors(0);
//        	User.setVerifyCount(0);
//        }
//        return User;
//    }
//
//    @PermitAll
//    public User checkUserByEmail(String email) {
//    	return userDao.findByEmail(email);
//    }
//    /**
//     * Delete User by Database Id
//     * @param id to delete.
//     */
////    @RolesAllowed({"ADMIN"})
//    @PermitAll // because welcoming service uses it. 
//    public void delete(Long id) {
//        userDao.deleteById(id);
//    }
//    
//    /**
//     * Remove verification flag for user id.
//     * @param id to find.
//     */
//    @RolesAllowed({"ADMIN"})    
//	public void unverify(Long id) {
//        User User = userDao.getOne(id);
//        User.setVerified(false);
//	}
//    /**
//     * Find User by Database Id
//     * @param id to find.
//     * @return User or null if not exists
//     */
//    @RolesAllowed({"ADMIN"})
//    public User findById(Long id) {
//        return userDao.getOne(id);
//    }
//    
    /**
     * Get List of all Users
     * @return List of all Users
     */
    // @RolesAllowed({"ADMIN"})
    @PermitAll // because court Report service uses it. 
    public List<ApplicationUser> findAll() {
        return userDao.findAll();
    }
    
//    /**
//     * Promote User by Database Id by adding "ADMIN" role to user.
//     * @param id to promote.
//     * @return User or null if not exists
//     */
//    @RolesAllowed({"ADMIN"})
//    public User promoteUser(Long id) {
//        User User = userDao.getOne(id);
//        User.getRoles().add(roleBean.getAdminRole());
////        return em.merge( user );
//        return User;
//    }
//    
//    /**
//     * Demote User by Database Id by removing "ADMIN" role from user.
//     * @param id of user to demote.
//     * @return User or null if not exists
//     */
//    @RolesAllowed({"ADMIN"})
//    public User demoteUser(Long id) {
//        User User = userDao.getOne(id);
//        Iterator<RoleSave> rIt = User.getRoles().iterator();
//        while ( rIt.hasNext()  ) {
//            RoleSave roleSave = rIt.next();
//            if ( roleSave.getRole().equals("ADMIN")) rIt.remove();
//        }
//        return userDao.save(User);
//    }
//
//    /**
//     * Manually encode a password
//     * @param args not user.
//     * @throws Exception if any.
//     */
//    public static void main(String[] args) throws Exception {
//        byte[] hash = MessageDigest.getInstance("SHA-256").digest("karl.nicholas@outlook.com".getBytes());
//        System.out.println( DatatypeConverter.printBase64Binary(hash) );
//    }
//
//    @PermitAll
//	public void incrementVerifyCount(User User) {
//		User.setVerifyCount( User.getVerifyCount() + 1);
//        userDao.save(User);
//	}
//    @PermitAll
//	public void incrementVerifyErrors(User User) {
//		User.setVerifyErrors( User.getVerifyErrors() + 1);
//        userDao.save(User);
//	}
//    @PermitAll
//	public List<User> findAllUnverified() {
//        return userDao.findUnverified();
//	}
//
//    @PermitAll
//	public void incrementWelcomeErrors(User User) {
//		User.setWelcomeErrors( User.getWelcomeErrors() + 1);
//        userDao.save(User);
//	}
//
//    @PermitAll
//	public void setWelcomedTrue(User User) {
//		User.setWelcomed( true );
//        userDao.save(User);
//	}
//
//    @PermitAll
//	public void setOptOut(User User) {
//		User.setOptout( true );
//        userDao.save(User);
//	}
//
//    @PermitAll
//	public void clearOptOut(User User) {
//		User.setOptout( false );
//        userDao.save(User);
//	}
//
//    @PermitAll
//    public List<User> findAllUnWelcomed() {
//        return userDao.findUnwelcomed();
//	}

}