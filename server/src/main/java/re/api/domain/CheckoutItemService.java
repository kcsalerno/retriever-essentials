package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.CheckoutItemRepository;
import re.api.data.CheckoutOrderRepository;
import re.api.data.ItemRepository;
import re.api.models.CheckoutItem;
import re.api.models.CheckoutOrder;
import re.api.models.Item;

import java.util.List;
import java.util.Map;

@Service
public class CheckoutItemService {

    private final CheckoutItemRepository checkoutItemRepository;
    private final CheckoutOrderRepository checkoutOrderRepository;
    private final ItemRepository itemRepository;

    public CheckoutItemService(CheckoutItemRepository checkoutItemRepository,
                                CheckoutOrderRepository checkoutOrderRepository,
                                ItemRepository itemRepository) {
        this.checkoutItemRepository = checkoutItemRepository;
        this.checkoutOrderRepository = checkoutOrderRepository;
        this.itemRepository = itemRepository;
    }

    public CheckoutItem findById(int checkoutItemId) {
        return checkoutItemRepository.findById(checkoutItemId);
    }

    public List<Map<String, Object>> findPopularItems() {
        return checkoutItemRepository.findPopularItems();
    }

    public List<Map<String, Object>> findPopularCategories() {
        return checkoutItemRepository.findPopularCategories();
    }

    @Transactional
    public Result<CheckoutItem> update(CheckoutItem checkoutItem) {
        Result<CheckoutItem> result = validate(checkoutItem);

        if (!result.isSuccess()) {
            return result;
        }

        if (checkoutItem.getCheckoutItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Checkout item ID must be set for update.");
            return result;
        }

        // Add functionality to update the item count in the inventory
        CheckoutItem existing = checkoutItemRepository.findById(checkoutItem.getCheckoutItemId());
        if (existing == null) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout item ID not found.");
            return result;
        }

        // Quantity change needs to be negative, opposite of purchase
        int quantityChange = checkoutItem.getQuantity() - existing.getQuantity();
        if (quantityChange != 0) {
            boolean updatedCount = itemRepository.updateCurrentCount(checkoutItem.getItemId(), -quantityChange);
            if (!updatedCount) {
                result.addMessage(ResultType.INVALID, "Failed to update item count for item ID: " + checkoutItem.getItemId());
                return result;
            }
        }

        if (!checkoutItemRepository.update(checkoutItem)) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout item not found.");
        } else {
            result.setPayload(checkoutItem);
        }

        return result;
    }

    @Transactional
    public Result<CheckoutItem> deleteById(int checkoutItemId) {
        Result<CheckoutItem> result = new Result<>();

        // Add the functionality to update the item count in the inventory
        CheckoutItem existing = checkoutItemRepository.findById(checkoutItemId);
        if (existing == null) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout item ID not found.");
            return result;
        }

        // I think this should be to add, not subtract
        int quantityToAdd = existing.getQuantity();
        boolean updatedInventory = itemRepository.updateCurrentCount(existing.getItemId(), quantityToAdd);
        if (!updatedInventory) {
            result.addMessage(ResultType.INVALID, "Failed to update item count for item ID: " + existing.getItemId());
            return result;
        }

        if (!checkoutItemRepository.deleteById(checkoutItemId)) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout item ID not found.");
        }

        return result;
    }

    private Result<CheckoutItem> validate(CheckoutItem checkoutItem) {
        Result<CheckoutItem> result = new Result<>();

        if (checkoutItem == null) {
            result.addMessage(ResultType.INVALID, "Checkout item cannot be null.");
            return result;
        }

        CheckoutOrder checkoutOrder = null;
        if (checkoutItem.getCheckoutOrderId() <= 0) {
            result.addMessage(ResultType.INVALID, "Checkout order ID is required.");
        } else {
            checkoutOrder = checkoutOrderRepository.findById(checkoutItem.getCheckoutOrderId());
            if (checkoutOrder == null) {
                result.addMessage(ResultType.NOT_FOUND, "Checkout order not found.");
            }
        }

        Item item = null;
        if (checkoutItem.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID is required.");
        } else {
            item = itemRepository.findById(checkoutItem.getItemId());
            if (item == null || !item.isEnabled()) {
                result.addMessage(ResultType.NOT_FOUND, "Item does not exist or is disabled.");
            }
        }

        if (checkoutItem.getQuantity() <= 0) {
            result.addMessage(ResultType.INVALID, "Quantity must be greater than 0.");
        } else if (item != null) {
            if (checkoutItem.getQuantity() > item.getCurrentCount()) {
                result.addMessage(ResultType.INVALID, "Quantity exceeds available stock.");
            }
            if (checkoutItem.getQuantity() > item.getItemLimit()) {
                result.addMessage(ResultType.INVALID, "Quantity exceeds item limit.");
            }
        }

        List<CheckoutItem> existingItems = checkoutItemRepository.findByCheckoutOrderId(checkoutItem.getCheckoutOrderId());
        for (CheckoutItem existingItem : existingItems) {
            if (existingItem.equals(checkoutItem)
                    && existingItem.getCheckoutItemId() != checkoutItem.getCheckoutItemId()) {
                result.addMessage(ResultType.INVALID, "Duplicate checkout item found.");
                break;
            }
        }

        return result;
    }
}
