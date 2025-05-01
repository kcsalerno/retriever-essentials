package re.api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.AppUserRepository;
import re.api.data.CheckoutItemRepository;
import re.api.data.CheckoutOrderRepository;
import re.api.data.ItemRepository;
import re.api.models.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CheckoutOrderServiceTest {

    @MockitoBean
    private CheckoutOrderRepository checkoutOrderRepository;
    @MockitoBean
    private CheckoutItemRepository checkoutItemRepository;
    @MockitoBean
    private ItemRepository itemRepository;
    @MockitoBean
    private AppUserRepository appUserRepository;

    @Autowired
    private CheckoutOrderService checkoutOrderService;

    @Test
    void shouldFindAll() {
        // Given
        List<CheckoutOrder> orders = makeCheckoutOrders();
        List<Item> items = makeItems();
        AppUser admin = makeAdmin();
        // When
        when(checkoutOrderRepository.findAll()).thenReturn(orders);
        when(checkoutItemRepository.findByCheckoutOrderId(anyInt()))
                .thenAnswer(invocation -> {
                    int orderId = invocation.getArgument(0);
                    return orders.stream()
                            .filter(o -> o.getCheckoutOrderId() == orderId)
                            .findFirst()
                            .map(CheckoutOrder::getCheckoutItems)
                            .orElse(List.of());
                });
        when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                    int itemId = invocation.getArgument(0);
                    return makeItems().stream()
                            .filter(i -> i.getItemId() == itemId)
                            .findFirst()
                            .orElse(null);
                });
        when(appUserRepository.findById(1)).thenReturn(admin);
        // Then
        List<CheckoutOrder> result = checkoutOrderService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("A123456789", result.getFirst().getStudentId());
        assertEquals("Rice", result.getFirst().getCheckoutItems().getFirst().getItem().getItemName());
        assertEquals("admin@school.edu", result.getFirst().getAuthority().getUsername());
    }

    @Test
    void shouldFindById() {
        // Given
        CheckoutOrder order = makeCheckoutOrders().getFirst();
        List<Item> items = makeItems();
        AppUser admin = makeAdmin();
        // When
        when(checkoutOrderRepository.findById(1)).thenReturn(order);
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(order.getCheckoutItems());
        when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> {
                    int itemId = invocation.getArgument(0);
                    return items.stream()
                            .filter(i -> i.getItemId() == itemId)
                            .findFirst()
                            .orElse(null);
                });
        when(appUserRepository.findById(1)).thenReturn(admin);
        //Then
        CheckoutOrder result = checkoutOrderService.findById(1);
        assertNotNull(result);
        assertEquals("A123456789", result.getStudentId());
        assertEquals(2, result.getCheckoutItems().size());
        assertEquals("admin@school.edu", result.getAuthority().getUsername());
    }

    @Test
    void shouldNotFindById() {
        // Given
        // When
        when(checkoutOrderRepository.findById(99)).thenReturn(null);
        // Then
        CheckoutOrder result = checkoutOrderService.findById(99);
        assertNull(result);
    }

    @Test
    void shouldFindHourlyCheckoutSummary() {
        // Given
        List<Map<String, Object>> hourlySummary = List.of(
                Map.of("hour", 10, "checkout_count", 5),
                Map.of("hour", 14, "checkout_count", 3)
        );
        // When
        when(checkoutOrderRepository.findHourlyCheckoutSummary()).thenReturn(hourlySummary);
        List<Map<String, Object>> result = checkoutOrderService.findHourlyCheckoutSummary();
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).get("hour"));
        assertEquals(5, result.get(0).get("checkout_count"));
        assertEquals(14, result.get(1).get("hour"));
        assertEquals(3, result.get(1).get("checkout_count"));
    }


    @Test
    void shouldAdd() {
        // Given
        CheckoutOrder newOrder = new CheckoutOrder(0, "Z123456789", 1,
                false, LocalDateTime.now());
        List<Item> items = makeItems();
        List<CheckoutItem> checkoutItems = List.of(
                new CheckoutItem(0, 0, 1, 2), // Rice (Item 1)
                new CheckoutItem(0, 0, 2, 1)  // Beans (Item 2)
        );
        newOrder.setCheckoutItems(checkoutItems);
        // When
        CheckoutOrder savedOrder = new CheckoutOrder(4, "Z123456789", 1,
                false, newOrder.getCheckoutDate());
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(items.get(0));
        when(itemRepository.findById(2)).thenReturn(items.get(1));
        when(checkoutOrderRepository.add(newOrder)).thenReturn(savedOrder);
        when(itemRepository.updateCurrentCount(1, -2)).thenReturn(true);
        when(itemRepository.updateCurrentCount(2, -1)).thenReturn(true);
        when(checkoutItemRepository.findByCheckoutOrderId(0)).thenReturn(List.of()); // No conflicts
        when(checkoutItemRepository.add(any())).thenReturn(null); // Don't care about return
        Result<CheckoutOrder> result = checkoutOrderService.add(newOrder);
        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
        assertEquals(4, result.getPayload().getCheckoutOrderId());
    }

    @Test
    void shouldNotAddWhenCheckoutOrderIsNull() {
        Result<CheckoutOrder> result = checkoutOrderService.add(null);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertTrue(result.getMessages().contains("Checkout order cannot be null."));
    }

    @Test
    void shouldNotAddWhenStudentIdIsMissing() {
        CheckoutOrder order = new CheckoutOrder(0, "  ", 1, false, LocalDateTime.now());
        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Student ID is required."));
    }

    @Test
    void shouldNotAddWhenStudentIdTooLong() {
        CheckoutOrder order = new CheckoutOrder(0, "TOOLONGSTUDENTID", 1, false, LocalDateTime.now());
        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Student ID cannot exceed 10 characters."));
    }

    @Test
    void shouldNotAddWhenAuthorityIdIsMissing() {
        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 0, false, LocalDateTime.now());
        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Authority ID is required."));
    }

    @Test
    void shouldNotAddWhenAuthorityIsNotFound() {
        when(appUserRepository.findById(999)).thenReturn(null);
        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 999, false, LocalDateTime.now());
        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Authority does not exist or is disabled."));
    }

    @Test
    void shouldNotAddWhenAuthorityInvalidOrDisabled() {
        CheckoutOrder order = makeCheckoutOrders().getFirst();
        order.setAuthorityId(0); // invalid

        Result<CheckoutOrder> result1 = checkoutOrderService.add(order);
        assertFalse(result1.isSuccess());
        assertTrue(result1.getMessages().contains("Authority ID is required."));

        order.setAuthorityId(999);
        when(appUserRepository.findById(999)).thenReturn(new AppUser(999, "x",
                "Disabled", UserRole.ADMIN, false));
        Result<CheckoutOrder> result2 = checkoutOrderService.add(order);
        assertFalse(result2.isSuccess());
        assertTrue(result2.getMessages().contains("Authority does not exist or is disabled."));
    }

    @Test
    void shouldNotAddWhenCheckoutDateIsMissing() {
        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 1, false, null);
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout date is required."));
    }

    @Test
    void shouldNotAddWhenDuplicateItemsInOrder() {
        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 1, false, LocalDateTime.now());
        CheckoutItem item1 = new CheckoutItem(0, 0, 1, 1);
        CheckoutItem item2 = new CheckoutItem(0, 0, 1, 2); // same itemId
        order.setCheckoutItems(List.of(item1, item2));

        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));

        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().stream().anyMatch(msg -> msg.contains("Duplicate item")));
    }

    @Test
    void shouldNotAddWhenItemQuantityIsZero() {
        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 1, false, LocalDateTime.now());
        CheckoutItem item = new CheckoutItem(0, 0, 1, 0); // invalid quantity
        order.setCheckoutItems(List.of(item));

        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(makeItems().get(0));

        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().stream().anyMatch(m -> m.contains("greater than 0")));
    }

    @Test
    void shouldNotAddWhenQuantityExceedsStock() {
        Item item = new Item(1, "Rice", "", "", "", "", 1, 2, BigDecimal.ZERO, true);
        CheckoutItem checkoutItem = new CheckoutItem(0, 0, 1, 5); // 5 > currentCount

        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 1, false, LocalDateTime.now());
        order.setCheckoutItems(List.of(checkoutItem));

        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(item);

        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().stream().anyMatch(m -> m.contains("exceeds available stock")));
    }

    @Test
    void shouldNotAddWhenQuantityExceedsItemLimit() {
        Item item = new Item(1, "Rice", "", "", "", "", 100, 1, BigDecimal.ZERO, true);
        CheckoutItem checkoutItem = new CheckoutItem(0, 0, 1, 5); // 5 > itemLimit

        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 1, false, LocalDateTime.now());
        order.setCheckoutItems(List.of(checkoutItem));

        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(1)).thenReturn(item);

        Result<CheckoutOrder> result = checkoutOrderService.add(order);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().stream().anyMatch(m -> m.contains("exceeds limit")));
    }

    @Test
    void shouldNotAddWithInvalidCheckoutItems() {
        CheckoutOrder order = new CheckoutOrder(0, "S123456789", 1, false, LocalDateTime.now());

        CheckoutItem invalidItem = new CheckoutItem(0, 0, 0, -1); // missing itemId, bad quantity
        order.setCheckoutItems(List.of(invalidItem, invalidItem)); // duplicate

        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(itemRepository.findById(0)).thenReturn(null);
        when(itemRepository.findById(1)).thenReturn(makeItems().getFirst());
        when(checkoutItemRepository.findByCheckoutOrderId(0)).thenReturn(List.of());

        Result<CheckoutOrder> result = checkoutOrderService.add(order);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item ID is required."));
        assertTrue(result.getMessages().stream().anyMatch(msg -> msg.startsWith("Duplicate item")));
    }

    @Test
    void shouldNotAddWhenCheckoutOrderIdIsPreset() {
        CheckoutOrder order = new CheckoutOrder(999, "A123456789", 1, false, LocalDateTime.now());
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());

        Result<CheckoutOrder> result = checkoutOrderService.add(order);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout order ID cannot be set for `add` operation."));
    }

    @Test
    void shouldNotUpdateWhenCheckoutOrderIdIsMissing() {
        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 1, false, LocalDateTime.now());
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());

        Result<CheckoutOrder> result = checkoutOrderService.update(order);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout order ID must be set for update."));
    }

    @Test
    void shouldNotAddWhenCheckoutItemIsNull() {
        CheckoutOrder order = new CheckoutOrder(0, "A123456789", 1, false, LocalDateTime.now());
        order.setCheckoutItems(Collections.singletonList((CheckoutItem) null));

        when(appUserRepository.findById(1)).thenReturn(makeAdmin());

        Result<CheckoutOrder> result = checkoutOrderService.add(order);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout item cannot be null."));
    }

    @Test
    void shouldUpdate() {
        // Given
        CheckoutOrder orderToUpdate = new CheckoutOrder(2, "B987654321",
                1, true, LocalDateTime.now().minusDays(1));
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(checkoutOrderRepository.update(orderToUpdate)).thenReturn(true);
        Result<CheckoutOrder> result = checkoutOrderService.update(orderToUpdate);
        // Then
        assertTrue(result.isSuccess());
        assertEquals(2, result.getPayload().getCheckoutOrderId());
    }

    @Test
    void shouldDeleteById() {
        // Given
        CheckoutOrder existingOrder = makeCheckoutOrders().get(0); // ID = 1
        List<CheckoutItem> itemsToRestore = existingOrder.getCheckoutItems();
        when(checkoutOrderRepository.findById(1)).thenReturn(existingOrder);
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(itemsToRestore);
        when(itemRepository.updateCurrentCount(1, 1)).thenReturn(true); // Rice
        when(itemRepository.updateCurrentCount(2, 1)).thenReturn(true); // Beans
        when(checkoutItemRepository.deleteByCheckoutOrderId(1)).thenReturn(true);
        when(checkoutOrderRepository.deleteById(1)).thenReturn(true);
        // When
        Result<CheckoutOrder> result = checkoutOrderService.deleteById(1);
        // Then
        assertTrue(result.isSuccess());
        verify(itemRepository).updateCurrentCount(1, 1);
        verify(itemRepository).updateCurrentCount(2, 1);
        verify(checkoutItemRepository).deleteByCheckoutOrderId(1);
        verify(checkoutOrderRepository).deleteById(1);
    }

    @Test
    void shouldNotDeleteById() {
        // Given
        when(checkoutOrderRepository.findById(999)).thenReturn(null);
        // When
        Result<CheckoutOrder> result = checkoutOrderService.deleteById(999);
        // Then
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertTrue(result.getMessages().contains("Checkout order ID not found."));
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

    private List<CheckoutOrder> makeCheckoutOrders() {
        CheckoutOrder order1 = new CheckoutOrder(1, "A123456789", 1,
                false, LocalDateTime.now().minusDays(2));
        order1.setCheckoutItems(List.of(
                new CheckoutItem(1, 1, 1, 1), // Rice
                new CheckoutItem(2, 1, 2, 1)  // Beans
        ));

        CheckoutOrder order2 = new CheckoutOrder(2, "B987654321", 1,
                true, LocalDateTime.now().minusDays(1));
        order2.setCheckoutItems(List.of(
                new CheckoutItem(3, 2, 1, 2)
        ));

        CheckoutOrder order3 = new CheckoutOrder(3, "C246810121", 1,
                true, LocalDateTime.now());
        order3.setCheckoutItems(List.of(
                new CheckoutItem(4, 3, 2, 1)
        ));

        return List.of(order1, order2, order3);
    }
}