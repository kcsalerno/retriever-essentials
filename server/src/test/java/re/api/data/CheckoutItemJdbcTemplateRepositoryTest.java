package re.api.data;

    import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.CheckoutItem;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckoutItemJdbcTemplateRepositoryTest {
    private final int CHECKOUT_ITEM_COUNT = 19;

    @Autowired
    CheckoutItemJdbcTemplateRepository checkoutItemJdbcTemplateRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    // Tuple for find methods: (1, 1, 1, 2)
    @Test
    void shouldFindById() {
        // Arrange
        int checkoutItemId = 1;
        int checkoutOrderId = 1;
        int itemId = 1;
        // Act
        CheckoutItem checkoutItem = checkoutItemJdbcTemplateRepository.findById(checkoutItemId);
        // Assert
        assertNotNull(checkoutItem);
        assertEquals(checkoutItemId, checkoutItem.getCheckoutItemId());
        assertEquals(checkoutOrderId, checkoutItem.getCheckoutOrderId());
        assertEquals(itemId, checkoutItem.getItemId());
        assertEquals(2, checkoutItem.getQuantity());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int checkoutItemId = 9999;
        // Act
        CheckoutItem checkoutItem = checkoutItemJdbcTemplateRepository.findById(checkoutItemId);
        // Assert
        assertNull(checkoutItem);
    }

    // # checkout_item_id, checkout_id, item_id, quantity
    // '1', '1', '1', '2'
    // '2', '1', '4', '1'
    @Test
    void shouldFindByCheckoutOrderId() {
        // Arrange
        int checkoutOrderId = 1;
        // Act
        List<CheckoutItem> checkoutItems = checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(checkoutOrderId);
        // Assert
        assertNotNull(checkoutItems);
        assertFalse(checkoutItems.isEmpty());
        assertEquals(2, checkoutItems.size());
    }

    @Test
    void shouldNotFindByBadCheckoutOrderId() {
        // Arrange
        int checkoutOrderId = 9999;
        // Act
        List<CheckoutItem> checkoutItems = checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(checkoutOrderId);
        // Assert
        assertNotNull(checkoutItems);
        assertTrue(checkoutItems.isEmpty());
    }

    // Top Result: ('Sona Masoori Rice', '3')
    @Test
    void findPopularItems() {
        // Arrange
        // Act
        List<Map<String, Object>> popularItems = checkoutItemJdbcTemplateRepository.findPopularItems();
        // Assert
        assertNotNull(popularItems);
        assertFalse(popularItems.isEmpty());
        assertEquals(5, popularItems.size());
        assertEquals("Toor Dahl (Red Lentils)", popularItems.getFirst().get("item_name"));
    }

    // Top Result: ('South Asian - Snack', '14')
    @Test
    void findPopularCategories() {
        // Arrange
        // Act
        List<Map<String, Object>> popularCategories = checkoutItemJdbcTemplateRepository.findPopularCategories();
        // Assert
        assertNotNull(popularCategories);
        assertFalse(popularCategories.isEmpty());
        assertEquals(5, popularCategories.size());
        assertEquals("South Asian - Snack", popularCategories.getFirst().get("category"));
    }

    @Test
    void shouldAdd() {
        // Arrange
        int checkoutOrderId = 4;
        final int initialItemCount = checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(checkoutOrderId).size();
        CheckoutItem checkoutItem = new CheckoutItem();
        checkoutItem.setCheckoutOrderId(checkoutOrderId);
        checkoutItem.setItemId(24);
        checkoutItem.setQuantity(1);
        // Act
        CheckoutItem addedCheckoutItem = checkoutItemJdbcTemplateRepository.add(checkoutItem);
        // Assert
        assertNotNull(addedCheckoutItem);
        assertEquals(initialItemCount + 1,
                checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(checkoutOrderId).size());
        assertEquals(CHECKOUT_ITEM_COUNT + 1, addedCheckoutItem.getCheckoutItemId());
    }

    @Test
    void shouldUpdate() {
        // Arrange
        int checkoutItemId = 2;
        CheckoutItem checkoutItemToUpdate = checkoutItemJdbcTemplateRepository.findById(checkoutItemId);
        assertNotNull(checkoutItemToUpdate);
        int initialQuantity = checkoutItemToUpdate.getQuantity();
        checkoutItemToUpdate.setQuantity(3);
        // Act
        boolean updated = checkoutItemJdbcTemplateRepository.update(checkoutItemToUpdate);
        // Assert
        assertTrue(updated);
        assertNotEquals(initialQuantity, checkoutItemJdbcTemplateRepository.findById(checkoutItemId).getQuantity());
        assertEquals(3, checkoutItemJdbcTemplateRepository.findById(checkoutItemId).getQuantity());
    }

    @Test
    void shouldNotUpdateBadId() {
        // Arrange
        CheckoutItem checkoutItemToUpdate = new CheckoutItem();
        checkoutItemToUpdate.setCheckoutItemId(9999);
        checkoutItemToUpdate.setQuantity(3);
        // Act
        boolean updated = checkoutItemJdbcTemplateRepository.update(checkoutItemToUpdate);
        // Assert
        assertFalse(updated);
    }

    @Test
    void shouldDeleteById() {
        // Arrange
        int checkoutItemId = 15;
        CheckoutItem checkoutItemToDelete = checkoutItemJdbcTemplateRepository.findById(checkoutItemId);
        assertNotNull(checkoutItemToDelete);
        int initialItemCountFromParentOrder = checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(
                checkoutItemToDelete.getCheckoutOrderId()).size();
        // Act
        boolean deleted = checkoutItemJdbcTemplateRepository.deleteById(checkoutItemId);
        // Assert
        assertTrue(deleted);
        assertNull(checkoutItemJdbcTemplateRepository.findById(checkoutItemId));
        assertEquals(initialItemCountFromParentOrder - 1,
                checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(
                        checkoutItemToDelete.getCheckoutOrderId()).size());
    }

    @Test
    void shouldNotDeleteByBadId() {
        // Arrange
        int checkoutItemId = 9999;
        // Act
        boolean deleted = checkoutItemJdbcTemplateRepository.deleteById(checkoutItemId);
        // Assert
        assertFalse(deleted);
    }

    @Test
    void shouldDeleteByCheckoutOrderId() {
        // Arrange
        int checkoutOrderId = 9;
        List<CheckoutItem> checkoutItemsToDelete = checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(
                checkoutOrderId);
        assertNotNull(checkoutItemsToDelete);
        int initialItemCount = checkoutItemsToDelete.size();
        // Act
        boolean deleted = checkoutItemJdbcTemplateRepository.deleteByCheckoutOrderId(checkoutOrderId);
        // Assert
        assertTrue(deleted);
        List<CheckoutItem> checkoutItems = checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(checkoutOrderId);
        int finalItemCount = checkoutItems.size();
        assertNotEquals(initialItemCount, finalItemCount);
        assertEquals(0, finalItemCount);
    }

    @Test
    void shouldNotDeleteByBadCheckoutOrderId() {
        // Arrange
        int checkoutOrderId = 9999;
        // Act
        boolean deleted = checkoutItemJdbcTemplateRepository.deleteByCheckoutOrderId(checkoutOrderId);
        // Assert
        assertFalse(deleted);
    }
}