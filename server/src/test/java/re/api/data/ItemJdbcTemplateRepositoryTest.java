package re.api.data;

import org.springframework.boot.test.context.SpringBootTest;
import re.api.models.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
                || items.size() == ITEM_COUNT - 1); // Tests may run out of order; if delete is called, we may have one less item.
    }

    @Test
    void findById() {
    }

    @Test
    void findByName() {
    }

    @Test
    void add() {
    }

    @Test
    void update() {
    }

    @Test
    void disableById() {
    }
}