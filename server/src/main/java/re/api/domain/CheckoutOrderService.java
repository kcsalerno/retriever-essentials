package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.CheckoutOrderRepository;
import re.api.data.CheckoutItemRepository;
import re.api.models.CheckoutItem;
import re.api.models.CheckoutOrder;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.domain.Validations;

import java.util.List;
import java.util.Map;

@Service
public class CheckoutOrderService {

    private final CheckoutOrderRepository checkoutOrderRepository;
    private final CheckoutItemRepository checkoutItemRepository;

    public CheckoutOrderService(CheckoutOrderRepository checkoutOrderRepository,
                                CheckoutItemRepository checkoutItemRepository) {
        this.checkoutOrderRepository = checkoutOrderRepository;
        this.checkoutItemRepository = checkoutItemRepository;
    }

    public List<CheckoutOrder> findAll() {
        List<CheckoutOrder> orders = checkoutOrderRepository.findAll();
        for (CheckoutOrder order : orders) {
            List<CheckoutItem> items = checkoutItemRepository.findByCheckoutOrderId(order.getCheckoutOrderId());
            order.setItems(items);
        }
        return orders;
    }

    public CheckoutOrder findById(int checkoutOrderId) {
        CheckoutOrder order = checkoutOrderRepository.findById(checkoutOrderId);
        if (order != null) {
            List<CheckoutItem> items = checkoutItemRepository.findByCheckoutOrderId(order.getCheckoutOrderId());
            order.setItems(items);
        }
        return order;
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

        if (checkoutOrder.getItems() != null) {
            for (CheckoutItem item : checkoutOrder.getItems()) {
                item.setCheckoutOrderId(addedOrder.getCheckoutOrderId());
                checkoutItemRepository.add(item);
            }
        }

        result.setPayload(addedOrder);
        return result;
    }

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

        if (checkoutOrder.getCheckoutDate() == null) {
            result.addMessage(ResultType.INVALID, "Checkout date is required.");
        }

        return result;
    }
}
