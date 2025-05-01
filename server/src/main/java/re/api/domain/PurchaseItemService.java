package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.ItemRepository;
import re.api.data.PurchaseItemRepository;
import re.api.data.PurchaseOrderRepository;
import re.api.models.Item;
import re.api.models.PurchaseItem;
import re.api.models.PurchaseOrder;

import java.util.List;

@Service
public class PurchaseItemService {

    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ItemRepository itemRepository;

    public PurchaseItemService(PurchaseItemRepository purchaseItemRepository,
                               PurchaseOrderRepository purchaseOrderRepository,
                               ItemRepository itemRepository) {
        this.purchaseItemRepository = purchaseItemRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.itemRepository = itemRepository;
    }

    public PurchaseItem findById(int purchaseItemId) {
        return purchaseItemRepository.findById(purchaseItemId);
    }

    @Transactional
    public Result<PurchaseItem> update(PurchaseItem purchaseItem) {
        Result<PurchaseItem> result = validate(purchaseItem);

        if (!result.isSuccess()) {
            return result;
        }

        if (purchaseItem.getPurchaseItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Purchase item ID must be set for `update` operation.");
            return result;
        }

        PurchaseItem existing = purchaseItemRepository.findById(purchaseItem.getPurchaseItemId());
        if (existing == null) {
            result.addMessage(ResultType.NOT_FOUND, "Purchase item ID not found.");
            return result;
        }

        int quantityChange = purchaseItem.getQuantity() - existing.getQuantity();
        if (quantityChange != 0) {
            boolean updatedCount = itemRepository.updateCurrentCount(purchaseItem.getItemId(), quantityChange);
            if (!updatedCount) {
                result.addMessage(ResultType.INVALID, "Failed to update item count for item ID: " + purchaseItem.getItemId());
                return result;
            }
        }

        if (!purchaseItemRepository.update(purchaseItem)) {
            result.addMessage(ResultType.NOT_FOUND, "Purchase item not found.");
        } else {
            result.setPayload(purchaseItem);
        }

        return result;
    }

    @Transactional
    public Result<PurchaseItem> deleteById(int purchaseItemId) {
        Result<PurchaseItem> result = new Result<>();

        PurchaseItem existing = purchaseItemRepository.findById(purchaseItemId);
        if (existing == null) {
            result.addMessage(ResultType.NOT_FOUND, "Purchase item ID not found.");
            return result;
        }

        int quantityToSubtract = existing.getQuantity();
        boolean updatedInventory = itemRepository.updateCurrentCount(existing.getItemId(), -quantityToSubtract);
        if (!updatedInventory) {
            result.addMessage(ResultType.INVALID, "Failed to update item count for item ID: " + existing.getItemId());
            return result;
        }

        if (!purchaseItemRepository.deleteById(purchaseItemId)) {
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

        PurchaseOrder purchaseOrder = null;
        if (purchaseItem.getPurchaseOrderId() <= 0) {
            result.addMessage(ResultType.INVALID, "Purchase order ID must be set.");
        } else {
            purchaseOrder = purchaseOrderRepository.findById(purchaseItem.getPurchaseOrderId());
            if (purchaseOrder == null) {
                result.addMessage(ResultType.NOT_FOUND, "Purchase order not found.");
            }
        }

        Item item = null;
        if (purchaseItem.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID must be set.");
        } else {
            item = itemRepository.findById(purchaseItem.getItemId());
            if (item == null || !item.isEnabled()) {
                result.addMessage(ResultType.NOT_FOUND, "Item does not exist or is disabled.");
            }
        }

        if (purchaseItem.getQuantity() <= 0) {
            result.addMessage(ResultType.INVALID, "Quantity must be greater than zero.");
        }

        // Prevent duplicate items in the same purchase order
        List<PurchaseItem> existingItems = purchaseItemRepository.findByPurchaseOrderId(purchaseItem.getPurchaseOrderId());
        for (PurchaseItem existing : existingItems) {
            if (existing.equals(purchaseItem)
                    && existing.getPurchaseItemId() != purchaseItem.getPurchaseItemId()) {
                result.addMessage(ResultType.INVALID, "Duplicate purchase item detected.");
                break;
            }
        }

        return result;
    }
}
