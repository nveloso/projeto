package pt.isel.ps.gis.bll.implementations;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.ps.gis.bll.UserService;
import pt.isel.ps.gis.dal.repositories.RoleRepository;
import pt.isel.ps.gis.dal.repositories.UserRoleRepository;
import pt.isel.ps.gis.dal.repositories.UsersRepository;
import pt.isel.ps.gis.exceptions.EntityAlreadyExistsException;
import pt.isel.ps.gis.exceptions.EntityException;
import pt.isel.ps.gis.exceptions.EntityNotFoundException;
import pt.isel.ps.gis.exceptions.InsufficientPrivilegesException;
import pt.isel.ps.gis.model.Role;
import pt.isel.ps.gis.model.UserRole;
import pt.isel.ps.gis.model.Users;
import pt.isel.ps.gis.utils.AuthorizationProvider;
import pt.isel.ps.gis.utils.ValidationsUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final static String ROLE_USER = "ROLE_USER";

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationProvider authorizationProvider;

    private final MessageSource messageSource;

    public UserServiceImpl(UsersRepository usersRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, AuthorizationProvider authorizationProvider, MessageSource messageSource) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorizationProvider = authorizationProvider;
        this.messageSource = messageSource;
    }

    @Override
    public boolean existsUserByUserUsername(String username) throws EntityException {
        ValidationsUtils.validateUserUsername(username);
        return usersRepository.existsByUsersUsername(username);
    }

    @Override
    public Users getUserByUserId(Long userId, Locale locale) throws EntityException, EntityNotFoundException {
        ValidationsUtils.validateUserId(userId);
        return usersRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d does not exist.", userId), messageSource.getMessage("user_Id_Not_Exist", new Object[]{userId}, locale)));
    }

    @Override
    public Users getUserByUserUsername(String authenticatedUsername, String username, Locale locale) throws EntityException, EntityNotFoundException, InsufficientPrivilegesException {
        ValidationsUtils.validateUserUsername(username);
        authorizationProvider.checkUserAuthorizationToAccessResource(authenticatedUsername, username);
        return usersRepository
                .findByUsersUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with username %s does not exist.", username), messageSource.getMessage("user_Username_Not_Exist", new Object[]{username}, locale)));
    }

    @Override
    public List<Users> getUsersStartsWithUsername(String username) throws EntityException {
        ValidationsUtils.validateUserUsername(username);
        return usersRepository.findAllByUsersUsernameStartsWith(username);
    }

    @Transactional
    @Override
    public Users addUser(String username, String email, Short age, String name, String password, Locale locale) throws EntityException, EntityAlreadyExistsException {
        if (existsUserByUserUsername(username))
            throw new EntityAlreadyExistsException(String.format("Username %s is already in use.", username), messageSource.getMessage("username_Already_Exist", new Object[]{username}, locale));
        ValidationsUtils.validateUserEmail(email);
        if (usersRepository.existsByUsersEmail(email))
            throw new EntityAlreadyExistsException(String.format("Already exists a user with email: %s", email), messageSource.getMessage("email_Aready_Exist", new Object[]{email}, locale));
        Users users = new Users(username, email, age, name, passwordEncoder.encode(password));
        users = usersRepository.save(users);
        Role userRole = roleRepository.findByRoleName(ROLE_USER).orElseThrow(() -> new IllegalStateException(messageSource.getMessage("Role_User_Not_Found", null, locale)));
        userRoleRepository.save(new UserRole(users.getUsersId(), userRole.getRoleId()));
        return users;
    }

    @Transactional
    @Override
    public Users updateUser(String authenticatedUsername, String username, String email, Short age, String name, String password, Locale locale) throws EntityException, EntityNotFoundException, EntityAlreadyExistsException, InsufficientPrivilegesException {
        Users user = getUserByUserUsername(authenticatedUsername, username, locale);
        ValidationsUtils.validateUserEmail(email);
        if (!user.getUsersEmail().equals(email) && !usersRepository.existsByUsersEmail(email))
            throw new EntityAlreadyExistsException(String.format("Already exists a user with email: %s", email), messageSource.getMessage("email_Aready_Exist", new Object[]{email}, locale));
        user.setUsersEmail(email);
        user.setUsersAge(age);
        user.setUsersName(name);
        user.setUsersPassword(passwordEncoder.encode(password));
        return user;
    }

    @Transactional
    @Override
    public void deleteUserByUserUsername(String authenticatedUsername, String username, Locale locale) throws EntityException, EntityNotFoundException, InsufficientPrivilegesException {
        authorizationProvider.checkUserAuthorizationToAccessResource(authenticatedUsername, username);
        checkUserUsername(username, locale);
        // Remover o utilizador bem como todas as relações das quais o utilizador seja parte integrante
        usersRepository.deleteCascadeUserByUsername(username);
    }

    private void checkUserUsername(String username, Locale locale) throws EntityException, EntityNotFoundException {
        ValidationsUtils.validateUserUsername(username);
        if (!usersRepository.existsByUsersUsername(username))
            throw new EntityNotFoundException(String.format("Username %s is already in use.", username), messageSource.getMessage("user_Username_Not_Exist", new Object[]{username}, locale));

    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user;
        try {
            Locale locale = LocaleContextHolder.getLocale();
            user = getUserByUserUsername(username, username, locale);
        } catch (EntityException | EntityNotFoundException | InsufficientPrivilegesException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
        List<GrantedAuthority> authorities = user.getUsersrolesByUsersId()
                .stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRoleByRoleId().getRoleName()))
                .collect(Collectors.toList());
        return new User(user.getUsersUsername(), user.getUsersPassword(), authorities);
    }
}
