package re.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

public class CheckoutItem {
    private int checkoutItemId;
    private int checkoutOrderId; // FK
    private int itemId; // FK
    private int quantity;

    // Enriched object (for GET requests)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Item item;

    public CheckoutItem() {}

    public CheckoutItem(int checkoutItemId, int checkoutId, int itemId, int quantity) {
        this.checkoutItemId = checkoutItemId;
        this.checkoutOrderId = checkoutId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getCheckoutItemId() {
        return checkoutItemId;
    }

    public void setCheckoutItemId(int checkoutItemId) {
        this.checkoutItemId = checkoutItemId;
    }

    public int getCheckoutOrderId() {
        return checkoutOrderId;
    }

    public void setCheckoutOrderId(int checkoutOrderId) {
        this.checkoutOrderId = checkoutOrderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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
        CheckoutItem that = (CheckoutItem) o;
        return checkoutOrderId == that.checkoutOrderId && itemId == that.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutOrderId, itemId);
    }

    @Override
    public String toString() {
        return "CheckoutItem{" +
                "checkoutItemId=" + checkoutItemId +
                ", itemId=" + itemId +
                ", checkoutOrderId=" + checkoutOrderId +
                ", quantity=" + quantity +
                ", item=" + item +
                '}';
    }
}
