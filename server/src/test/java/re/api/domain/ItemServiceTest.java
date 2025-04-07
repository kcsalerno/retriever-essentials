package re.api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.ItemRepository;
import re.api.models.Item;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ItemServiceTest {

    @MockitoBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Test
    void shouldFindAll() {
        // Given
        List<Item> testItems = makeTestItems();
        // When
        when(itemRepository.findAll()).thenReturn(testItems);
        // Then
        List<Item> items = itemService.findAll();
        assertNotNull(items);
        assertEquals(3, items.size());
        assertEquals(testItems, items);
        assertEquals("Test Item 1", items.get(0).getItemName());
    }

    @Test
    void shouldFindById() {
        // Given
        List<Item> testItems = makeTestItems();
        // When
        when(itemRepository.findById(1)).thenReturn(testItems.get(0));
        // Then
        Item item = itemService.findById(1);
        assertNotNull(item);
        assertEquals(1, item.getItemId());
        assertEquals("Test Item 1", item.getItemName());
    }

    @Test
    void shouldNotFindById() {
        // Given
        List<Item> testItems = makeTestItems();
        // When
        when(itemRepository.findById(4)).thenReturn(null);
        // Then
        Item item = itemService.findById(4);
        assertNull(item);
    }

    @Test
    void shouldFindByName() {
        // Given
        List<Item> testItems = makeTestItems();
        // When
        when(itemRepository.findByName("Test Item 1")).thenReturn(testItems.get(0));
        // Then
        Item item = itemService.findByName("Test Item 1");
        assertNotNull(item);
        assertEquals(1, item.getItemId());
        assertEquals("Test Item 1", item.getItemName());
        assertEquals("Description 1", item.getItemDescription());
    }

    @Test
    void shouldNotFindByName() {
        // Given
        List<Item> testItems = makeTestItems();
        // When
        when(itemRepository.findByName("Nonexistent Item")).thenReturn(null);
        // Then
        Item item = itemService.findByName("Nonexistent Item");
        assertNull(item);
    }

    @Test
    void shouldAdd() {
        // Given
        Item newItem = new Item(0, "Test Item 4", "Description 4", "Nutrition Facts 4",
                "https://cloudinary.com/item4", "Category 4", 40, 20,
                BigDecimal.valueOf(39.99), true);
        // When
        when(itemRepository.add(newItem)).thenReturn(newItem);
        // Then
        Result<Item> result = itemService.add(newItem);
        assertTrue(result.isSuccess());
        assertEquals(newItem, result.getPayload());
    }

    @Test
    void shouldNotAddWithIdSet() {
        // Given
        Item newItem = new Item(4, "Test Item 4", "Description 4",
                "Nutrition Facts 4", "https://cloudinary.com/item4",
                "Category 4", 40, 2,
                BigDecimal.valueOf(39.99), true);
        // When
        when(itemRepository.add(newItem)).thenReturn(newItem);
        // Then
        Result<Item> result = itemService.add(newItem);
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Item ID cannot be set for `add` operation.", result.getMessages().get(0));
    }

    // Validations tests
    // null item
    // no item name, name less than 55
    // null picture path, less than 255
    // null category, less than 55
    // current count, less than 0
    // item limit, less than 1
    // price per unit, less than 0
    // price per unit, decimal, 2 digits
    // duplicates not allowed for add

    @Test
    void shouldUpdate() {
        // Given
        List<Item> testItems = makeTestItems();
        Item updatedItem = testItems.get(0);
        updatedItem.setItemName("Updated Item 1");
        // When
        when(itemRepository.update(updatedItem)).thenReturn(true);
        when(itemRepository.findById(1)).thenReturn(updatedItem);
        // Then
        Result<Item> result = itemService.update(updatedItem);
        assertTrue(result.isSuccess());
        assertEquals(updatedItem, result.getPayload());
    }

    @Test
    void shouldNotUpdateWithIdNotSet() {
        // Given
        Item updatedItem = new Item(0, "Updated Item 1", "Description 1", "Nutrition Facts 1",
                "https://cloudinary.com/item1", "Category 1", 10, 5,
                BigDecimal.valueOf(9.99), true);
        // When
        when(itemRepository.update(updatedItem)).thenReturn(true);
        // Then
        Result<Item> result = itemService.update(updatedItem);
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Item ID must be set for `update` operation.", result.getMessages().get(0));
    }

    @Test
    void shouldNotUpdateWithIdNotFound() {
        // Given
        Item updatedItem = new Item(4, "Updated Item 1", "Description 1", "Nutrition Facts 1",
                "https://cloudinary.com/item1", "Category 1", 10, 5,
                BigDecimal.valueOf(9.99), true);
        // When
        when(itemRepository.update(updatedItem)).thenReturn(false);
        // Then
        Result<Item> result = itemService.update(updatedItem);
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Item ID not found.", result.getMessages().get(0));
    }

    @Test
    void shouldDisableById() {
        // Given
        List<Item> testItems = makeTestItems();
        Item diasbledItem = testItems.get(0);
        diasbledItem.setEnabled(false);
        // When
        when(itemRepository.disableById(1)).thenReturn(true);
        when(itemRepository.findById(1)).thenReturn(diasbledItem);
        // Then
        Result<Item> result = itemService.disableById(1);
        assertTrue(result.isSuccess());
        Item item = itemService.findById(1);
        assertNotNull(item);
        assertFalse(item.isEnabled());
    }

    @Test
    void shouldNotDisableById() {
        // Given
        List<Item> testItems = makeTestItems();
        // When
        when(itemRepository.disableById(4)).thenReturn(false);
        // Then
        Result<Item> result = itemService.disableById(4);
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Item ID not found.", result.getMessages().get(0));
        assertEquals("NOT_FOUND", result.getType().name());
    }

    private List<Item> makeTestItems() {
        return List.of(
                new Item(1, "Test Item 1", "Description 1", "Nutrition Facts 1",
                        "https://cloudinary.com/item1", "Category 1", 10, 5,
                        BigDecimal.valueOf(9.99), true),
                new Item(2, "Test Item 2", "Description 2", "Nutrition Facts 2",
                        "https://cloudinary.com/item2", "Category 2", 20, 10,
                        BigDecimal.valueOf(19.99), true),
                new Item(3, "Test Item 3", "Description 3", "Nutrition Facts 3",
                        "https://cloudinary.com/item3", "Category 3", 30, 15,
                        BigDecimal.valueOf(29.99), true)
        );
    }
}