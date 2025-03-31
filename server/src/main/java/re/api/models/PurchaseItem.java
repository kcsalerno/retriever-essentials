package re.api.models;

import java.util.Objects;

public class PurchaseItem {
    private int purchaseItemId;
    private int purchaseOrderId; // FK
    private int itemId; // FK
    private int quantity;

    // Enriched object (for GET requests)
    private Item item;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseItem that = (PurchaseItem) o;
        return purchaseOrderId == that.purchaseOrderId && itemId == that.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseOrderId, itemId);
    }

    @Override
    public String toString() {
        return "PurchaseItem{" +
                "purchaseItemId=" + purchaseItemId +
                ", purchaseOrderId=" + purchaseOrderId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", item=" + item +
                '}';
    }
}
