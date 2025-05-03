package re.api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.*;
import re.api.models.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PurchaseOrderServiceTest {

    @MockitoBean
    private PurchaseOrderRepository purchaseOrderRepository;
    @MockitoBean
    private PurchaseItemRepository purchaseItemRepository;
    @MockitoBean
    private AppUserRepository appUserRepository;
    @MockitoBean
    private VendorRepository vendorRepository;
    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Test
    void shouldFindAll() {
        // Given
        List<PurchaseOrder> purchaseOrders = makePurchaseOrders();
        List<PurchaseItem> purchaseItems = makePurchaseItems();
        List<Item> items = makeItems();
        AppUser admin = makeAdmin();
        Vendor vendor = makeVendor();
        // When
        when(purchaseOrderRepository.findAll()).thenReturn(purchaseOrders);
        when(purchaseItemRepository.findByPurchaseOrderId(anyInt()))
                .thenAnswer(invocation -> purchaseItems.stream()
                        .filter(pi -> pi.getPurchaseOrderId() == invocation.getArgument(0, Integer.class))
                        .toList());
        when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> items.stream()
                        .filter(i -> i.getItemId() == invocation.getArgument(0, Integer.class))
                        .findFirst().orElse(null));
        when(appUserRepository.findById(1)).thenReturn(admin);
        when(vendorRepository.findById(1)).thenReturn(vendor);
        // Then
        List<PurchaseOrder> result = purchaseOrderService.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Rice", result.getFirst().getPurchaseItems().getFirst().getItem().getItemName());
        assertEquals("UMBC Dining", result.getFirst().getVendor().getVendorName());
    }

    @Test
    void shouldFindById() {
        // Given
        PurchaseOrder order = makePurchaseOrders().getFirst();
        List<Item> items = makeItems();
        AppUser admin = makeAdmin();
        Vendor vendor = makeVendor();
        // When
        when(purchaseOrderRepository.findById(1)).thenReturn(order);
        when(purchaseItemRepository.findByPurchaseOrderId(1)).thenReturn(order.getPurchaseItems());
        when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> items.stream()
                        .filter(i -> i.getItemId() == invocation.getArgument(0, Integer.class))
                        .findFirst().orElse(null));
        when(appUserRepository.findById(1)).thenReturn(admin);
        when(vendorRepository.findById(1)).thenReturn(vendor);
        // Then
        PurchaseOrder result = purchaseOrderService.findById(1);
        assertNotNull(result);
        assertEquals(1, result.getPurchaseId());
        assertEquals("admin@school.edu", result.getAdmin().getUsername());
    }

    @Test
    void shouldNotFindById() {
        // Given
        // When
        when(purchaseOrderRepository.findById(999)).thenReturn(null);
        // Then
        PurchaseOrder result = purchaseOrderService.findById(999);
        assertNull(result);
    }

    @Test
    void shouldAdd() {
        // Given
        PurchaseOrder newOrder = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        List<PurchaseItem> items = makePurchaseItems().subList(0, 2); // Only items for orderId 1
        List<Item> itemObjects = makeItems();
        AppUser admin = makeAdmin();
        Vendor vendor = makeVendor();
        newOrder.setPurchaseItems(items);
        //When
        when(appUserRepository.findById(1)).thenReturn(admin);
        when(vendorRepository.findById(1)).thenReturn(vendor);
        when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> itemObjects.stream()
                        .filter(i -> i.getItemId() == (invocation.getArgument(0, Integer.class)))
                        .findFirst().orElse(null));
        when(itemRepository.updateCurrentCount(anyInt(), anyInt())).thenReturn(true);
        when(purchaseOrderRepository.add(newOrder)).thenReturn(new PurchaseOrder(10, 1, 1, newOrder.getPurchaseDate()));
        when(purchaseItemRepository.add(any())).thenReturn(null); // Don't care about return value here
        // Then
        Result<PurchaseOrder> result = purchaseOrderService.add(newOrder);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(10, result.getPayload().getPurchaseId());
    }

    @Test
    void shouldNotAddWhenAdminOrVendorInvalid() {
        PurchaseOrder newOrder = new PurchaseOrder(0, -1, -1, LocalDateTime.now());

        // Simulate missing admin and vendor
        when(appUserRepository.findById(-1)).thenReturn(null);
        when(vendorRepository.findById(-1)).thenReturn(null);

        Result<PurchaseOrder> result = purchaseOrderService.add(newOrder);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Admin ID is required.")
                || result.getMessages().contains("Vendor ID is required.")
                || result.getMessages().contains("Admin ID does not exist or is disabled.")
                || result.getMessages().contains("Vendor ID does not exist."));
    }

    @Test
    void shouldNotAddWhenAdminIsDisabled() {
        // Given
        AppUser disabledAdmin = new AppUser(1, "admin@school.edu", "password", UserRole.ADMIN, false);
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(disabledAdmin);
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Admin ID does not exist or is disabled."));
    }

    @Test
    void shouldNotAddWhenAdminIsMissing() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(null);
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Admin ID does not exist or is disabled."));
    }


    @Test
    void shouldNotAddWhenIdIsAlreadySet() {
        // Given
        PurchaseOrder newOrder = new PurchaseOrder(99, 1, 1, LocalDateTime.now());
        AppUser admin = makeAdmin();
        Vendor vendor = makeVendor();
        // When
        when(appUserRepository.findById(1)).thenReturn(admin);
        when(vendorRepository.findById(1)).thenReturn(vendor);
        Result<PurchaseOrder> result = purchaseOrderService.add(newOrder);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Purchase ID cannot be set for add operation."));
    }

    @Test
    void shouldNotAddWhenItemNotFoundOrDisabled() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        order.setPurchaseItems(List.of(new PurchaseItem(0, 0, 999, 10))); // invalid item ID
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        when(itemRepository.findById(999)).thenReturn(null); // item missing
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item ID 999 not found or disabled."));
    }

    @Test
    void shouldNotAddWhenQuantityInvalid() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        order.setPurchaseItems(List.of(
                new PurchaseItem(0, 0, 1, 0)
        ));
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        when(itemRepository.findById(1)).thenReturn(makeItems().getFirst());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Quantity must be greater than zero."));
    }

    @Test
    void shouldNotAddWhenInventoryUpdateFails() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        order.setPurchaseItems(List.of(
                new PurchaseItem(0, 0, 1, 5)
        ));
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        when(itemRepository.findById(1)).thenReturn(makeItems().getFirst());
        when(purchaseOrderRepository.add(order)).thenReturn(new PurchaseOrder(10, 1, 1, order.getPurchaseDate()));
        when(purchaseItemRepository.add(any())).thenReturn(null); // Don't care about return value here
        when(itemRepository.updateCurrentCount(1, 5)).thenReturn(false); // simulate failure
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Failed to update item count for item ID: 1"));
    }

    @Test
    void shouldNotAddWhenPurchaseOrderIsNull() {
        // Given
        // When
        // Then
        Result<PurchaseOrder> result = purchaseOrderService.add(null);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Purchase order cannot be null."));
    }

    @Test
    void shouldNotAddWhenPurchaseDateMissing() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, null);
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Purchase date is required."));
    }

    @Test
    void shouldNotAddWhenPurchaseItemIsNull() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        order.setPurchaseItems(Collections.singletonList((PurchaseItem) null));
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Checkout item cannot be null."));
    }

    @Test
    void shouldNotAddWhenAdminNotFound() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        // When
        when(appUserRepository.findById(1)).thenReturn(null); // missing
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Admin ID does not exist or is disabled."));
    }

    @Test
    void shouldNotAddWhenItemIdIsInvalid() {
        // Given
        PurchaseItem invalidItem = new PurchaseItem(0, 0, 0, 10); // itemId = 0
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        order.setPurchaseItems(List.of(invalidItem));
        // When
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Item ID is required."));
    }

    @Test
    void shouldNotAddWhenDuplicateItemsInOrder() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 1, LocalDateTime.now());
        order.setPurchaseItems(List.of(
                new PurchaseItem(0, 0, 1, 5),
                new PurchaseItem(0, 0, 1, 10) // duplicate itemId
        ));
        // Then
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(1)).thenReturn(makeVendor());
        when(itemRepository.findById(1)).thenReturn(makeItems().getFirst());
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().stream().anyMatch(msg -> msg.contains("Duplicate item in purchase order")));
    }

    @Test
    void shouldUpdate() {
        // Given
        PurchaseOrder order = makePurchaseOrders().get(1); // ID = 2
        order.setPurchaseDate(LocalDateTime.now());
        List<Item> items = makeItems();
        AppUser admin = makeAdmin();
        Vendor vendor = makeVendor();
        // When
        when(purchaseOrderRepository.update(order)).thenReturn(true);
        when(purchaseItemRepository.findByPurchaseOrderId(order.getPurchaseId())).thenReturn(order.getPurchaseItems());
        when(itemRepository.findById(anyInt()))
                .thenAnswer(invocation -> items.stream()
                        .filter(i -> i.getItemId() == invocation.getArgument(0, Integer.class))
                        .findFirst().orElse(null));
        when(appUserRepository.findById(1)).thenReturn(admin);
        when(vendorRepository.findById(1)).thenReturn(vendor);
        // Then
        Result<PurchaseOrder> result = purchaseOrderService.update(order);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotAddWhenVendorMissing() {
        // Given
        PurchaseOrder order = new PurchaseOrder(0, 1, 999, LocalDateTime.now());
        when(appUserRepository.findById(1)).thenReturn(makeAdmin());
        when(vendorRepository.findById(999)).thenReturn(null);
        // When
        Result<PurchaseOrder> result = purchaseOrderService.add(order);
        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessages().contains("Vendor ID does not exist."));
    }

    @Test
    void shouldDeleteById() {
        // Given
        PurchaseOrder existingOrder = makePurchaseOrders().get(1); // ID = 2
        List<PurchaseItem> itemsToRestore = existingOrder.getPurchaseItems();
        // When
        when(purchaseOrderRepository.findById(2)).thenReturn(existingOrder);
        when(purchaseItemRepository.findByPurchaseOrderId(2)).thenReturn(itemsToRestore);
        when(itemRepository.updateCurrentCount(1, -20)).thenReturn(true); // Rice
        when(purchaseItemRepository.deleteByPurchaseOrderId(2)).thenReturn(true);
        when(purchaseOrderRepository.deleteById(2)).thenReturn(true);
        Result<PurchaseOrder> result = purchaseOrderService.deleteById(2);
        // Then
        assertTrue(result.isSuccess());
        verify(itemRepository).updateCurrentCount(1, -20); // match the actual service call
        verify(purchaseItemRepository).deleteByPurchaseOrderId(2);
        verify(purchaseOrderRepository).deleteById(2);
    }

    @Test
    void shouldNotDeleteById() {
        // Given
        int purchaseId = 999;
        // When
        when(purchaseOrderRepository.deleteById(purchaseId)).thenReturn(false);
        // Then
        Result<PurchaseOrder> result = purchaseOrderService.deleteById(purchaseId);
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    private List<PurchaseOrder> makePurchaseOrders() {
        PurchaseOrder order1 = new PurchaseOrder(1, 1, 1, LocalDateTime.now().minusDays(3));
        order1.setPurchaseItems(List.of(
                new PurchaseItem(1, 1, 1, 10),
                new PurchaseItem(2, 1, 2, 5)
        ));

        PurchaseOrder order2 = new PurchaseOrder(2, 1, 1, LocalDateTime.now().minusDays(2));
        order2.setPurchaseItems(List.of(
                new PurchaseItem(3, 2, 1, 20)
        ));

        PurchaseOrder order3 = new PurchaseOrder(3, 1, 1, LocalDateTime.now().minusDays(1));
        order3.setPurchaseItems(List.of(
                new PurchaseItem(4, 3, 2, 10)
        ));

        return List.of(order1, order2, order3);
    }

    private List<PurchaseItem> makePurchaseItems() {
        return List.of(
                new PurchaseItem(1, 1, 1, 10),
                new PurchaseItem(2, 1, 2, 5),
                new PurchaseItem(3, 2, 1, 20),
                new PurchaseItem(4, 3, 2, 10)
        );
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

    private AppUser makeAdmin() {
        return new AppUser(1, "admin@school.edu", "password",
                UserRole.ADMIN, true);
    }

    private Vendor makeVendor() {
        return new Vendor(1, "UMBC Dining", "999-999-9999",
                "https://link", true);
    }
}