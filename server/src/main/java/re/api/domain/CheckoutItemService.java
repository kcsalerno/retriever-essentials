package re.api.domain;

import org.springframework.stereotype.Service;
import re.api.data.CheckoutItemRepository;
import re.api.models.CheckoutItem;

import java.util.List;
import java.util.Map;

@Service
public class CheckoutItemService {

    private final CheckoutItemRepository repository;

    public CheckoutItemService(CheckoutItemRepository repository) {
        this.repository = repository;
    }

    public CheckoutItem findById(int checkoutItemId) {
        return repository.findById(checkoutItemId);
    }

    // Used by CheckoutOrderService. Can we remove?
    public List<CheckoutItem> findByCheckoutOrderId(int checkoutOrderId) {
        return repository.findByCheckoutOrderId(checkoutOrderId);
    }

    // Add handled by CheckoutOrderService

    public List<Map<String, Object>> findPopularItems() {
        return repository.findPopularItems();
    }

    public List<Map<String, Object>> findPopularCategories() {
        return repository.findPopularCategories();
    }

    public Result<CheckoutItem> update(CheckoutItem checkoutItem) {
        Result<CheckoutItem> result = validate(checkoutItem);

        if (!result.isSuccess()) {
            return result;
        }

        if (checkoutItem.getCheckoutItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Checkout item ID must be set for update.");
            return result;
        }

        if (!repository.update(checkoutItem)) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout item not found.");
        } else {
            result.setPayload(checkoutItem);
        }

        return result;
    }

    public Result<CheckoutItem> deleteById(int checkoutItemId) {
        Result<CheckoutItem> result = new Result<>();

        if (!repository.deleteById(checkoutItemId)) {
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

        if (checkoutItem.getCheckoutOrderId() <= 0) {
            result.addMessage(ResultType.INVALID, "Checkout order ID is required.");
        }

        if (checkoutItem.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID is required.");
        }

        if (checkoutItem.getQuantity() <= 0) {
            result.addMessage(ResultType.INVALID, "Quantity must be greater than 0.");
        }

        return result;
    }
}
