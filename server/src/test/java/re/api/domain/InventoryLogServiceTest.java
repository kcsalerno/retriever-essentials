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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private InventoryLogService logService;

    @Test void shouldFindAll() {
        // Given
        List<InventoryLog> logs = makeLogs();
        when(logRepository.findAll()).thenReturn(logs);

        // When
        List<InventoryLog> result = logService.findAll();

        // Then
        assertEquals(logs, result);
    }

    @Test void shouldFindById() {
        // Given
        InventoryLog log = new InventoryLog();
        log.setLogId(1);
        when(logRepository.findById(1)).thenReturn(java.util.Optional.of(log));

        // When
        InventoryLog result = logService.findById(1);

        // Then
        assertEquals(log, result);
    }

    @Test void shouldNotFindById() {
        // Given
        when(logRepository.findById(1)).thenReturn(java.util.Optional.empty());

        // When
        InventoryLog result = logService.findById(1);

        // Then
        assertNull(result);
    }

    @Test void shouldFindByItemId() {
        // Given
        InventoryLog log = new InventoryLog();
        log.setItemId(1);
        when(logRepository.findByItemId(1)).thenReturn(List.of(log));

        // When
        List<InventoryLog> result = logService.findByItemId(1);

        // Then
        assertEquals(List.of(log), result);
    }

    @Test void shouldNotFindByItemId() {
        // Given
        when(logRepository.findByItemId(1)).thenReturn(List.of());

        // When
        List<InventoryLog> result = logService.findByItemId(1);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test void shouldFindByAuthorityId() {}

    @Test void shouldNotFindByAuthorityId() {}

    @Test void shouldFindByItemName() {}

    @Test void shouldNotFindByItemName() {}

    @Test void shouldFindByAuthorityEmail() {}

    @Test void shouldNotFindByAuthorityEmail() {}

    // === Add ===
    @Test void shouldAdd() {}

    @Test void shouldNotAddWhenInvalidAuthority() {}

    @Test void shouldNotAddWhenInvalidItem() {}

    @Test void shouldNotAddWhenReasonBlank() {}

    @Test void shouldNotAddWhenReasonTooLong() {}

    @Test void shouldNotAddWhenQuantityZero() {}

    @Test void shouldNotAddWhenTimestampMissing() {}

    @Test void shouldNotAddWhenDuplicate() {}

    @Test void shouldNotAddWhenInventoryFailsToUpdate() {}

    // === Update ===
    @Test void shouldUpdate() {}

    // === Delete ===
    @Test void shouldDeleteById() {}

    @Test void shouldNotDeleteById() {}

    // === Helpers ===
    private List<InventoryLog> makeLogs() { return null; }

    private List<Item> makeItems() { return null; }

    private AppUser makeAdmin() { return null; }
}