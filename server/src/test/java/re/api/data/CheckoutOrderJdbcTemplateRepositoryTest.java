package re.api.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.CheckoutOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckoutOrderJdbcTemplateRepositoryTest {
    private final int CHECKOUT_ORDER_COUNT = 10;

    @Autowired
    CheckoutOrderJdbcTemplateRepository checkoutOrderJdbcTemplateRepository;

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
        List<CheckoutOrder> checkoutOrders = checkoutOrderJdbcTemplateRepository.findAll();
        // Assert
        assertNotNull(checkoutOrders);
        assertFalse(checkoutOrders.isEmpty());
        assertTrue(checkoutOrders.size() == CHECKOUT_ORDER_COUNT
                || checkoutOrders.size() == CHECKOUT_ORDER_COUNT + 1
                || checkoutOrders.size() == CHECKOUT_ORDER_COUNT - 1);
    }

    // (checkout_id, student_id, authority_id, self_checkout, checkout_date)
    // (1, VF21042, 2, 0, 2025-04-06 21:51:54)
    @Test
    void shouldFindById() {
        // Arrange
        int checkoutOrderId = 1;
        // Act
        CheckoutOrder checkoutOrder = checkoutOrderJdbcTemplateRepository.findById(checkoutOrderId);
        // Assert
        assertNotNull(checkoutOrder);
        assertEquals(checkoutOrderId, checkoutOrder.getCheckoutOrderId());
        assertEquals("VF21042", checkoutOrder.getStudentId());
        assertEquals(2, checkoutOrder.getAuthorityId());
        assertFalse(checkoutOrder.isSelfCheckout());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int checkoutOrderId = 9999;
        // Act
        CheckoutOrder checkoutOrder = checkoutOrderJdbcTemplateRepository.findById(checkoutOrderId);
        // Assert
        assertNull(checkoutOrder);
    }

    @Test
    void findHourlyCheckoutSummary() {
        // Arrange
        // Act
        List<Map<String, Object>> summary = checkoutOrderJdbcTemplateRepository.findHourlyCheckoutSummary();
        // Assert
        assertNotNull(summary);
        assertFalse(summary.isEmpty());
        // 0 = Monday, 12, 2 (first row of summary)
        Map<String, Object> firstRow = summary.getFirst();
        assertEquals("Monday", firstRow.get("day"));
        assertEquals(12, ((Number) firstRow.get("hour")).intValue());
        assertEquals(2, ((Number) firstRow.get("total_checkouts")).intValue());
        // Summary only contains entries for Monday, Tuesday, Wednesday, and Friday (days open)
        assertTrue(summary.stream().noneMatch(row -> row.get("day").equals("Thursday")));
        assertTrue(summary.stream().noneMatch(row -> row.get("day").equals("Saturday")));
        assertTrue(summary.stream().noneMatch(row -> row.get("day").equals("Sunday")));
    }

    @Test
    void shouldAdd() {
        // Arrange
        CheckoutOrder checkoutOrder = new CheckoutOrder();
        checkoutOrder.setStudentId("VF99999");
        checkoutOrder.setAuthorityId(3);
        checkoutOrder.setSelfCheckout(true);
        checkoutOrder.setCheckoutDate(LocalDateTime.of(2025, 04, 04,12, 0, 0));
        // Act
        CheckoutOrder addedCheckoutOrder = checkoutOrderJdbcTemplateRepository.add(checkoutOrder);
        // Assert
        assertNotNull(addedCheckoutOrder);
        assertEquals(checkoutOrder.getStudentId(), addedCheckoutOrder.getStudentId());
        assertEquals(checkoutOrder.getAuthorityId(), addedCheckoutOrder.getAuthorityId());
        assertEquals(checkoutOrder.isSelfCheckout(), addedCheckoutOrder.isSelfCheckout());
        assertEquals(CHECKOUT_ORDER_COUNT + 1, addedCheckoutOrder.getCheckoutOrderId());
    }

    @Test
    void shouldUpdate() {
        // Arrange
        int checkoutOrderId = 7;
        CheckoutOrder checkoutOrderToUpdate = checkoutOrderJdbcTemplateRepository.findById(checkoutOrderId);
        assertNotNull(checkoutOrderToUpdate);
        checkoutOrderToUpdate.setStudentId("VF99999");
        // Act
        boolean updated = checkoutOrderJdbcTemplateRepository.update(checkoutOrderToUpdate);
        // Assert
        assertTrue(updated);
        assertEquals("VF99999", checkoutOrderJdbcTemplateRepository.findById(checkoutOrderId).getStudentId());
    }

    @Test
    void shouldNotUpdateBadId() {
        // Arrange
        CheckoutOrder checkoutOrderToUpdate = new CheckoutOrder();
        checkoutOrderToUpdate.setCheckoutOrderId(9999);
        checkoutOrderToUpdate.setStudentId("VF99999");
        // Act
        boolean updated = checkoutOrderJdbcTemplateRepository.update(checkoutOrderToUpdate);
        // Assert
        assertFalse(updated);
    }

    @Test
    void shouldDeleteById() {
        // Arrange
        int checkoutOrderId = 8;
        CheckoutOrder checkoutOrderToDelete = checkoutOrderJdbcTemplateRepository.findById(checkoutOrderId);
        assertNotNull(checkoutOrderToDelete);
        // Act
        boolean deleted = checkoutOrderJdbcTemplateRepository.deleteById(checkoutOrderId);
        // Assert
        assertTrue(deleted);
        assertNull(checkoutOrderJdbcTemplateRepository.findById(checkoutOrderId));
        assertTrue(CHECKOUT_ORDER_COUNT == checkoutOrderJdbcTemplateRepository.findAll().size()
                || CHECKOUT_ORDER_COUNT - 1 == checkoutOrderJdbcTemplateRepository.findAll().size());
    }

    @Test
    void shouldNotDeleteByBadId() {
        // Arrange
        int checkoutOrderId = 9999;
        // Act
        boolean deleted = checkoutOrderJdbcTemplateRepository.deleteById(checkoutOrderId);
        // Assert
        assertFalse(deleted);
    }
}