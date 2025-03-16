package re.api.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class CheckoutOrder {
    private int checkoutId;
    private String studentId;
    private AppUser authority;
    private boolean selfCheckout;
    private LocalDateTime checkoutDate;

    public CheckoutOrder() {}

    public CheckoutOrder(int checkoutId, String studentId, AppUser authority, boolean selfCheckout, LocalDateTime checkoutDate) {
        this.checkoutId = checkoutId;
        this.studentId = studentId;
        this.authority = authority;
        this.selfCheckout = selfCheckout;
        this.checkoutDate = checkoutDate;
    }

    public int getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(int checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public AppUser getAuthority() {
        return authority;
    }

    public void setAuthority(AppUser authority) {
        this.authority = authority;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckoutOrder that = (CheckoutOrder) o;
        return checkoutId == that.checkoutId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkoutId);
    }
}
