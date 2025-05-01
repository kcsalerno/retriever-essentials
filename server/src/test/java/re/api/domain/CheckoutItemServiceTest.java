package re.api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.CheckoutItemRepository;
import re.api.data.CheckoutOrderRepository;
import re.api.data.ItemRepository;
import re.api.models.CheckoutItem;
import re.api.models.CheckoutOrder;
import re.api.models.Item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CheckoutItemServiceTest {

    @MockitoBean
    private CheckoutItemRepository checkoutItemRepository;
    @MockitoBean
    private CheckoutOrderRepository checkoutOrderRepository;
    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private CheckoutItemService checkoutItemService;

    @Test
    void shouldFindById() {
        // Given
        List<CheckoutItem> checkoutItems = makeCheckoutItems();
        // When
        when(checkoutItemRepository.findById(1)).thenReturn(checkoutItems.get(0));
        // Then
        CheckoutItem checkoutItem = checkoutItemService.findById(1);
        assertEquals(checkoutItems.getFirst(), checkoutItem);
        assertEquals(1, checkoutItem.getCheckoutItemId());
        assertEquals(1, checkoutItem.getCheckoutOrderId());
        assertEquals(3, checkoutItem.getItemId());
    }

    @Test
    void shouldNotFindById() {
        // Given
        List<CheckoutItem> checkoutItems = makeCheckoutItems();
        // When
        when(checkoutItemRepository.findById(99)).thenReturn(null);
        // Then
        CheckoutItem checkoutItem = checkoutItemService.findById(99);
        assertNull(checkoutItem);
    }

    @Test
    void shouldFindPopularItems() {
        Map<String, Object> mockItemStat = Map.of("item_id", 1, "count", 20);
        when(checkoutItemRepository.findPopularItems()).thenReturn(List.of(mockItemStat));

        List<Map<String, Object>> result = checkoutItemService.findPopularItems();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().get("item_id"));
        assertEquals(20, result.getFirst().get("count"));
    }

    @Test
    void shouldFindPopularCategories() {
        Map<String, Object> mockCategoryStat = Map.of("category", "Grains", "count", 40);
        when(checkoutItemRepository.findPopularCategories()).thenReturn(List.of(mockCategoryStat));

        List<Map<String, Object>> result = checkoutItemService.findPopularCategories();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Grains", result.getFirst().get("category"));
        assertEquals(40, result.getFirst().get("count"));
    }


    @Test
    void shouldUpdate() {
        CheckoutItem existing = new CheckoutItem(2, 1, 2, 2);
        CheckoutItem updated = new CheckoutItem(2, 1, 2, 4);

        when(checkoutItemRepository.findById(2)).thenReturn(existing);
        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        when(itemRepository.findById(2)).thenReturn(new Item(2, "Item", "",
                "", "", "", 100, 10, BigDecimal.TEN, true));
        when(itemRepository.updateCurrentCount(2, -2)).thenReturn(true);
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(List.of(existing));
        when(checkoutItemRepository.update(updated)).thenReturn(true);

        Result<CheckoutItem> result = checkoutItemService.update(updated);
        assertTrue(result.isSuccess());
        verify(itemRepository).updateCurrentCount(2, -2);
        verify(checkoutItemRepository).update(updated);
    }

    @Test
    void shouldNotUpdateWithNull() {
        Result<CheckoutItem> result = checkoutItemService.update(null);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout item cannot be null."));
    }

    @Test
    void shouldNotUpdateWhenIdMissing() {
        CheckoutItem badItem = new CheckoutItem(0, 1, 1, 1);
        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        when(itemRepository.findById(1)).thenReturn(new Item(1, "", "",
                "", "", "", 100, 5, BigDecimal.TEN, true));
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(List.of());
        Result<CheckoutItem> result = checkoutItemService.update(badItem);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout item ID must be set for update."));
    }

    @Test
    void shouldNotUpdateIfCheckoutOrderMissing() {
        CheckoutItem item = new CheckoutItem(2, 99, 1, 1);
        when(checkoutOrderRepository.findById(99)).thenReturn(null);
        Result<CheckoutItem> result = checkoutItemService.update(item);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout order not found."));
    }

    @Test
    void shouldNotUpdateWhenCheckoutOrderIdNotSet() {
        CheckoutItem item = new CheckoutItem(1, 0, 1, 2); // order ID is 0
        Result<CheckoutItem> result = checkoutItemService.update(item);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout order ID is required."));
    }


    @Test
    void shouldNotUpdateIfItemMissingOrDisabled() {
        CheckoutItem item = new CheckoutItem(2, 1, 999, 1);
        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        when(itemRepository.findById(999)).thenReturn(null);
        Result<CheckoutItem> result = checkoutItemService.update(item);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item does not exist or is disabled."));
    }

    @Test
    void shouldNotUpdateWhenItemIdNotSet() {
        CheckoutItem item = new CheckoutItem(1, 1, 0, 2); // item ID is 0
        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        Result<CheckoutItem> result = checkoutItemService.update(item);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item ID is required."));
    }

    @Test
    void shouldNotUpdateWhenQuantityIsZero() {
        CheckoutItem item = new CheckoutItem(1, 1, 1, 0);
        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        when(itemRepository.findById(1)).thenReturn(new Item(1, "Test", "", "", "", "", 10, 2, BigDecimal.ONE, true));
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(List.of());
        Result<CheckoutItem> result = checkoutItemService.update(item);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Quantity must be greater than 0."));
    }

    @Test
    void shouldNotUpdateIfExceedsStockOrLimit() {
        Item item = new Item(2, "", "", "", "",
                "", 5, 1, BigDecimal.TEN, true);
        CheckoutItem badItem = new CheckoutItem(2, 1, 2, 10);

        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        when(itemRepository.findById(2)).thenReturn(item);
        when(checkoutItemRepository.findById(2)).thenReturn(badItem);
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(List.of());

        Result<CheckoutItem> result = checkoutItemService.update(badItem);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Quantity exceeds available stock."));
        assertTrue(result.getMessages().contains("Quantity exceeds item limit."));
    }

    @Test
    void shouldNotUpdateIfInventoryFails() {
        CheckoutItem existing = new CheckoutItem(2, 1, 2, 5);
        CheckoutItem updated = new CheckoutItem(2, 1, 2, 8);

        when(checkoutItemRepository.findById(2)).thenReturn(existing);
        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        when(itemRepository.findById(2)).thenReturn(new Item(2, "", "",
                "", "", "", 100, 10, BigDecimal.TEN, true));
        when(itemRepository.updateCurrentCount(2, -3)).thenReturn(false); // fail here
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(List.of());

        Result<CheckoutItem> result = checkoutItemService.update(updated);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 2"));
    }

    @Test
    void shouldNotUpdateWhenDuplicateCheckoutItemExists() {
        CheckoutItem input = new CheckoutItem(1, 1, 1, 2);
        CheckoutItem duplicate = new CheckoutItem(99, 1, 1, 2); // same order and item ID, different ID

        when(checkoutOrderRepository.findById(1)).thenReturn(new CheckoutOrder());
        when(itemRepository.findById(1)).thenReturn(new Item(1, "Item", "", "", "", "", 10, 2, BigDecimal.ONE, true));
        when(checkoutItemRepository.findByCheckoutOrderId(1)).thenReturn(List.of(duplicate));
        when(checkoutItemRepository.findById(1)).thenReturn(input);

        Result<CheckoutItem> result = checkoutItemService.update(input);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Duplicate checkout item found."));
    }

    @Test
    void shouldDeleteById() {
        CheckoutItem toDelete = new CheckoutItem(3, 1, 3, 5);
        when(checkoutItemRepository.findById(3)).thenReturn(toDelete);
        when(itemRepository.updateCurrentCount(3, 5)).thenReturn(true);
        when(checkoutItemRepository.deleteById(3)).thenReturn(true);

        Result<CheckoutItem> result = checkoutItemService.deleteById(3);
        assertTrue(result.isSuccess());
        verify(itemRepository).updateCurrentCount(3, 5);
        verify(checkoutItemRepository).deleteById(3);
    }

    @Test
    void shouldNotDeleteIfNotFound() {
        when(checkoutItemRepository.findById(999)).thenReturn(null);
        Result<CheckoutItem> result = checkoutItemService.deleteById(999);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout item ID not found."));
    }

    @Test
    void shouldNotDeleteIfInventoryUpdateFails() {
        CheckoutItem existing = new CheckoutItem(3, 1, 3, 5);
        when(checkoutItemRepository.findById(3)).thenReturn(existing);
        when(itemRepository.updateCurrentCount(3, 5)).thenReturn(false); // fails here
        Result<CheckoutItem> result = checkoutItemService.deleteById(3);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 3"));
    }

    private List<CheckoutItem> makeCheckoutItems() {
        return List.of(
                new CheckoutItem(1, 1, 3, 2),
                new CheckoutItem(2, 1, 3, 3),
                new CheckoutItem(3, 2, 3, 4),
                new CheckoutItem(4, 2, 2, 5)
        );
    }
}