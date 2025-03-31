package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.AppUserRepository;
import re.api.data.CheckoutOrderRepository;
import re.api.data.CheckoutItemRepository;
import re.api.data.ItemRepository;
import re.api.models.AppUser;
import re.api.models.CheckoutItem;
import re.api.models.CheckoutOrder;
import re.api.models.Item;

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

    public List<Map<String, Object>> findTopBusiestHours() {
        return checkoutOrderRepository.findTopBusiestHours();
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
            for (CheckoutItem item : checkoutOrder.getCheckoutItems()) {
                item.setCheckoutOrderId(addedOrder.getCheckoutOrderId());
                checkoutItemRepository.add(item);
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

        checkoutItemRepository.deleteByCheckoutOrderId(checkoutOrderId);

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

        if (checkoutOrder.getAuthorityId() <= 0) {
            result.addMessage(ResultType.INVALID, "Invalid authority ID.");
        } else {
            AppUser authority = appUserRepository.findById(checkoutOrder.getAuthorityId());
            if (authority == null || !authority.isEnabled()) {
                result.addMessage(ResultType.NOT_FOUND, "Authority ID does not exist or is disabled.");
            }
        }

        if (checkoutOrder.getCheckoutDate() == null) {
            result.addMessage(ResultType.INVALID, "Checkout date is required.");
        }

        // Is this enough validation for Checkout Item since that validation is not being done in Checkout Item Service?
        // Or is there a way to do validation there, and then just call that method here?
        if (checkoutOrder.getCheckoutItems() != null) {
            Set<Integer> foundItemIds = new HashSet<>();

            for (CheckoutItem checkoutItem : checkoutOrder.getCheckoutItems()) {

                // Check for duplicates using a Set
                if (!foundItemIds.add(checkoutItem.getItemId())) {
                    result.addMessage(ResultType.INVALID,
                            "Duplicate item in checkout order: Item ID " + checkoutItem.getItemId());
                    continue; // Skip additional validation for duplicates
                }

                Item item = itemRepository.findById(checkoutItem.getItemId());

                if (item == null) {
                    result.addMessage(ResultType.INVALID,
                            "Item ID " + checkoutItem.getItemId() + " not found.");
                    continue;
                }

                if (checkoutItem.getQuantity() <= 0) {
                    result.addMessage(ResultType.INVALID,
                            "Quantity for item " + item.getItemName() + " must be greater than 0.");
                }

                if ((item.getItemLimit() > 0) && (checkoutItem.getQuantity() > item.getItemLimit())) {
                    result.addMessage(ResultType.INVALID,
                            String.format("Quantity for item %s exceeds limit (%d).",
                                    item.getItemName(), item.getItemLimit()));
                }
            }
        }

        return result;
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
        // Fetch authority associated with the order
        if (checkoutOrder.getAuthorityId() > 0) {
            checkoutOrder.setAuthority(appUserRepository.findById(checkoutOrder.getAuthorityId()));
        }
    }

    private void enrichOrderWithItemsAndAuthority(CheckoutOrder checkoutOrder) {
        enrichOrderWithItems(checkoutOrder);
        enrichOrderWithAuthority(checkoutOrder);
    }
}
