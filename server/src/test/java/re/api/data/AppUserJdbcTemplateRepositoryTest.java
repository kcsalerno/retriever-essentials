package re.api.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.AppUser;
import re.api.models.UserRole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppUserJdbcTemplateRepositoryTest {
    private final int USER_COUNT = 3;

    @Autowired
    AppUserJdbcTemplateRepository appUserJdbcTemplateRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindAll() {
        // Arrange
        // Act
        List<AppUser> users = appUserJdbcTemplateRepository.findAll();
        // Assert
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() == USER_COUNT
                || users.size() == USER_COUNT + 1);
    }

    @Test
    void shouldFindById() {
        // Arrange
        int userId = 1;
        // Act
        AppUser user = appUserJdbcTemplateRepository.findById(userId);
        // Assert
        assertNotNull(user);
        assertEquals(userId, user.getAppUserId());
        assertEquals("admin@umbc.com", user.getUsername());
        assertEquals("ROLE_ADMIN", user.getAuthorities().stream().findFirst().get().getAuthority());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int userId = 9999;
        // Act
        AppUser user = appUserJdbcTemplateRepository.findById(userId);
        // Assert
        assertNull(user);
    }

    @Test
    void shouldFindByEmail() {
        // Arrange
        String email = "admin@umbc.com";
        // Act
        AppUser user = appUserJdbcTemplateRepository.findByEmail(email);
        // Assert
        assertNotNull(user);
        assertEquals(1, user.getAppUserId());
        assertEquals("admin@umbc.com", user.getUsername());
        assertEquals("ROLE_ADMIN", user.getAuthorities().stream().findFirst().get().getAuthority());
    }

    @Test
    void shouldNotFindByEmail() {
        // Arrange
        String email = "bad@email.foo";
        // Act
        AppUser user = appUserJdbcTemplateRepository.findByEmail(email);
        // Assert
        assertNull(user);
    }

    @Test
    void shouldAdd() {
        // Arrange
        AppUser testUser = new AppUser(0, "test@umbc.com",
                "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa",
                UserRole.ADMIN, true);
        // Act
        AppUser addedUser = appUserJdbcTemplateRepository.add(testUser);
        // Assert
        assertNotNull(addedUser);
        assertEquals(testUser.getUsername(), addedUser.getUsername());
        assertEquals(USER_COUNT + 1, addedUser.getAppUserId());
    }

    @Test
    void shouldUpdatePassword() {
        // Arrange
        int userId = 2;
        AppUser userToUpdate = appUserJdbcTemplateRepository.findById(userId);
        assertNotNull(userToUpdate);
        String newPassword = "newPassword";
        // Act
        boolean updatedUser = appUserJdbcTemplateRepository.updatePassword(userToUpdate.getAppUserId(),newPassword);
        // Assert
        assertTrue(updatedUser);
        assertTrue(appUserJdbcTemplateRepository.findById(userId).getPassword().equals(newPassword));
    }

    @Test
    void shouldNoteUpdatePasswordBadId() {
        // Arrange
        int userId = 9999;
        AppUser userToUpdate = appUserJdbcTemplateRepository.findById(userId);
        assertNull(userToUpdate);
        String newPassword = "newPassword";
        // Act
        boolean updatedUser = appUserJdbcTemplateRepository.updatePassword(userId, newPassword);
        // Assert
        assertFalse(updatedUser);
    }

    @Test
    void shouldDisableById() {
        // Arrange
        int userId = 2;
        AppUser userToDisable = appUserJdbcTemplateRepository.findById(userId);
        assertNotNull(userToDisable);
        assertTrue(userToDisable.isEnabled());
        // Act
        boolean disabledUser = appUserJdbcTemplateRepository.disableById(userToDisable.getAppUserId());
        // Assert
        assertTrue(disabledUser);
        assertFalse(appUserJdbcTemplateRepository.findById(userId).isEnabled());
    }

    @Test
    void shouldNotDisableByBadId() {
        // Arrange
        int userId = 9999;
        AppUser userToDisable = appUserJdbcTemplateRepository.findById(userId);
        assertNull(userToDisable);
        // Act
        boolean disabledUser = appUserJdbcTemplateRepository.disableById(userId);
        // Assert
        assertFalse(disabledUser);
    }

    @Test
    void shouldEnableById() {
        // Arrange
        int userId = 3;
        AppUser testUser = appUserJdbcTemplateRepository.findById(userId);
        assertNotNull(testUser);
        assertTrue(testUser.isEnabled());
        boolean disabled = appUserJdbcTemplateRepository.disableById(testUser.getAppUserId());
        assertTrue(disabled);
        // Act
        boolean enabled = appUserJdbcTemplateRepository.enableById(testUser.getAppUserId());
        // Assert
        assertTrue(enabled);
        assertTrue(appUserJdbcTemplateRepository.findById(userId).isEnabled());
    }

    @Test
    void shouldNotEnableByBadId() {
        // Arrange
        int userId = 9999;
        AppUser testUser = appUserJdbcTemplateRepository.findById(userId);
        assertNull(testUser);
        // Act
        boolean enabled = appUserJdbcTemplateRepository.enableById(userId);
        // Assert
        assertFalse(enabled);
    }
}