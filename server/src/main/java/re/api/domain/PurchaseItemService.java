package re.api.domain;

import org.springframework.stereotype.Service;
import re.api.data.PurchaseItemRepository;
import re.api.models.PurchaseItem;

import java.util.List;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository repository;

    public PurchaseItemService(PurchaseItemRepository repository) {
        this.repository = repository;
    }

    public PurchaseItem findById(int purchaseItemId) {
        return repository.findById(purchaseItemId);
    }

//    public List<PurchaseItem> findByPurchaseOrderId(int purchaseOrderId) {
//        return repository.findByPurchaseOrderId(purchaseOrderId);
//    }

    // Add handled by PurchaseOrderService

    public Result<PurchaseItem> update(PurchaseItem purchaseItem) {
        Result<PurchaseItem> result = validate(purchaseItem);

        if (!result.isSuccess()) {
            return result;
        }

        if (purchaseItem.getPurchaseItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Purchase item ID must be set for update.");
            return result;
        }

        if (!repository.update(purchaseItem)) {
            result.addMessage(ResultType.NOT_FOUND, "Purchase item not found.");
        } else {
            result.setPayload(purchaseItem);
        }

        return result;
    }

    public Result<PurchaseItem> deleteById(int purchaseItemId) {
        Result<PurchaseItem> result = new Result<>();

        if (!repository.deleteById(purchaseItemId)) {
            result.addMessage(ResultType.NOT_FOUND, "Purchase item ID not found.");
        }

        return result;
    }

    private Result<PurchaseItem> validate(PurchaseItem purchaseItem) {
        Result<PurchaseItem> result = new Result<>();

        if (purchaseItem == null) {
            result.addMessage(ResultType.INVALID, "Purchase item cannot be null.");
            return result;
        }

        if (purchaseItem.getPurchaseOrderId() <= 0) {
            result.addMessage(ResultType.INVALID, "Purchase order ID must be set.");
        }

        if (purchaseItem.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID must be set.");
        }

        if (purchaseItem.getQuantity() <= 0) {
            result.addMessage(ResultType.INVALID, "Quantity must be greater than zero.");
        }

        return result;
    }
}
