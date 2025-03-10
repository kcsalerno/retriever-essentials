package re.api.domain;

import org.springframework.stereotype.Service;
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

    public Result<Item> deleteById(int itemId) {
        Result<Item> result = new Result<>();

        if (!repository.deleteById(itemId)) {
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

        if (Validations.isNullOrBlank(item.getCategory())) {
            result.addMessage(ResultType.INVALID, "Category is required");
        } else if (item.getCategory().length() > 20) {
            result.addMessage(ResultType.INVALID, "Category must be 20 characters or fewer");
        }

        if (item.getCurrentCount() < 0) {
            result.addMessage(ResultType.INVALID, "Current count cannot be negative");
        }

        if (item.getPricePerUnit() != null && (item.getPricePerUnit().compareTo(BigDecimal.ZERO) < 0)) {
            result.addMessage(ResultType.INVALID, "Price per unit cannot be negative");
        }

        if (!Validations.isNullOrBlank(item.getPicturePath()) && !Validations.isValidUrl(item.getPicturePath())) {
            result.addMessage(ResultType.INVALID, "Picture path must be a valid URL");
        }

        List<Item> items = repository.findAll();
        for (Item existingItem : items) {
            if (existingItem.equals(item)) {
                result.addMessage(ResultType.DUPLICATE, "Duplicate item names are not allowed.");
                return result;
            }
        }

        return result;
    }
}