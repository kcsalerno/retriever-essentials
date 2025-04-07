package re.api.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.PurchaseItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PurchaseItemJdbcTemplateRepositoryTest {
    private final int PURCHASE_ITEM_COUNT = 10;

    @Autowired
    PurchaseItemJdbcTemplateRepository purchaseItemJdbcTemplateRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    //  (1, 1, 10)
    @Test
    void shouldFindById() {
        // Arrange
        int purchaseItemId = 1;
        int purchaseOrderId = 1;
        int itemId = 1;
        int quantity = 10;
        // Act
        PurchaseItem purchaseItem = purchaseItemJdbcTemplateRepository.findById(purchaseItemId);
        // Assert
        assertNotNull(purchaseItem);
        assertEquals(purchaseItemId, purchaseItem.getPurchaseItemId());
        assertEquals(purchaseOrderId, purchaseItem.getPurchaseOrderId());
        assertEquals(itemId, purchaseItem.getItemId());
        assertEquals(quantity, purchaseItem.getQuantity());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int purchaseItemId = 9999;
        // Act
        PurchaseItem purchaseItem = purchaseItemJdbcTemplateRepository.findById(purchaseItemId);
        // Assert
        assertNull(purchaseItem);
    }

    //-- Purchase Orders (admin_id = 1, vendor_id = 1 = Patel Brothers)
    //    INSERT INTO purchase_order (admin_id, vendor_id) VALUES
    //        (1, 1),
    //        (1, 1);
    //
    //    -- Purchase Items
    //    INSERT INTO purchase_item (purchase_id, item_id, quantity) VALUES
    //        (1, 1, 10), (1, 2, 20), (1, 4, 50), (1, 5, 30), (1, 6, 30),
    //        (2, 23, 5), (2, 27, 10), (2, 29, 60), (2, 32, 20), (2, 36, 10);

    @Test
    void shouldFindByPurchaseOrderId() {
        // Arrange
        int purchaseOrderId = 1;
        // Act
        List<PurchaseItem> purchaseItems = purchaseItemJdbcTemplateRepository.findByPurchaseOrderId(purchaseOrderId);
        // Assert
        assertNotNull(purchaseItems);
        assertFalse(purchaseItems.isEmpty());
        assertEquals(5, purchaseItems.size());
    }

    @Test
    void shouldNotFindByBadPurchaseOrderId() {
        // Arrange
        int purchaseOrderId = 9999;
        // Act
        List<PurchaseItem> purchaseItems = purchaseItemJdbcTemplateRepository.findByPurchaseOrderId(purchaseOrderId);
        // Assert
        assertNotNull(purchaseItems);
        assertTrue(purchaseItems.isEmpty());
    }

    @Test
    void shouldAdd() {
        // Arrange
        PurchaseItem purchaseItem = new PurchaseItem();
        purchaseItem.setPurchaseOrderId(2);
        purchaseItem.setItemId(24);
        purchaseItem.setQuantity(10);
        // Act
        PurchaseItem addedPurchaseItem = purchaseItemJdbcTemplateRepository.add(purchaseItem);
        // Assert
        assertNotNull(addedPurchaseItem);
        assertEquals(PURCHASE_ITEM_COUNT + 1, addedPurchaseItem.getPurchaseItemId());
        assertEquals(purchaseItem.getItemId(), addedPurchaseItem.getItemId());
        assertEquals(purchaseItem.getQuantity(), addedPurchaseItem.getQuantity());
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void deleteByPurchaseOrderId() {
    }
}