package re.api.models;

import java.util.Objects;

public class PurchaseItem {
    private int purchaseItemId;
    private int purchaseOrderId; // FK
    private int itemId; // FK
    private int quantity;


    // Enriched objects (for GET requests)
    private Item item;
    private PurchaseOrder purchaseOrder;

    public PurchaseItem() {}

    public PurchaseItem(int purchaseItemId, int purchaseOrderId, int itemId, int quantity) {
        this.purchaseItemId = purchaseItemId;
        this.purchaseOrderId = purchaseOrderId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseItem that = (PurchaseItem) o;
        return purchaseItemId == that.purchaseItemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseItemId);
    }

    @Override
    public String toString() {
        return "PurchaseItem{" +
                "purchaseItemId=" + purchaseItemId +
                ", purchaseOrder=" + purchaseOrder +
                ", item=" + item +
                ", quantity=" + quantity +
                '}';
    }
}
