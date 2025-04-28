package re.api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.ItemRepository;
import re.api.data.PurchaseItemRepository;
import re.api.data.PurchaseOrderRepository;
import re.api.models.Item;
import re.api.models.PurchaseItem;
import re.api.models.PurchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class PurchaseItemServiceTest {

    @MockitoBean
    private PurchaseItemRepository purchaseItemRepository;
    @MockitoBean
    private ItemRepository itemRepository;
    @MockitoBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseItemService purchaseItemService;

    @Test
    void shouldFindById() {
        // Given
        List<PurchaseItem> purchaseItems = makePurchaseItems();
        // When
        when(purchaseItemRepository.findById(1)).thenReturn(purchaseItems.getFirst());
        // Then
        PurchaseItem purchaseItem = purchaseItemService.findById(1);
        assertNotNull(purchaseItem);
        assertEquals(1, purchaseItem.getPurchaseItemId());
        assertEquals(1, purchaseItem.getPurchaseOrderId());
        assertEquals(1, purchaseItem.getItemId());
        assertEquals(10, purchaseItem.getQuantity());
    }

    @Test
    void shouldNotFindById() {
        // Given
        List<PurchaseItem> purchaseItems = makePurchaseItems();
        // When
        when(purchaseItemRepository.findById(3)).thenReturn(null);
        // Then
        PurchaseItem purchaseItem = purchaseItemService.findById(3);
        assertNull(purchaseItem);
    }

    @Test
    void shouldUpdate() {
        // Given
        List<PurchaseItem> purchaseItems = makePurchaseItems();
        PurchaseItem updatedPurchaseItem = purchaseItems.get(1);
        updatedPurchaseItem.setQuantity(15);
        // When
        when(purchaseItemRepository.findById(2)).thenReturn(purchaseItems.get(1));
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder(1, 1, 1, LocalDateTime.now()));
        when(itemRepository.findById(2)).thenReturn(new Item(2, "Test Item", "desc", "nutri", "https://link", "cat", 10, 2, BigDecimal.TEN, true));
        when(itemRepository.updateCurrentCount(2, 10)).thenReturn(true); // 15-5 = 10
        when(purchaseItemRepository.findByPurchaseOrderId(1)).thenReturn(List.of(purchaseItems.get(1)));
        when(purchaseItemRepository.update(updatedPurchaseItem)).thenReturn(true);
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(updatedPurchaseItem);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getPayload().getPurchaseItemId());
    }

    @Test
    void shouldNotUpdateWhenPurchaseItemIsNull() {
        // Given
        PurchaseItem purchaseItem = null;
        // When
        when(purchaseItemRepository.update(purchaseItem)).thenReturn(true);
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Purchase item cannot be null.", result.getMessages().getFirst());
    }

    @Test
    void shouldNotUpdateWhenPurchaseOrderIdNotSet() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(2, 0, 1, 10); // purchaseOrderId is 0
        // When
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Purchase order ID must be set."));
    }

    @Test
    void shouldNotUpdateWhenPurchaseOrderNotFound() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(2, 999, 1, 10); // some non-existing purchase order ID
        // When
        when(purchaseOrderRepository.findById(999)).thenReturn(null);
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Purchase order not found."));
    }

    @Test
    void shouldNotUpdateWithIdNotSet() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(0, 1, 1, 10);
        // When
        when(purchaseItemRepository.findById(0)).thenReturn(null);
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder(1, 1, 1, LocalDateTime.now()));
        when(itemRepository.findById(1)).thenReturn(new Item(1, "Item1", "desc", "nutri", "http://pic", "cat", 10, 2, BigDecimal.TEN, true));
        when(purchaseItemRepository.findByPurchaseOrderId(1)).thenReturn(List.of());
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertTrue(result.getMessages().contains("Purchase item ID must be set for `update` operation."));
    }

    @Test
    void shouldNotUpdateWhenItemIdNotSet() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(2, 1, 0, 10); // itemId is 0
        // When
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder(1, 1, 1, LocalDateTime.now()));
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item ID must be set."));
    }

    @Test
    void shouldNotUpdateWhenItemNotFoundOrDisabled() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(2, 1, 999, 10);
        // When
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder(1, 1, 1, LocalDateTime.now()));
        when(itemRepository.findById(999)).thenReturn(null); // simulate missing item
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item does not exist or is disabled."));
    }

    @Test
    void shouldNotUpdateWhenUpdateCurrentCountFails() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(2, 1, 1, 15); // new quantity = 15
        PurchaseItem existingItem = new PurchaseItem(2, 1, 1, 10); // old quantity = 10
        int quantityChange = 5; // 15 - 10
        // When
        when(purchaseItemRepository.findById(2)).thenReturn(existingItem);
        when(itemRepository.updateCurrentCount(1, quantityChange)).thenReturn(false); // fail inventory update
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder());
        when(itemRepository.findById(1)).thenReturn(new Item(1, "Test Item", "desc", "nutri", "https://link", "cat", 10, 2, BigDecimal.TEN, true));
        when(purchaseItemRepository.findByPurchaseOrderId(1)).thenReturn(List.of(existingItem));
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 1"));
    }

    @Test
    void shouldNotUpdateWithIdNotFound() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(4, 1, 1, 10);
        // When
        when(purchaseItemRepository.findById(4)).thenReturn(new PurchaseItem(4, 1, 1, 10)); // simulate it exists
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder(1, 1, 1, LocalDateTime.now()));
        when(itemRepository.findById(1)).thenReturn(new Item(1, "Item1", "desc", "nutri", "https://pic", "cat", 10, 2, BigDecimal.TEN, true));
        when(purchaseItemRepository.findByPurchaseOrderId(1)).thenReturn(List.of());
        when(itemRepository.updateCurrentCount(1, 0)).thenReturn(true);
        when(purchaseItemRepository.update(purchaseItem)).thenReturn(false); // fail the actual update
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertTrue(result.getMessages().contains("Purchase item not found."));
    }

    @Test
    void shouldNotUpdateWithNegativeItemCount() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(2, 1, 2, -2);
        // When:
        when(purchaseItemRepository.findById(2)).thenReturn(new PurchaseItem(2, 1, 2, 5)); // existing item
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder(1, 1, 1, LocalDateTime.now()));
        when(itemRepository.findById(2)).thenReturn(new Item(2, "Item2", "desc", "nutri", "https://pic", "cat", 10, 2, BigDecimal.TEN, true));
        when(purchaseItemRepository.findByPurchaseOrderId(1)).thenReturn(List.of());
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertTrue(result.getMessages().contains("Quantity must be greater than zero."));
    }

    @Test
    void shouldNotUpdateWhenDuplicatePurchaseItemExists() {
        // Given
        PurchaseItem purchaseItem = new PurchaseItem(2, 1, 1, 10); // trying to update
        PurchaseItem existingDuplicate = new PurchaseItem(99, 1, 1, 10); // same order, same item, different ID (duplicate)
        // When
        when(purchaseOrderRepository.findById(1)).thenReturn(new PurchaseOrder(1, 1, 1, LocalDateTime.now()));
        when(itemRepository.findById(1)).thenReturn(new Item(1, "Item", "desc", "nutri", "https://url", "cat", 10, 2, BigDecimal.TEN, true));
        when(purchaseItemRepository.findById(2)).thenReturn(purchaseItem); // find the current
        when(purchaseItemRepository.findByPurchaseOrderId(1)).thenReturn(List.of(existingDuplicate)); // show existing duplicate
        // Then
        Result<PurchaseItem> result = purchaseItemService.update(purchaseItem);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Duplicate purchase item detected."));
    }

    @Test
    void shouldDeleteById() {
        // Given
        List<PurchaseItem> purchaseItems = makePurchaseItems();
        PurchaseItem deletedPurchaseItem = purchaseItems.get(2);
        // When
        when(purchaseItemRepository.findById(3)).thenReturn(deletedPurchaseItem);
        when(itemRepository.updateCurrentCount(deletedPurchaseItem.getItemId(), -deletedPurchaseItem.getQuantity())).thenReturn(true);
        when(purchaseItemRepository.deleteById(3)).thenReturn(true);
        // Then
        Result<PurchaseItem> result = purchaseItemService.deleteById(3);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteById() {
        // Given
        List<PurchaseItem> purchaseItems = makePurchaseItems();
        PurchaseItem deletedPurchaseItem = purchaseItems.get(2);

        // Needed mocks
        when(purchaseItemRepository.findById(3)).thenReturn(deletedPurchaseItem);
        when(itemRepository.updateCurrentCount(3, -20)).thenReturn(true); // simulate inventory update succeeding
        when(purchaseItemRepository.deleteById(3)).thenReturn(false);

        // When
        Result<PurchaseItem> result = purchaseItemService.deleteById(3);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Purchase item ID not found.", result.getMessages().getFirst());
    }

    @Test
    void shouldNotDeleteWhenInventoryUpdateFails() {
        // Given
        PurchaseItem existing = new PurchaseItem(1, 1, 1, 10);
        // When
        when(purchaseItemRepository.findById(1)).thenReturn(existing);
        when(itemRepository.updateCurrentCount(1, -10)).thenReturn(false); // simulate inventory update failing
        // Then
        Result<PurchaseItem> result = purchaseItemService.deleteById(1);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 1"));
    }

    private List<PurchaseItem> makePurchaseItems() {
        return List.of(
                new PurchaseItem(1, 1, 1, 10),
                new PurchaseItem(2, 1, 2, 5),
                new PurchaseItem(3, 1, 3, 20)
        );
    }
}