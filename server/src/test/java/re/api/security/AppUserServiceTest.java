package re.api.security;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.AppUserRepository;
import re.api.domain.Result;
import re.api.models.AppUser;
import re.api.models.UserRole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class AppUserServiceTest {

    @MockitoBean
    private AppUserRepository appUserRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserService appUserService;

    @Test
    void shouldFindAll() {
        // Given
        List<AppUser> testUsers = makeAppUsers();
        // When
        when(appUserRepository.findAll()).thenReturn(testUsers);
        // Then
        List<AppUser> actualUsers = appUserService.findAll();
        assertEquals(testUsers, actualUsers);
        assertEquals(3, actualUsers.size());
        assertEquals("auth1", actualUsers.get(0).getUsername());
        assertEquals("auth2", actualUsers.get(1).getUsername());
        assertEquals("admin", actualUsers.get(2).getUsername());
        assertTrue(actualUsers.getFirst().hasRole(UserRole.AUTHORITY));
        assertTrue(actualUsers.get(1).hasRole(UserRole.AUTHORITY));
        assertTrue(actualUsers.get(2).hasRole(UserRole.ADMIN));
        assertTrue(actualUsers.get(0).isEnabled());
        assertTrue(actualUsers.get(1).isEnabled());
        assertTrue(actualUsers.get(2).isEnabled());
        assertEquals(1, actualUsers.get(0).getAppUserId());
        assertEquals(2, actualUsers.get(1).getAppUserId());
        assertEquals(3, actualUsers.get(2).getAppUserId());
    }

    @Test
    void shouldFindByEmail() {
        // Given
        AppUser testUser = new AppUser(1, "auth1", "password1",
                UserRole.AUTHORITY, true);
        // When
        when(appUserRepository.findByEmail("auth1")).thenReturn(testUser);
        // Then
        AppUser actualUser = appUserService.findByEmail("auth1");
        assertEquals(testUser, actualUser);
        assertEquals("auth1", actualUser.getUsername());
        assertEquals("password1", actualUser.getPassword());
        assertTrue(actualUser.hasRole(UserRole.AUTHORITY));
        assertTrue(actualUser.isEnabled());
    }

    @Test
    void shouldNotFindByEmail() {
        // Given
        when(appUserRepository.findByEmail("missing")).thenReturn(null);
        // When
        AppUser actualUser = appUserService.findByEmail("missing");
        // Then
        assertNull(actualUser);
    }

    @Test
    void shouldFindById() {
        // Given
        AppUser testUser = new AppUser(1, "auth1", "password1",
                UserRole.AUTHORITY, true);
        // When
        when(appUserRepository.findById(1)).thenReturn(testUser);
        // Then
        AppUser actualUser = appUserService.findById(1);
        assertEquals(testUser, actualUser);
        assertEquals("auth1", actualUser.getUsername());
        assertEquals("password1", actualUser.getPassword());
        assertTrue(actualUser.hasRole(UserRole.AUTHORITY));
        assertTrue(actualUser.isEnabled());
    }

    @Test
    void shouldNotFindById() {
        // Given
        when(appUserRepository.findById(999)).thenReturn(null);
        // When
        AppUser actualUser = appUserService.findById(999);
        // Then
        assertNull(actualUser);
    }

    @Test
    void shouldAdd() {
        // Given
        AppUser inputUser = new AppUser(0, "newuser@example.com",
                "plainpassword",
                UserRole.AUTHORITY, true);
        AppUser expectedUser = new AppUser(5, "newuser@example.com",
                "hashedpassword",
                UserRole.AUTHORITY, true);
        // When
        when(appUserRepository.findByEmail("newuser@example.com")).thenReturn(null);
        when(passwordEncoder.encode("plainpassword")).thenReturn("hashedpassword");
        when(appUserRepository.add(any(AppUser.class))).thenReturn(expectedUser);
        Result<AppUser> result = appUserService.add(inputUser);
        // Then
        assertTrue(result.isSuccess());
        assertEquals(expectedUser, result.getPayload());
        assertEquals("hashedpassword", result.getPayload().getPassword());
    }

    @Test
    void shouldNotAddWhenUserIsNull() {
        // Given
        // When
        Result<AppUser> result = appUserService.add(null);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User cannot be null."));
    }

    @Test
    void shouldNotAddWhenEmailIsBlank() {
        // Given
        AppUser user = new AppUser(0, "  ", "password",
                UserRole.AUTHORITY, true);
        // When
        // Then
        Result<AppUser> result = appUserService.add(user);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Email is required."));
    }

    @Test
    void shouldNotAddWhenPasswordIsBlank() {
        // Given
        AppUser user = new AppUser(0, "valid@example.com", "  ",
                UserRole.AUTHORITY, true);
        // When
        // Then
        Result<AppUser> result = appUserService.add(user);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Password is required."));
    }

    @Test
    void shouldNotAddWhenEmailAlreadyExists() {
        // Given
        AppUser user = new AppUser(0, "existing@example.com", "password",
                UserRole.AUTHORITY, true);
        // When
        when(appUserRepository.findByEmail("existing@example.com")).thenReturn(user);
        // Then
        Result<AppUser> result = appUserService.add(user);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Email already in use."));
    }


    @Test
    void shouldChangePassword() {
        // Given
        AppUser user = new AppUser(1, "auth1", "newpassword",
                UserRole.AUTHORITY, true);
        // When
        when(passwordEncoder.encode("newpassword")).thenReturn("hashedpassword");
        when(appUserRepository.updatePassword(1, "hashedpassword")).thenReturn(true);
        Result<AppUser> result = appUserService.changePassword(user);
        // Then
        assertTrue(result.isSuccess());
        assertNull(result.getPayload()); // Since changePassword doesn't set payload
    }

    @Test
    void shouldNotChangePasswordWhenUserIsNull() {
        // Given
        // When
        // Then
        Result<AppUser> result = appUserService.changePassword(null);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User cannot be null."));
    }

    @Test
    void shouldNotChangePasswordWhenPasswordTooShort() {
        // Given
        AppUser user = new AppUser(1, "auth1", "123", UserRole.AUTHORITY, true);
        // When
        // Then
        Result<AppUser> result = appUserService.changePassword(user);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Password must be at least 6 characters."));
    }

    @Test
    void shouldNotChangePasswordWhenUserNotFound() {
        // Given
        AppUser user = new AppUser(1, "auth1", "validPass", UserRole.AUTHORITY, true);
        // When
        when(passwordEncoder.encode("validPass")).thenReturn("hashedPass");
        when(appUserRepository.updatePassword(1, "hashedPass")).thenReturn(false);
        // Then
        Result<AppUser> result = appUserService.changePassword(user);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User not found."));
    }

    @Test
    void shouldEnableById() {
        // Given
        int userId = 1;
        when(appUserRepository.enableById(userId)).thenReturn(true);
        // When
        Result<AppUser> result = appUserService.enableById(userId);
        // Then
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotEnableByIdWhenNotFound() {
        // Given
        int userId = 999;
        when(appUserRepository.enableById(userId)).thenReturn(false);
        // When
        Result<AppUser> result = appUserService.enableById(userId);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User not found."));
    }

    @Test
    void shouldDisableById() {
        // Given
        int userId = 1;
        when(appUserRepository.disableById(userId)).thenReturn(true);
        // When
        Result<AppUser> result = appUserService.disableById(userId);
        // Then
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDisableByIdWhenNotFound() {
        // Given
        int userId = 999;
        when(appUserRepository.disableById(userId)).thenReturn(false);
        // When
        Result<AppUser> result = appUserService.disableById(userId);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("User not found."));
    }

    private List<AppUser> makeAppUsers() {
        return List.of(
                new AppUser(1, "auth1", "password1",
                        UserRole.AUTHORITY, true),
                new AppUser(2, "auth2", "password2",
                        UserRole.AUTHORITY, true),
                new AppUser(3, "admin", "adminpassword",
                        UserRole.ADMIN, true)
        );
    }
}