package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.ItemRepository;
import re.api.models.Item;

import java.math.BigDecimal;
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

    public Item findByName(String name){
        return repository.findByName(name);
    }

    @Transactional
    public Result<Item> add(Item item) {
        Result<Item> result = validate(item);

        if (!result.isSuccess()) {
            return result;
        }
        if (item.getItemId() != 0) {
            result.addMessage(ResultType.INVALID, "Item ID cannot be set for `add` operation.");
        }
        if (result.isSuccess()) {
            item = repository.add(item);
            result.setPayload(item);
        }

        return result;
    }

    @Transactional
    public Result<Item> update(Item item) {
        Result<Item> result = validate(item);

        if (!result.isSuccess()) {
            return result;
        }
        if (item.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID must be set for `update` operation.");
        }
        if (result.isSuccess()) {
            if (repository.update(item)) {
                result.setPayload(item);
            } else {
                result.addMessage(ResultType.NOT_FOUND, "Item ID not found.");
            }
        }

        return result;
    }

    @Transactional
    public Result<Item> disableById(int itemId) {
        Result<Item> result = new Result<>();

        if (!repository.disableById(itemId)) {
            result.addMessage(ResultType.NOT_FOUND, "Item ID not found.");
        }

        return result;
    }

    private Result<Item> validate(Item item) {
        Result<Item> result = new Result<>();

        if (item == null) {
            result.addMessage(ResultType.INVALID, "Item cannot be null.");
            return result;
        }

        if (Validations.isNullOrBlank(item.getItemName())) {
            result.addMessage(ResultType.INVALID, "Item name is required");
        } else if (item.getItemName().length() > 20) {
            result.addMessage(ResultType.INVALID, "Item name must be 20 characters or fewer");
        }

        if (!Validations.isNullOrBlank(item.getPicturePath()) && !Validations.isValidUrl(item.getPicturePath())) {
            result.addMessage(ResultType.INVALID, "Picture path must be a valid URL");
        }

        if (Validations.isNullOrBlank(item.getCategory())) {
            result.addMessage(ResultType.INVALID, "Category is required");
        } else if (item.getCategory().length() > 20) {
            result.addMessage(ResultType.INVALID, "Category must be 20 characters or fewer");
        }

        if (item.getCurrentCount() < 0) {
            result.addMessage(ResultType.INVALID, "Current count cannot be negative");
        }

        if (item.getItemLimit() < 1) {
            result.addMessage(ResultType.INVALID, "Item limit must be greater than or equal to 1");
        }

        // Better null safety check
        if (item.getPricePerUnit() != null) {
            if (item.getPricePerUnit().compareTo(BigDecimal.ZERO) < 0) {
                result.addMessage(ResultType.INVALID, "Price per unit cannot be negative");
            } else if (item.getPricePerUnit().scale() > 2) {
                result.addMessage(ResultType.INVALID, "Price per unit cannot have more than 2 decimal places");
            }
        }

        List<Item> existingItems = repository.findAll();
        for (Item existingItem : existingItems) {
            if (existingItem.equals(item)
                    && existingItem.getItemId() != item.getItemId()) {
                result.addMessage(ResultType.DUPLICATE, "Duplicate items are not allowed.");
                return result;
            }
        }

        return result;
    }
}