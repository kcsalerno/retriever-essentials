package re.api.data;

import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemJdbcTemplateRepositoryTest {
    private final int ITEM_COUNT = 37;

    @Autowired
    ItemJdbcTemplateRepository itemJdbcTemplateRepository;

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
         List<Item> items = itemJdbcTemplateRepository.findAll();
        // Assert
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertTrue(items.size() == ITEM_COUNT
                || items.size() == ITEM_COUNT + 1); // Tests may run out of order; if add is called, we may have one more item.
    }

    @Test
    void shouldFindById() {
        // Arrange
        int itemId = 1;
        // Act
        Item item = itemJdbcTemplateRepository.findById(itemId);
        // Assert
        assertNotNull(item);
        assertEquals(itemId, item.getItemId());
        assertEquals("Sona Masoori Rice", item.getItemName());
        assertEquals("South Asian - Staple", item.getCategory());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int itemId = 9999;
        // Act
        Item item = itemJdbcTemplateRepository.findById(itemId);
        // Assert
        assertNull(item);
    }

    @Test
    void shouldFindByName() {
        // Arrange
        String itemName = "Sona Masoori Rice";
        // Act
        Item item = itemJdbcTemplateRepository.findByName(itemName);
        // Assert
        assertNotNull(item);
        assertEquals(1, item.getItemId());
        assertEquals(itemName, item.getItemName());
        assertEquals("South Asian - Staple", item.getCategory());
    }

    @Test
    void shouldNotFindByBadName() {
        // Arrange
        String itemName = "Bad Name";
        // Act
        Item item = itemJdbcTemplateRepository.findByName(itemName);
        // Assert
        assertNull(item);
    }

    @Test
    void shouldFindByCategory() {
        // Arrange
        String category = "South Asian - Staple";
        // Act
        List<Item> items = itemJdbcTemplateRepository.findByCategory(category);
        // Assert
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());
    }

    @Test
    void shouldNotFindByBadCategory() {
        // Arrange
        String category = "Bad Category";
        // Act
        List<Item> items = itemJdbcTemplateRepository.findByCategory(category);
        // Assert
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void shouldAdd() {
        // Arrange
        Item testItem = new Item();
        testItem.setItemName("Test Item");
        testItem.setItemDescription("Test Description");
        testItem.setNutritionFacts("Test Nutrition Facts");
        testItem.setPicturePath("Test Picture Path");
        testItem.setCategory("Test Category");
        testItem.setCurrentCount(10);
        testItem.setItemLimit(5);
        testItem.setPricePerUnit(BigDecimal.valueOf(9.99));
        // Act
        Item addedItem = itemJdbcTemplateRepository.add(testItem);
        // Assert
        assertNotNull(addedItem);
        assertEquals(testItem.getItemName(), addedItem.getItemName());
        assertEquals(ITEM_COUNT + 1, addedItem.getItemId());
    }

    @Test
    void shouldUpdate() {
        // Arrange
        int itemId = 2;
        Item itemToUpdate = itemJdbcTemplateRepository.findById(itemId);
        assertNotNull(itemToUpdate);
        itemToUpdate.setItemDescription("Updated Description");
        // Act
        boolean updated = itemJdbcTemplateRepository.update(itemToUpdate);
        // Assert
        assertTrue(updated);
        assertEquals("Updated Description", itemJdbcTemplateRepository.findById(itemId).getItemDescription());
    }

    @Test
    void shouldNotUpdateBadId() {
        // Arrange
        Item itemToUpdate = new Item();
        itemToUpdate.setItemId(9999); // Non-existent ID
        itemToUpdate.setItemDescription("Updated Description");
        // Act
        boolean updated = itemJdbcTemplateRepository.update(itemToUpdate);
        // Assert
        assertFalse(updated);
    }

    @Test
    void shouldUpdateCurrentCount() {
        // Arrange
        int itemId = 2;
        int updateAmount = 5;
        Item itemToUpdate = itemJdbcTemplateRepository.findById(itemId);
        assertNotNull(itemToUpdate);
        int originalCount = itemToUpdate.getCurrentCount();
        // Act
        boolean updated = itemJdbcTemplateRepository.updateCurrentCount(itemId, updateAmount);
        // Assert
        assertTrue(updated);
        assertEquals(originalCount + updateAmount, itemJdbcTemplateRepository.findById(itemId).getCurrentCount());
    }

    @Test
    void shouldDisableById() {
        // Arrange
        int itemId = 3;
        Item itemToDisable = itemJdbcTemplateRepository.findById(itemId);
        assertNotNull(itemToDisable);
        assertTrue(itemToDisable.isEnabled());
        // Act
        boolean disabled = itemJdbcTemplateRepository.disableById(itemId);
        // Assert
        assertTrue(disabled);
        assertFalse(itemJdbcTemplateRepository.findById(itemId).isEnabled());
    }

    @Test
    void shouldNotDisableByBadId() {
        // Arrange
        int itemId = 9999;
        // Act
        boolean disabled = itemJdbcTemplateRepository.disableById(itemId);
        // Assert
        assertFalse(disabled);
    }
}