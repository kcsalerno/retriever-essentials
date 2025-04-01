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

    // Not used here, used by CheckoutOrderService to enrich CheckoutOrder with CheckoutItems
//    public List<CheckoutItem> findByCheckoutOrderId(int checkoutOrderId) {
//        return repository.findByCheckoutOrderId(checkoutOrderId);
//    }

    public List<Map<String, Object>> findPopularItems() {
        return checkoutItemRepository.findPopularItems();
    }

    public List<Map<String, Object>> findPopularCategories() {
        return checkoutItemRepository.findPopularCategories();
    }

    // Add handled by CheckoutOrderService

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

        // Add checks for whether the checkout order and item exist
        if (checkoutItem.getCheckoutOrderId() <= 0) {
            result.addMessage(ResultType.INVALID, "Checkout order ID is required.");
        } else {
            CheckoutOrder checkoutOrder = checkoutOrderRepository.findById(checkoutItem.getCheckoutOrderId());
            if (checkoutOrder == null) {
                result.addMessage(ResultType.NOT_FOUND, "Checkout order not found.");
            }
        }

        if (checkoutItem.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID is required.");
        } else {
            Item item = itemRepository.findById(checkoutItem.getItemId());
            if (item == null) {
                result.addMessage(ResultType.NOT_FOUND, "Item not found.");
            }
        }

        // Is this null safe? Does it short circuit on the 'item != null' check?
        if (checkoutItem.getQuantity() <= 0) {
            result.addMessage(ResultType.INVALID, "Quantity must be greater than 0.");
        } else {
            Item item = itemRepository.findById(checkoutItem.getItemId());
            if (item != null && checkoutItem.getQuantity() > item.getCurrentCount()) {
                result.addMessage(ResultType.INVALID, "Quantity exceeds available stock.");
            } else if (checkoutItem.getQuantity() > item.getItemLimit()) {
                result.addMessage(ResultType.INVALID, "Quantity exceeds item limit.");
            }
        }

        // Add check for duplicate (needed for update)
        List<CheckoutItem> existingItems = checkoutItemRepository.findByCheckoutOrderId(checkoutItem.getCheckoutOrderId());
        for (CheckoutItem existingItem : existingItems) {
            if (existingItem.equals(checkoutItem)) {
                result.addMessage(ResultType.INVALID, "Duplicate checkout item found.");
                break;
            }
        }

        return result;
    }
}
