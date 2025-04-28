package re.api.data;

import re.api.models.Item;
import java.util.List;

public interface ItemRepository {
    List<Item> findAll();

    Item findById(int itemId);

    Item findByName(String name);

    List<Item> findByCategory(String category);

    Item add(Item item);

    boolean update(Item item);

    boolean updateCurrentCount(int itemId, int updateAmount);

    boolean disableById(int itemId);
}