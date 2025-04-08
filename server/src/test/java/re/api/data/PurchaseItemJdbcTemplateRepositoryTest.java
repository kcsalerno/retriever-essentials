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
    private final int PURCHASE_ITEM_COUNT = 15;

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

    // (1, 1, 10), (1, 2, 20), (1, 4, 50), (1, 5, 30), (1, 6, 30),
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
        PurchaseItem testPurchaseItem = new PurchaseItem();
        testPurchaseItem.setPurchaseOrderId(2);
        testPurchaseItem.setItemId(24);
        testPurchaseItem.setQuantity(10);
        // Act
        PurchaseItem addedPurchaseItem = purchaseItemJdbcTemplateRepository.add(testPurchaseItem);
        // Assert
        assertNotNull(addedPurchaseItem);
        assertEquals(testPurchaseItem.getItemId(), addedPurchaseItem.getItemId());
        assertEquals(testPurchaseItem.getQuantity(), addedPurchaseItem.getQuantity());
        assertEquals(PURCHASE_ITEM_COUNT + 1, addedPurchaseItem.getPurchaseItemId());
    }

    @Test
    void shouldUpdate() {
        // Arrange
        int purchaseItemId = 2;
        int testQuantity = 30;
        PurchaseItem purchaseItemToUpdate = purchaseItemJdbcTemplateRepository.findById(purchaseItemId);
        assertNotNull(purchaseItemToUpdate);
        int initialQuantity = purchaseItemToUpdate.getQuantity();
        purchaseItemToUpdate.setQuantity(testQuantity);
        // Act
        boolean updated = purchaseItemJdbcTemplateRepository.update(purchaseItemToUpdate);
        // Assert
        assertTrue(updated);
        assertNotEquals(initialQuantity, purchaseItemJdbcTemplateRepository.findById(purchaseItemId).getQuantity());
        assertEquals(testQuantity, purchaseItemJdbcTemplateRepository.findById(purchaseItemId).getQuantity());
    }

    @Test
    void shouldNotUpdateBadId() {
        // Arrange
        PurchaseItem purchaseItemToUpdate = new PurchaseItem();
        purchaseItemToUpdate.setPurchaseItemId(9999);
        purchaseItemToUpdate.setQuantity(30);
        // Act
        boolean updated = purchaseItemJdbcTemplateRepository.update(purchaseItemToUpdate);
        // Assert
        assertFalse(updated);
    }

    @Test
    void shouldDeleteById() {
        // Arrange
        int purchaseItemId = 11;
        PurchaseItem purchaseItemToDelete = purchaseItemJdbcTemplateRepository.findById(purchaseItemId);
        assertNotNull(purchaseItemToDelete);
        int initialItemCountFromParentOrder = purchaseItemJdbcTemplateRepository.findByPurchaseOrderId(
                purchaseItemToDelete.getPurchaseOrderId()).size();
        // Act
        boolean deleted = purchaseItemJdbcTemplateRepository.deleteById(purchaseItemId);
        // Assert
        assertTrue(deleted);
        assertNull(purchaseItemJdbcTemplateRepository.findById(purchaseItemId));
        assertEquals(initialItemCountFromParentOrder - 1,
                purchaseItemJdbcTemplateRepository.findByPurchaseOrderId(
                        purchaseItemToDelete.getPurchaseOrderId()).size());
    }

    @Test
    void shouldNotDeleteByBadId() {
        // Arrange
        int purchaseItemId = 9999;
        // Act
        boolean deleted = purchaseItemJdbcTemplateRepository.deleteById(purchaseItemId);
        // Assert
        assertFalse(deleted);
    }

    @Test
    void shouldDeleteByPurchaseOrderId() {
        // Arrange
        int purchaseOrderId = 3;
        List<PurchaseItem> purchaseItemsToDelete = purchaseItemJdbcTemplateRepository.findByPurchaseOrderId(purchaseOrderId);
        assertNotNull(purchaseItemsToDelete);
        assertFalse(purchaseItemsToDelete.isEmpty());
        int initialItemCountFromParentOrder = purchaseItemJdbcTemplateRepository.findByPurchaseOrderId(
                purchaseOrderId).size();
        // Act
        boolean deleted = purchaseItemJdbcTemplateRepository.deleteByPurchaseOrderId(purchaseOrderId);
        // Assert
        assertTrue(deleted);
        for (PurchaseItem purchaseItem : purchaseItemsToDelete) {
            assertNull(purchaseItemJdbcTemplateRepository.findById(purchaseItem.getPurchaseItemId()));
        }
        assertEquals(0, purchaseItemJdbcTemplateRepository.findByPurchaseOrderId(purchaseOrderId).size());
    }
}