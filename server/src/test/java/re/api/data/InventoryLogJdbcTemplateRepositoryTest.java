package re.api.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.InventoryLog;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InventoryLogJdbcTemplateRepositoryTest {
    private final int LOG_COUNT = 4;

    @Autowired
    InventoryLogJdbcTemplateRepository inventoryLogJdbcTemplateRepository;

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
        var logs = inventoryLogJdbcTemplateRepository.findAll();
        // Assert
        assertNotNull(logs);
        assertFalse(logs.isEmpty());
        assertTrue(logs.size() == LOG_COUNT // If find occurs first
                || logs.size() == LOG_COUNT + 1 // If add occurs first
                || logs.size() == LOG_COUNT - 1); // If delete occurs first
    }

    // Log Entry for Find Tests: (2, 4, -2, 'Damaged Packaging')
    @Test
    void shouldFindById() {
        // Arrange
        int logId = 1;
        // Act
        var log = inventoryLogJdbcTemplateRepository.findById(logId);
        // Assert
        assertNotNull(log);
        assertEquals(logId, log.getLogId());
        assertEquals(2, log.getAuthorityId());
        assertEquals(4, log.getItemId());
        assertEquals(-2, log.getQuantityChange());
        assertEquals("Damaged Packaging", log.getReason());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int logId = 9999;
        // Act
        var log = inventoryLogJdbcTemplateRepository.findById(logId);
        // Assert
        assertNull(log);
    }

    @Test
    void shouldFindByItemId() {
        // Arrange
        int itemId = 4;
        // Act
        List<InventoryLog> logs = inventoryLogJdbcTemplateRepository.findByItemId(itemId);
        // Assert
        assertNotNull(logs);
        assertFalse(logs.isEmpty());
        assertEquals(2, logs.size()); // There are two logs for itemId 4
        assertEquals("Damaged Packaging", logs.getFirst().getReason());
    }

    @Test
    void shouldNotFindByBadItemId() {
        // Arrange
        int itemId = 9999;
        // Act
        List<InventoryLog> logs = inventoryLogJdbcTemplateRepository.findByItemId(itemId);
        // Assert
        assertNotNull(logs);
        assertTrue(logs.isEmpty());
    }

    @Test
    void shouldFindByAuthorityId() {
        // Arrange
        int authorityId = 2;
        // Act
        List<InventoryLog> logs = inventoryLogJdbcTemplateRepository.findByAuthorityId(authorityId);
        // Assert
        assertNotNull(logs);
        assertFalse(logs.isEmpty());
        assertEquals(2, logs.size()); // There are two logs for authorityId 2
        assertEquals("Damaged Packaging", logs.getFirst().getReason());
    }

    @Test
    void shouldNotFindByBadAuthorityId() {
        // Arrange
        int authorityId = 9999;
        // Act
        List<InventoryLog> logs = inventoryLogJdbcTemplateRepository.findByAuthorityId(authorityId);
        // Assert
        assertNotNull(logs);
        assertTrue(logs.isEmpty());
    }

    @Test
    void shouldAdd() {
        // Arrange
        InventoryLog testLog = new InventoryLog();
        testLog.setAuthorityId(3);
        testLog.setItemId(5);
        testLog.setQuantityChange(5);
        testLog.setReason("Corrected count");
        testLog.setTimeStamp(LocalDateTime.now());
        // Act
        InventoryLog addedLog = inventoryLogJdbcTemplateRepository.add(testLog);
        InventoryLog foundLog = inventoryLogJdbcTemplateRepository.findById(addedLog.getLogId());
        // Assert
        assertNotNull(addedLog);
        assertEquals(testLog.getAuthorityId(), foundLog.getAuthorityId());
        assertEquals(testLog.getItemId(), foundLog.getItemId());
        assertEquals(testLog.getQuantityChange(), foundLog.getQuantityChange());
        assertEquals(testLog.getReason(), foundLog.getReason());
        assertNotNull(foundLog.getTimeStamp());
        assertEquals(LOG_COUNT + 1, foundLog.getLogId());
    }

    @Test
    void shouldUpdate() {
        // Arrange
        int logId = 2;
        InventoryLog logToUpdate = inventoryLogJdbcTemplateRepository.findById(logId);
        assertNotNull(logToUpdate);
        logToUpdate.setQuantityChange(-10);
        logToUpdate.setReason("Whole box is expired");
        // Act
        boolean updated = inventoryLogJdbcTemplateRepository.update(logToUpdate);
        // Assert
        assertTrue(updated);
        assertEquals(-10, inventoryLogJdbcTemplateRepository.findById(logId).getQuantityChange());
        assertEquals("Whole box is expired", inventoryLogJdbcTemplateRepository.findById(logId).getReason());
    }

    @Test
    void shouldNotUpdateBadId() {
        // Arrange
        InventoryLog logToUpdate = new InventoryLog();
        logToUpdate.setLogId(9999);
        logToUpdate.setQuantityChange(-10);
        logToUpdate.setReason("Whole box is expired");
        // Act
        boolean updated = inventoryLogJdbcTemplateRepository.update(logToUpdate);
        // Assert
        assertFalse(updated);
    }

    @Test
    void shouldDeleteById() {
        // Arrange
        int logId = 3;
        InventoryLog logToDelete = inventoryLogJdbcTemplateRepository.findById(logId);
        assertNotNull(logToDelete);
        // Act
        boolean deleted = inventoryLogJdbcTemplateRepository.deleteById(logId);
        // Assert
        assertTrue(deleted);
        assertNull(inventoryLogJdbcTemplateRepository.findById(logId));
        assertTrue(LOG_COUNT == inventoryLogJdbcTemplateRepository.findAll().size() // If add occurs first
                || LOG_COUNT - 1 == inventoryLogJdbcTemplateRepository.findAll().size()); // If delete occurs first
    }

    @Test
    void shouldNotDeleteByBadId() {
        // Arrange
        int logId = 9999;
        // Act
        boolean deleted = inventoryLogJdbcTemplateRepository.deleteById(logId);
        // Assert
        assertFalse(deleted);
    }
}