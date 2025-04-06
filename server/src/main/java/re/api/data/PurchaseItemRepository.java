package re.api.data;

import re.api.models.PurchaseItem;
import java.util.List;

public interface PurchaseItemRepository {
    PurchaseItem findById(int purchaseItemId);

    List<PurchaseItem> findByPurchaseOrderId(int purchaseOrderId);

    PurchaseItem add(PurchaseItem purchaseItem);

    boolean update(PurchaseItem purchaseItem);

    boolean deleteById(int purchaseItemId);

    boolean deleteByPurchaseOrderId(int purchaseOrderId);
}