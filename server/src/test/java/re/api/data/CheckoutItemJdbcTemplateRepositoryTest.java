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

    @Test
    void findPopularItems() {
        // Arrange
        // Act
        List<Map<String, Object>> popularItems = checkoutItemJdbcTemplateRepository.findPopularItems();
        // Assert
        assertNotNull(popularItems);
        assertFalse(popularItems.isEmpty());
        assertEquals(5, popularItems.size());
    }

    @Test
    void findPopularCategories() {
        // Arrange
        // Act
        List<Map<String, Object>> popularCategories = checkoutItemJdbcTemplateRepository.findPopularCategories();
        // Assert
        assertNotNull(popularCategories);
        assertFalse(popularCategories.isEmpty());
        assertEquals(5, popularCategories.size());
    }

    @Test
    void shouldAdd() {
        // Arrange
        CheckoutItem checkoutItem = new CheckoutItem();
        checkoutItem.setCheckoutOrderId(1);
        checkoutItem.setItemId(24);
        checkoutItem.setQuantity(1);
        // Act
        CheckoutItem addedCheckoutItem = checkoutItemJdbcTemplateRepository.add(checkoutItem);
        // Assert
        assertNotNull(addedCheckoutItem);
        assertEquals(3, checkoutItemJdbcTemplateRepository.findByCheckoutOrderId(1).size());

    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void deleteByCheckoutOrderId() {
    }
}