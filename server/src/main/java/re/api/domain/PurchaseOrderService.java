package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.*;
import re.api.models.*;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.domain.Validations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final AppUserRepository appUserRepository;
    private final VendorRepository vendorRepository;
    private final ItemRepository itemRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                PurchaseItemRepository purchaseItemRepository,
                                AppUserRepository appUserRepository,
                                VendorRepository vendorRepository,
                                ItemRepository itemRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.appUserRepository = appUserRepository;
        this.vendorRepository = vendorRepository;
        this.itemRepository = itemRepository;
    }


    public List<PurchaseOrder> findAll() {
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        if (purchaseOrderList == null || purchaseOrderList.isEmpty()) {
            return purchaseOrderList;
        }

        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            enrichPurchaseWithItemsAdminAndVendor(purchaseOrder);
        }

        return purchaseOrderList;
    }

    public PurchaseOrder findById(int purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId);
        if (purchaseOrder != null) {
            enrichPurchaseWithItemsAdminAndVendor(purchaseOrder);
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

        PurchaseOrder addedPurchase = purchaseOrderRepository.add(purchaseOrder);

        if (addedPurchase == null) {
            result.addMessage(ResultType.INVALID, "Failed to add purchase order.");
            return result;
        }

        if (purchaseOrder.getPurchaseItems() != null) {
            for (PurchaseItem purchaseItem : purchaseOrder.getPurchaseItems()) {
                purchaseItem.setPurchaseOrderId(addedPurchase.getPurchaseId());
                purchaseItemRepository.add(purchaseItem);
            }
        }

        result.setPayload(addedPurchase);
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

        validateAdmin(result, purchaseOrder.getAdminId());
        validateVendor(result, purchaseOrder.getVendorId());

        if (purchaseOrder.getPurchaseDate() == null) {
            result.addMessage(ResultType.INVALID, "Purchase date is required.");
        }

        if (purchaseOrder.getPurchaseItems() != null && !purchaseOrder.getPurchaseItems().isEmpty()) {
            Set<Integer> itemIds = new HashSet<>();

            for (PurchaseItem item : purchaseOrder.getPurchaseItems()) {
                // Check for duplicates
                if (!itemIds.add(item.getItemId())) {
                    result.addMessage(ResultType.INVALID,
                            "Duplicate item in purchase order: Item ID " + item.getItemId());
                    continue; // Skip further validation for this item
                }

                validatePurchaseItem(result, item);
            }
        }

        return result;
    }


    private void validatePurchaseItem(Result<?> result, PurchaseItem purchaseItem) {
        if (purchaseItem == null) {
            result.addMessage(ResultType.INVALID, "Purchase item cannot be null.");
            return;
        }

        if (purchaseItem.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Item ID is required.");
            return;
        }

        if (purchaseItem.getQuantity() <= 0) {
            result.addMessage(ResultType.INVALID, "Quantity must be greater than zero.");
        }

        Item item = itemRepository.findById(purchaseItem.getItemId());
        if (item == null || !item.isEnabled()) {
            result.addMessage(ResultType.NOT_FOUND, "Item ID " + purchaseItem.getItemId() + " not found or disabled.");
        }
    }

    private void validateVendor(Result<?> result, int vendorId) {
        if (vendorId <= 0) {
            result.addMessage(ResultType.INVALID, "Vendor ID is required.");
            return;
        }

        if (vendorRepository.findById(vendorId) == null) {
            result.addMessage(ResultType.NOT_FOUND, "Vendor ID does not exist.");
        }
    }

    private void validateAdmin(Result<?> result, int adminId) {
        if (adminId <= 0) {
            result.addMessage(ResultType.INVALID, "Admin ID is required.");
            return;
        }

        AppUser admin = appUserRepository.findById(adminId);
        if (admin == null || !admin.isEnabled()) {
            result.addMessage(ResultType.NOT_FOUND, "Admin ID does not exist or is disabled.");
        }
    }

    private void enrichPurchaseWithItems(PurchaseOrder purchaseOrder) {
        List<PurchaseItem> purchaseItems = purchaseItemRepository.findByPurchaseOrderId(purchaseOrder.getPurchaseId());
        if (purchaseItems == null || purchaseItems.isEmpty()) {
            return;
        }

        // Fetch items from the item repository using the IDs from checkout items
        for (PurchaseItem purchaseItem : purchaseItems) {
            Item item  = itemRepository.findById(purchaseItem.getItemId());
            if (item != null) {
                purchaseItem.setItem(item);
            }
        }

        purchaseOrder.setPurchaseItems(purchaseItems);
    }

    private void enrichPurchaseWithAdmin(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getAdminId() > 0) {
            AppUser admin = appUserRepository.findById(purchaseOrder.getAdminId());
            if (admin != null) {
                purchaseOrder.setAdmin(admin);
            }
        }
    }

    private void enrichPurchaseWithVendor(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getVendorId() > 0) {
            Vendor vendor = vendorRepository.findById(purchaseOrder.getVendorId());
            if (vendor != null) {
                purchaseOrder.setVendor(vendor);
            }
        }
    }

    private void enrichPurchaseWithItemsAdminAndVendor(PurchaseOrder purchaseOrder) {
        enrichPurchaseWithItems(purchaseOrder);
        enrichPurchaseWithAdmin(purchaseOrder);
        enrichPurchaseWithVendor(purchaseOrder);
    }
}
