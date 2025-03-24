package re.api.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CheckoutOrder {
    private int checkoutOrderId;
    private String studentId;
    private int authorityId;
    private boolean selfCheckout;
    private LocalDateTime checkoutDate;

    // Enriched objects
    private AppUser authority;
    private List<CheckoutItem> items;

    public int getCheckoutOrderId() {
        return checkoutOrderId;
    }

    public void setCheckoutOrderId(int checkoutOrderId) {
        this.checkoutOrderId = checkoutOrderId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(int authorityId) {
        this.authorityId = authorityId;
    }

    public boolean isSelfCheckout() {
        return selfCheckout;
    }

    public void setSelfCheckout(boolean selfCheckout) {
        this.selfCheckout = selfCheckout;
    }

    public LocalDateTime getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDateTime checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public AppUser getAuthority() {
        return authority;
    }

    public void setAuthority(AppUser authority) {
        this.authority = authority;
    }

    public List<CheckoutItem> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CheckoutOrder)) return false;
        CheckoutOrder that = (CheckoutOrder) o;
        return checkoutOrderId == that.checkoutOrderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutOrderId);
    }

    @Override
    public String toString() {
        return "CheckoutOrder{" +
                "checkoutOrderId=" + checkoutOrderId +
                ", studentId='" + studentId + '\'' +
                ", authorityId=" + authorityId +
                ", selfCheckout=" + selfCheckout +
                ", checkoutDate=" + checkoutDate +
                ", authority=" + authority +
                ", items=" + items +
                '}';
    }
}
