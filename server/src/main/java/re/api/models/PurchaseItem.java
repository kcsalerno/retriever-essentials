package re.api.models;

import java.util.Objects;

public class PurchaseItem {
    private int purchaseItemId;
    private PurchaseOrder purchaseOrder;
    private Item item;
    private int quantity;

    public PurchaseItem() {}

    public PurchaseItem(int purchaseItemId, PurchaseOrder purchaseOrder, Item item, int quantity) {
        this.purchaseItemId = purchaseItemId;
        this.purchaseOrder = purchaseOrder;
        this.item = item;
        this.quantity = quantity;
    }

    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
