package re.api.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.PurchaseOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PurchaseOrderJdbcTemplateRepositoryTest {
    private final int PURCHASE_ORDER_COUNT = 3;

    @Autowired
    PurchaseOrderJdbcTemplateRepository purchaseOrderJdbcTemplateRepository;

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
        List<PurchaseOrder> purchaseOrders = purchaseOrderJdbcTemplateRepository.findAll();
        // Assert
        assertNotNull(purchaseOrders);
        assertFalse(purchaseOrders.isEmpty());
        assertTrue(purchaseOrders.size() == PURCHASE_ORDER_COUNT
                || purchaseOrders.size() == PURCHASE_ORDER_COUNT + 1
                || purchaseOrders.size() == PURCHASE_ORDER_COUNT - 1);
    }

    // (1, 1)
    @Test
    void shouldFindById() {
        // Arrange
        int purchaseOrderId = 1;
        // Act
        PurchaseOrder purchaseOrder = purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId);
        // Assert
        assertNotNull(purchaseOrder);
        assertEquals(purchaseOrderId, purchaseOrder.getPurchaseId());
        assertEquals(1, purchaseOrder.getAdminId());
        assertEquals(1, purchaseOrder.getVendorId());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int purchaseOrderId = 9999;
        // Act
        PurchaseOrder purchaseOrder = purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId);
        // Assert
        assertNull(purchaseOrder);
    }

    @Test
    void shouldAdd() {
        // Arrange
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setAdminId(1);
        purchaseOrder.setVendorId(2);
        purchaseOrder.setPurchaseDate(LocalDateTime.now().minusDays(7));
        // Act
        purchaseOrder = purchaseOrderJdbcTemplateRepository.add(purchaseOrder);
        // Assert
        assertNotNull(purchaseOrder);
        assertEquals(1, purchaseOrder.getAdminId());
        assertEquals(2, purchaseOrder.getVendorId());
        assertEquals(PURCHASE_ORDER_COUNT + 1, purchaseOrder.getPurchaseId());
    }

    // (1, 1)
    @Test
    void shouldUpdate() {
        // Arrange
        int purchaseOrderId = 2;
        PurchaseOrder purchaseOrder = purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId);
        assertNotNull(purchaseOrder);
        LocalDateTime testTime = LocalDateTime.now();
        purchaseOrder.setPurchaseDate(testTime);
        // Act
        boolean updated = purchaseOrderJdbcTemplateRepository.update(purchaseOrder);
        // Assert
        assertTrue(updated);
        assertEquals(testTime.getYear(),
                purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId).getPurchaseDate().getYear());
        assertEquals(testTime.getMonth(),
                purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId).getPurchaseDate().getMonth());
        assertEquals(testTime.getDayOfMonth(),
                purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId).getPurchaseDate().getDayOfMonth());
        assertEquals(testTime.getHour(),
                purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId).getPurchaseDate().getHour());
        assertEquals(testTime.getMinute(),
                purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId).getPurchaseDate().getMinute());
    }

    @Test
    void shouldNotUpdateBadId() {
        // Arrange
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseId(9999);
        purchaseOrder.setAdminId(1);
        purchaseOrder.setVendorId(2);
        // Act
        boolean updated = purchaseOrderJdbcTemplateRepository.update(purchaseOrder);
        // Assert
        assertFalse(updated);
    }

    @Test
    void shouldDeleteById() {
        // Arrange
        int purchaseOrderId = 3;
        PurchaseOrder purchaseOrder = purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId);
        assertNotNull(purchaseOrder);
        // Act
        boolean deleted = purchaseOrderJdbcTemplateRepository.deleteById(purchaseOrderId);
        // Assert
        assertTrue(deleted);
        assertNull(purchaseOrderJdbcTemplateRepository.findById(purchaseOrderId));
        assertTrue(PURCHASE_ORDER_COUNT == purchaseOrderJdbcTemplateRepository.findAll().size()
                || PURCHASE_ORDER_COUNT - 1 == purchaseOrderJdbcTemplateRepository.findAll().size());
    }

    @Test
    void shouldNotDeleteByBadId() {
        // Arrange
        int purchaseOrderId = 9999;
        // Act
        boolean deleted = purchaseOrderJdbcTemplateRepository.deleteById(purchaseOrderId);
        // Assert
        assertFalse(deleted);
    }
}