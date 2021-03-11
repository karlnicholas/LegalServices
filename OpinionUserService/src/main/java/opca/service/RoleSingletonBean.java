package opca.service;

import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.github.karlnicholas.opinionservices.security.dao.RoleDao;
import com.github.karlnicholas.opinionservices.security.dao.UserDao;
import com.github.karlnicholas.opinionservices.security.model.Role;
import com.github.karlnicholas.opinionservices.security.model.User;

/**
 * This class is a singleton that loads and holds all Role definitions from 
 * the database. 
 * 
 * @author Karl Nicholas
 *
 */
//@Singleton
@Component
public class RoleSingletonBean {
    private List<Role> allRoles;
    private final RoleDao roleDao;
    private final UserDao userDao;

    public RoleSingletonBean(RoleDao roleDao, UserDao userDao) {
		super();
		this.roleDao = roleDao;
		this.userDao = userDao;
	}

	//private constructor to avoid client applications to use constructor
    @PostConstruct
    protected void postConstruct(){
//        allRoles = roleDao.listAvailable();
//        // initialize if needed
//        if ( allRoles.size() == 0 ) {
//        	Role userRole = new Role();
//        	userRole.setRole("USER");
//        	roleDao.save(userRole);
//        	allRoles.add(userRole);
//        	Role adminRole = new Role();
//        	adminRole.setRole("ADMIN");
//        	roleDao.save(adminRole);
//        	allRoles.add(adminRole);
//        	// might as well add an administrator now as well.
//        	User admin = new User("karl.nicholas@outlook.com", true, "N3sPSBxOjdhCygeA8LkqtBskJ+v8TR0do4zJRTIQ4Aw=", Locale.US);
//        	admin.setFirstName("Karl");
//        	admin.setLastName("Nicholas");
//        	admin.setVerified(true);
//        	admin.setRoles(allRoles);
//        	userDao.save(admin);
//        }
    }

    /**
     * Get the USER Role
     * @return USER Role
     */
    public Role getUserRole()  {
        for ( Role role: allRoles ) {
            if ( role.getRole().equals("USER")) return role;
        }
        throw new RuntimeException("Role USER not found"); 
    }
    /**
     * Get the ADMIN Role
     * @return ADMIN Role
     */
    public Role getAdminRole()  {
        for ( Role role: allRoles ) {
            if ( role.getRole().equals("ADMIN")) return role;
        }
        throw new RuntimeException("Role ADMIN not found"); 
    }
}
