package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.PurchaseOrderRepository;
import re.api.data.PurchaseItemRepository;
import re.api.models.PurchaseOrder;
import re.api.models.PurchaseItem;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.domain.Validations;

import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseItemRepository purchaseItemRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                PurchaseItemRepository purchaseItemRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseItemRepository = purchaseItemRepository;
    }

    public List<PurchaseOrder> findAll() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            List<PurchaseItem> items = purchaseItemRepository.findByPurchaseOrderId(purchaseOrder.getPurchaseId());
            purchaseOrder.setPurchaseItems(items);
        }
        return purchaseOrders;
    }

    public PurchaseOrder findById(int purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId);
        if (purchaseOrder != null) {
            List<PurchaseItem> items = purchaseItemRepository.findByPurchaseOrderId(purchaseOrderId);
            purchaseOrder.setPurchaseItems(items);
        }
        return purchaseOrder;
    }

    @Transactional
    public Result<PurchaseOrder> add(PurchaseOrder purchaseOrder) {
        Result<PurchaseOrder> result = validate(purchaseOrder);

        if (!result.isSuccess()) {
            return result;
        }

        if (purchaseOrder.getPurchaseId() != 0) {
            result.addMessage(ResultType.INVALID, "Purchase ID cannot be set for add operation.");
            return result;
        }

        PurchaseOrder added = purchaseOrderRepository.add(purchaseOrder);

        if (added == null) {
            result.addMessage(ResultType.INVALID, "Failed to add purchase order.");
            return result;
        }

        if (purchaseOrder.getPurchaseItems() != null) {
            for (PurchaseItem item : purchaseOrder.getPurchaseItems()) {
                item.setPurchaseOrderId(added.getPurchaseId());
                purchaseItemRepository.add(item);
            }
        }

        result.setPayload(added);
        return result;
    }

    @Transactional
    public Result<PurchaseOrder> update(PurchaseOrder purchaseOrder) {
        Result<PurchaseOrder> result = validate(purchaseOrder);

        if (!result.isSuccess()) {
            return result;
        }

        if (purchaseOrder.getPurchaseId() <= 0) {
            result.addMessage(ResultType.INVALID, "Purchase ID must be set for update.");
            return result;
        }

        boolean updated = purchaseOrderRepository.update(purchaseOrder);

        if (!updated) {
            result.addMessage(ResultType.NOT_FOUND, "Purchase order not found.");
        } else {
            result.setPayload(purchaseOrder);
        }

        return result;
    }

    @Transactional
    public Result<PurchaseOrder> deleteById(int purchaseOrderId) {
        Result<PurchaseOrder> result = new Result<>();

        purchaseItemRepository.deleteByPurchaseOrderId(purchaseOrderId);

        if (!purchaseOrderRepository.deleteById(purchaseOrderId)) {
            result.addMessage(ResultType.NOT_FOUND, "Purchase order not found.");
        }

        return result;
    }

    private Result<PurchaseOrder> validate(PurchaseOrder purchaseOrder) {
        Result<PurchaseOrder> result = new Result<>();

        if (purchaseOrder == null) {
            result.addMessage(ResultType.INVALID, "Purchase order cannot be null.");
            return result;
        }

        if (purchaseOrder.getPurchaseDate() == null) {
            result.addMessage(ResultType.INVALID, "Purchase date is required.");
        }

        // Option to validate vendor/admin IDs or enriched objects, but skipping for now.

        return result;
    }
}
