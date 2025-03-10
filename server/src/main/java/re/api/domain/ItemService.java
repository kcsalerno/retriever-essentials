package re.api.domain;

import org.springframework.stereotype.Service;
import re.api.data.ItemRepository;
import re.api.models.Item;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> findAll() {
        return repository.findAll();
    }

    public Item findById(int itemId) {
        return repository.findById(itemId);
    }

    public Item add(Item item) {
        return repository.add(item);
    }

    public boolean update(Item item) {
        return repository.update(item);
    }

    public boolean deleteById(int itemId) {
        return repository.deleteById(itemId);
    }

    public List<Item> findMostPopularItems() {
        return repository.findMostPopularItems();
    }
}