package re.api.models;

import java.util.Objects;

public class CheckoutItem {
    private int checkoutItemId;
    private CheckoutOrder checkoutOrder;
    private Item item;
    private int quantity;

    public CheckoutItem() {}

    public CheckoutItem(int checkoutItemId, CheckoutOrder checkoutOrder, Item item, int quantity) {
        this.checkoutItemId = checkoutItemId;
        this.checkoutOrder = checkoutOrder;
        this.item = item;
        this.quantity = quantity;
    }

    public int getCheckoutItemId() {
        return checkoutItemId;
    }

    public void setCheckoutItemId(int checkoutItemId) {
        this.checkoutItemId = checkoutItemId;
    }

    public CheckoutOrder getCheckoutOrder() {
        return checkoutOrder;
    }

    public void setCheckoutOrder(CheckoutOrder checkoutOrder) {
        this.checkoutOrder = checkoutOrder;
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
        return checkoutItemId == that.checkoutItemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutItemId);
    }
}
