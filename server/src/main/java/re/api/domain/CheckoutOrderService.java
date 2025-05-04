package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.AppUserRepository;
import re.api.data.CheckoutOrderRepository;
import re.api.data.CheckoutItemRepository;
import re.api.data.ItemRepository;
import re.api.models.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CheckoutOrderService {
    private final CheckoutOrderRepository checkoutOrderRepository;
    private final CheckoutItemRepository checkoutItemRepository;
    private final ItemRepository itemRepository;
    private final AppUserRepository appUserRepository;

    public CheckoutOrderService(CheckoutOrderRepository checkoutOrderRepository,
                                CheckoutItemRepository checkoutItemRepository,
                                ItemRepository itemRepository,
                                AppUserRepository appUserRepository) {
        this.checkoutOrderRepository = checkoutOrderRepository;
        this.checkoutItemRepository = checkoutItemRepository;
        this.itemRepository = itemRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<CheckoutOrder> findAll() {
        List<CheckoutOrder> checkoutOrderList = checkoutOrderRepository.findAll();
        if (checkoutOrderList == null || checkoutOrderList.isEmpty()) {
            return checkoutOrderList;
        }

        for (CheckoutOrder checkoutOrder : checkoutOrderList) {
            enrichOrderWithItemsAndAuthority(checkoutOrder);
        }

        return checkoutOrderList;
    }

    public CheckoutOrder findById(int checkoutOrderId) {
        CheckoutOrder checkoutOrder = checkoutOrderRepository.findById(checkoutOrderId);
        if (checkoutOrder != null) {
            enrichOrderWithItemsAndAuthority(checkoutOrder);
        }

        return checkoutOrder;
    }

    public List<Map<String, Object>> findHourlyCheckoutSummary() {
        return checkoutOrderRepository.findHourlyCheckoutSummary();
    }

    @Transactional
    public Result<CheckoutOrder> add(CheckoutOrder checkoutOrder) {
        Result<CheckoutOrder> result = validate(checkoutOrder);

        if (!result.isSuccess()) {
            return result;
        }

        if (checkoutOrder.getCheckoutOrderId() != 0) {
            result.addMessage(ResultType.INVALID, "Checkout order ID cannot be set for `add` operation.");
        }

        CheckoutOrder addedOrder = checkoutOrderRepository.add(checkoutOrder);

        if (addedOrder == null) {
            result.addMessage(ResultType.INVALID, "Failed to add checkout order.");
            return result;
        }

        if (checkoutOrder.getCheckoutItems() != null) {
            for (CheckoutItem checkoutItem : checkoutOrder.getCheckoutItems()) {
                checkoutItem.setCheckoutOrderId(addedOrder.getCheckoutOrderId());
                checkoutItemRepository.add(checkoutItem);

                // Update the item count in the inventory
                boolean updatedCount = itemRepository.updateCurrentCount(checkoutItem.getItemId(), -checkoutItem.getQuantity());
                if (!updatedCount) {
                    result.addMessage(ResultType.INVALID, "Failed to update item count for item ID: " + checkoutItem.getItemId());
                }
            }
        }

        result.setPayload(addedOrder);
        return result;
    }

    @Transactional
    public Result<CheckoutOrder> update(CheckoutOrder checkoutOrder) {
        Result<CheckoutOrder> result = validate(checkoutOrder);

        if (!result.isSuccess()) {
            return result;
        }

        if (checkoutOrder.getCheckoutOrderId() <= 0) {
            result.addMessage(ResultType.INVALID, "Checkout order ID must be set for update.");
            return result;
        }

        boolean updated = checkoutOrderRepository.update(checkoutOrder);

        if (!updated) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout order not found.");
        } else {
            result.setPayload(checkoutOrder);
        }

        return result;
    }

    @Transactional
    public Result<CheckoutOrder> deleteById(int checkoutOrderId) {
        Result<CheckoutOrder> result = new Result<>();

        CheckoutOrder existing = checkoutOrderRepository.findById(checkoutOrderId);
        if (existing == null) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout order ID not found.");
            return result;
        }

        // Update the item count in the inventory
        List<CheckoutItem> checkoutItems = checkoutItemRepository.findByCheckoutOrderId(checkoutOrderId);
        if (checkoutItems != null) {
            for (CheckoutItem checkoutItem : checkoutItems) {
                boolean updatedCount = itemRepository.updateCurrentCount(checkoutItem.getItemId(), checkoutItem.getQuantity());
                if (!updatedCount) {
                    result.addMessage(ResultType.INVALID, "Failed to update item count for item ID: " + checkoutItem.getItemId());
                }
            }
        }

        // Delete checkout items associated with the order
        checkoutItemRepository.deleteByCheckoutOrderId(checkoutOrderId);

        // Delete the checkout order
        if (!checkoutOrderRepository.deleteById(checkoutOrderId)) {
            result.addMessage(ResultType.NOT_FOUND, "Checkout order not found.");
        }

        return result;
    }

    private Result<CheckoutOrder> validate(CheckoutOrder checkoutOrder) {
        Result<CheckoutOrder> result = new Result<>();

        if (checkoutOrder == null) {
            result.addMessage(ResultType.INVALID, "Checkout order cannot be null.");
            return result;
        }

        if (Validations.isNullOrBlank(checkoutOrder.getStudentId())) {
            result.addMessage(ResultType.INVALID, "Student ID is required.");
        } else if (checkoutOrder.getStudentId().length() > 10) {
            result.addMessage(ResultType.INVALID, "Student ID cannot exceed 10 characters.");
        }

        validateAuthority(result, checkoutOrder.getAuthorityId());

        if (checkoutOrder.getCheckoutDate() == null) {
            result.addMessage(ResultType.INVALID, "Checkout date is required.");
        }

        // Validate checkout items if present
        if (checkoutOrder.getCheckoutItems() != null && !checkoutOrder.getCheckoutItems().isEmpty()) {
            Set<Integer> itemIds = new HashSet<>();

            for (CheckoutItem checkoutItem : checkoutOrder.getCheckoutItems()) {
                if (checkoutItem == null) {
                    result.addMessage(ResultType.INVALID, "Checkout item cannot be null.");
                    continue; // prevent NPE
                }

                if (!itemIds.add(checkoutItem.getItemId())) {
                    result.addMessage(ResultType.INVALID,
                            "Duplicate item in checkout order: Item ID " + checkoutItem.getItemId());
                    continue;
                }

                validateCheckoutItem(result, checkoutItem);
            }
        }

        List<CheckoutOrder> existing = checkoutOrderRepository.findAll();
        for (CheckoutOrder order : existing) {
            if (order.equals(checkoutOrder)) {
                result.addMessage(ResultType.INVALID, "Duplicate checkout order already exists.");
                return result;
            }
        }

        return result;
    }

    private void validateCheckoutItem(Result<?> result, CheckoutItem checkoutItem) {
        if (checkoutItem == null) {
            result.addMessage(ResultType.INVALID, "Checkout item cannot be null.");
            return;
        }

        if (checkoutItem.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID is required.");
            return;
        }

        Item item = itemRepository.findById(checkoutItem.getItemId());
        if (item == null || !item.isEnabled()) {
            result.addMessage(ResultType.NOT_FOUND, "Item does not exist or is disabled.");
            return;
        }

        if (checkoutItem.getQuantity() <= 0) {
            result.addMessage(ResultType.INVALID,
                    "Quantity for item " + item.getItemName() + " must be greater than 0.");
        } else {
            if (checkoutItem.getQuantity() > item.getCurrentCount()) {
                result.addMessage(ResultType.INVALID,
                        String.format("Quantity for item %s exceeds available stock (%d).",
                                item.getItemName(), item.getCurrentCount()));
            }

            if (checkoutItem.getQuantity() > item.getItemLimit()) {
                result.addMessage(ResultType.INVALID,
                        String.format("Quantity for item %s exceeds limit (%d).",
                                item.getItemName(), item.getItemLimit()));
            }
        }
    }

    private void validateAuthority(Result<?> result, int authorityId) {
        if (authorityId <= 0) {
            result.addMessage(ResultType.INVALID, "Authority ID is required.");
            return;
        }

        AppUser authority = appUserRepository.findById(authorityId);
        if (authority == null || !authority.isEnabled()) {
            result.addMessage(ResultType.NOT_FOUND, "Authority does not exist or is disabled.");
        }
    }

    private void enrichOrderWithItems(CheckoutOrder checkoutOrder) {
        // Fetch checkout items associated with order (contains Item IDs and quantities)
        List<CheckoutItem> checkoutItems = checkoutItemRepository.findByCheckoutOrderId(checkoutOrder.getCheckoutOrderId());
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            return;
        }

        // Fetch items from the item repository using the IDs from checkout items
        for (CheckoutItem checkoutItem : checkoutItems) {
            Item item  = itemRepository.findById(checkoutItem.getItemId());
            if (item != null) {
                checkoutItem.setItem(item);
            }
        }

        checkoutOrder.setCheckoutItems(checkoutItems);
    }

    private void enrichOrderWithAuthority(CheckoutOrder checkoutOrder) {
        if (checkoutOrder.getAuthorityId() > 0) {
            AppUser authority = appUserRepository.findById(checkoutOrder.getAuthorityId());
            if (authority != null) {
                checkoutOrder.setAuthority(authority);
            }
        }
    }

    private void enrichOrderWithItemsAndAuthority(CheckoutOrder checkoutOrder) {
        enrichOrderWithItems(checkoutOrder);
        enrichOrderWithAuthority(checkoutOrder);
    }
}
