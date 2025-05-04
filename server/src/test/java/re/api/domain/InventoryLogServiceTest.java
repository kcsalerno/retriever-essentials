package re.api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.AppUserRepository;
import re.api.data.InventoryLogRepository;
import re.api.data.ItemRepository;
import re.api.models.AppUser;
import re.api.models.InventoryLog;
import re.api.models.Item;
import re.api.models.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class InventoryLogServiceTest {

    @MockitoBean
    private InventoryLogRepository logRepository;
    @MockitoBean
    private ItemRepository itemRepository;
    @MockitoBean
    private AppUserRepository appUserRepository;

    @Autowired
    private InventoryLogService inventoryLogService;

    @Test
    void shouldFindAll() {
        // Given
        List<InventoryLog> logs = makeInventoryLogs();
        // When
        when(logRepository.findAll()).thenReturn(logs);
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(anyInt())).thenReturn(makeItems().getFirst());
        // Then
        List<InventoryLog> result = inventoryLogService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("admin@school.edu", result.getFirst().getAuthority().getUsername());
    }

    @Test
    void shouldFindById() {
        // Given
        InventoryLog log = makeInventoryLogs().getFirst();
        // When
        when(logRepository.findById(1)).thenReturn(log);
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().getFirst());
        // Then
        InventoryLog result = inventoryLogService.findById(1);
        assertNotNull(result);
        assertEquals(1, result.getLogId());
        assertEquals("Restock", result.getReason());
    }

    @Test
    void shouldNotFindById() {
        // Given
        // When
        when(logRepository.findById(999)).thenReturn(null);
        // Then
        InventoryLog result = inventoryLogService.findById(999);
        assertNull(result);
    }

    @Test
    void shouldFindByItemId() {
        // Given
        // When
        when(logRepository.findByItemId(1)).thenReturn(makeInventoryLogs());
        when(itemRepository.findById(1)).thenReturn(makeItems().getFirst());
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        // Then
        List<InventoryLog> result = inventoryLogService.findByItemId(1);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldNotFindByItemId() {
        // Given
        // When
        when(logRepository.findByItemId(999)).thenReturn(List.of());
        // Then
        List<InventoryLog> result = inventoryLogService.findByItemId(999);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByAuthorityId() {
        // Given
        // When
        when(logRepository.findByAuthorityId(1)).thenReturn(makeInventoryLogs());
        when(itemRepository.findById(anyInt())).thenReturn(makeItems().get(0));
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        // Then
        List<InventoryLog> result = inventoryLogService.findByAuthorityId(1);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldNotFindByAuthorityId() {
        // Given
        // When
        when(logRepository.findByAuthorityId(999)).thenReturn(List.of());
        List<InventoryLog> result = inventoryLogService.findByAuthorityId(999);
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByItemName() {
        // Given
        // When
        when(itemRepository.findByName("Rice")).thenReturn(makeItems().get(0));
        when(logRepository.findByItemId(1)).thenReturn(makeInventoryLogs());
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        // Then
        List<InventoryLog> result = inventoryLogService.findByItemName("Rice");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldNotFindByItemName() {
        // Given
        // When
        when(itemRepository.findByName("Unknown")).thenReturn(null);
        // Then
        List<InventoryLog> result = inventoryLogService.findByItemName("Unknown");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByAuthorityEmail() {
        // Given
        AppUser admin = makeAdmin();
        // When
        when(appUserRepository.findByEmail("admin@school.edu")).thenReturn(admin);
        when(logRepository.findByAuthorityId(1)).thenReturn(makeInventoryLogs());
        when(itemRepository.findById(anyInt())).thenReturn(makeItems().getFirst());
        when(appUserRepository.findById(1)).thenReturn(admin);
        // Then
        List<InventoryLog> result = inventoryLogService.findByAuthorityEmail("admin@school.edu");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldNotFindByAuthorityEmail() {
        //Given
        // When
        when(appUserRepository.findByEmail("notfound@domain.com")).thenReturn(null);
        // Then
        List<InventoryLog> result = inventoryLogService.findByAuthorityEmail("notfound@domain.com");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldAdd() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 1, 1, 10, "Restock", LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        when(logRepository.add(any())).thenReturn(new InventoryLog(10, 1, 1, 10, "Restock", newLog.getTimeStamp()));
        when(itemRepository.updateCurrentCount(1, 10)).thenReturn(true);
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(10, result.getPayload().getLogId());
    }

    @Test
    void shouldNotAddWhenLogIsNull() {
        // Given
        // When
        // Then
        Result<InventoryLog> result = inventoryLogService.add(null);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Inventory log cannot be null."));
    }

    @Test
    void shouldNotAddWhenAuthorityIsInvalidOrDisabled() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 999, 1, 10, "Restock", LocalDateTime.now());
        // When
        when(appUserRepository.findById(999)).thenReturn(null); // authority not found
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Authority ID does not exist or is disabled."));
    }

    @Test
    void shouldNotAddWhenItemIsInvalidOrDisabled() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 1, 999, 10, "Restock", LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(999)).thenReturn(null); // item not found
        when(logRepository.findAll()).thenReturn(List.of());
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item ID does not exist or is disabled."));
    }

    @Test
    void shouldNotAddWhenQuantityIsZero() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 1, 1, 0, "Restock", LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Quantity change cannot be zero."));
    }

    @Test
    void shouldNotAddWhenReasonMissing() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 1, 1, 5, " ", LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Reason for inventory change is required."));
    }

    @Test
    void shouldNotAddWhenReasonTooLong() {
        // Given
        String longReason = "A".repeat(300);
        InventoryLog newLog = new InventoryLog(0, 1, 1, 5, longReason, LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().getFirst().contains("Reason must not exceed"));
    }

    @Test
    void shouldNotAddWhenTimestampMissing() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 1, 1, 5, "Restock", null);
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Log date is required."));
    }

    @Test
    void shouldNotAddWhenDuplicate() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 1, 1, 5, "Restock", LocalDateTime.of(2023, 1, 1, 0, 0));
        InventoryLog existing = new InventoryLog(5, 1, 1, 5, "Restock", LocalDateTime.of(2023, 1, 1, 0, 0));
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of(existing));
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Duplicate log entry detected."));
    }

    @Test
    void shouldNotAddWhenLogIdAlreadySet() {
        // Given
        InventoryLog newLog = new InventoryLog(100, 1, 1, 5, "Restock", LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Inventory Log ID cannot be set for add operation."));
    }

    @Test
    void shouldNotAddWhenInventoryUpdateFails() {
        // Given
        InventoryLog newLog = new InventoryLog(0, 1, 1, 10, "Restock", LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findAll()).thenReturn(List.of());
        when(logRepository.add(any())).thenReturn(new InventoryLog(10, 1, 1, 10, "Restock", newLog.getTimeStamp()));
        when(itemRepository.updateCurrentCount(1, 10)).thenReturn(false); // fail update
        // Then
        Result<InventoryLog> result = inventoryLogService.add(newLog);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 1"));
    }

    @Test
    void shouldUpdate() {
        // Given
        InventoryLog existingLog = new InventoryLog(1, 1, 1, 5, "Initial stock", LocalDateTime.now().minusDays(1));
        InventoryLog updatedLog = new InventoryLog(1, 1, 1, 8, "Restocked", LocalDateTime.now());
        AppUser authority = makeAdmin();
        Item item = makeItems().getFirst(); // Rice
        // When
        when(logRepository.findAll()).thenReturn(List.of(existingLog));
        when(logRepository.findById(1)).thenReturn(existingLog);
        when(logRepository.update(updatedLog)).thenReturn(true);
        when(itemRepository.findById(1)).thenReturn(item);
        when(itemRepository.updateCurrentCount(1, 3)).thenReturn(true); // 8 - 5 = +3
        when(appUserRepository.findById(1)).thenReturn(authority);
        // Then
        Result<InventoryLog> result = inventoryLogService.update(updatedLog);
        assertTrue(result.isSuccess());
        assertEquals(8, result.getPayload().getQuantityChange());
        verify(itemRepository).updateCurrentCount(1, 3);
    }

    @Test
    void shouldNotUpdateWhenIdNotSet() {
        // Given
        InventoryLog log = new InventoryLog(0, 1, 1, 5,
                "Added items", LocalDateTime.now());
        // When
        when(logRepository.findById(0)).thenReturn(null);
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().getFirst());
        // Then
        Result<InventoryLog> result = inventoryLogService.update(log);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Inventory Log ID must be set for update."));
    }

    @Test
    void shouldNotUpdateWhenLogNotFound() {
        // Given
        InventoryLog log = new InventoryLog(999, 1, 1, 5, "Added items", LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));
        when(logRepository.findById(999)).thenReturn(null);
        // Then
        Result<InventoryLog> result = inventoryLogService.update(log);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Inventory Log ID not found."));
    }

    @Test
    void shouldNotUpdateWhenInventoryFails() {
        InventoryLog original = new InventoryLog(10, 1, 1, 5, "Original", LocalDateTime.now().minusDays(1));
        InventoryLog updated = new InventoryLog(10, 1, 1, 10, "Adjusted", LocalDateTime.now());

        when(logRepository.findById(10)).thenReturn(original);
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0)); // Rice
        when(itemRepository.updateCurrentCount(1, 5)).thenReturn(false); // simulate failure

        Result<InventoryLog> result = inventoryLogService.update(updated);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 1"));
    }

    @Test
    void shouldNotUpdateWhenAuthorityIdInvalid() {
        InventoryLog log = new InventoryLog(1, -1, 1, 5, "Correction", LocalDateTime.now());

        Result<InventoryLog> result = inventoryLogService.update(log);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Invalid authority ID."));
    }

    @Test
    void shouldNotUpdateWhenItemIdInvalid() {
        InventoryLog log = new InventoryLog(1, 1, 0, 5, "Correction", LocalDateTime.now());

        when(appUserRepository.findById(1)).thenReturn(makeAdmin());

        Result<InventoryLog> result = inventoryLogService.update(log);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Valid item ID is required."));
    }

    @Test
    void shouldDeleteById() {
        // Given
        InventoryLog logToDelete = new InventoryLog(1, 1, 2, -4, "Removed expired", LocalDateTime.now().minusHours(6));
        Item item = makeItems().get(1); // Beans
        // When
        when(logRepository.findById(1)).thenReturn(logToDelete);
        when(itemRepository.updateCurrentCount(2, 4)).thenReturn(true); // Reverting -4 -> +4
        when(logRepository.deleteById(1)).thenReturn(true);
        when(itemRepository.findById(2)).thenReturn(item);
        // Then
        Result<InventoryLog> result = inventoryLogService.deleteById(1);
        assertTrue(result.isSuccess());
        verify(itemRepository).updateCurrentCount(2, 4);
        verify(logRepository).deleteById(1);
    }

    @Test
    void shouldNotDeleteById() {
        // Given
        InventoryLog logToDelete = new InventoryLog(1, 1, 2,
                -5, "Spoiled", LocalDateTime.now().minusDays(1));
        // When
        when(logRepository.findById(1)).thenReturn(logToDelete);
        when(itemRepository.updateCurrentCount(2, 5)).thenReturn(true); // Rollback inventory succeeds
        when(logRepository.deleteById(1)).thenReturn(false); // But deletion fails
        when(itemRepository.findById(2)).thenReturn(makeItems().get(1)); // Beans
        // Then
        Result<InventoryLog> result = inventoryLogService.deleteById(1);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Inventory Log ID not found."));
        verify(itemRepository).updateCurrentCount(2, 5);
        verify(logRepository).deleteById(1);
    }

    @Test
    void shouldNotDeleteWhenLogNotFound() {
        // Given
        // When
        when(logRepository.findById(999)).thenReturn(null);
        // Then
        Result<InventoryLog> result = inventoryLogService.deleteById(999);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Inventory Log ID not found."));
    }

    @Test
    void shouldNotDeleteWhenItemUpdateFails() {
        // Given
        InventoryLog logToDelete = new InventoryLog(1, 1, 2, 3, "Adjustment", LocalDateTime.now());
        // When
        when(logRepository.findById(1)).thenReturn(logToDelete);
        when(itemRepository.updateCurrentCount(2, -3)).thenReturn(false); // Simulate failure
        // Then
        Result<InventoryLog> result = inventoryLogService.deleteById(1);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 2"));
    }

    private List<InventoryLog> makeInventoryLogs() {
        return List.of(
                new InventoryLog(1, 1, 1, 10,
                        "Restock", LocalDateTime.now().minusDays(2)),
                new InventoryLog(2, 1, 2, -5,
                        "Spoiled items removed", LocalDateTime.now().minusDays(1))
        );
    }

    private AppUser makeAdmin() {
        return new AppUser(1, "admin@school.edu", "password",
                UserRole.ADMIN, true);
    }

    private List<Item> makeItems() {
        return List.of(
                new Item(1, "Rice", "Staple food", "Carbs",
                        "http://image1", "Staple", 100,
                        2, BigDecimal.ZERO, true),
                new Item(2, "Beans", "Protein source", "Protein",
                        "http://image2", "Staple", 50,
                        1, BigDecimal.ZERO, true)
        );
    }
}