package re.api.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CheckoutOrder {
    private int checkoutOrderId;
    private String studentId;
    private int authorityId; // FK
    private boolean selfCheckout;
    private LocalDateTime checkoutDate;

    // Enriched objects
    private AppUser authority;
    private List<CheckoutItem> checkoutItemList;

    public CheckoutOrder() {}

    public CheckoutOrder(int checkoutOrderId, String studentId, int authorityId, boolean selfCheckout, LocalDateTime checkoutDate) {
        this.checkoutOrderId = checkoutOrderId;
        this.studentId = studentId;
        this.authorityId = authorityId;
        this.selfCheckout = selfCheckout;
        this.checkoutDate = checkoutDate;
    }

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

    public List<CheckoutItem> getCheckoutItems() {
        return checkoutItemList;
    }

    public void setCheckoutItems(List<CheckoutItem> items) {
        this.checkoutItemList = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckoutOrder that = (CheckoutOrder) o;
        return authorityId == that.authorityId
                && Objects.equals(studentId, that.studentId)
                && Objects.equals(checkoutDate, that.checkoutDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, authorityId, checkoutDate);
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
                ", items=" + checkoutItemList +
                '}';
    }
}
