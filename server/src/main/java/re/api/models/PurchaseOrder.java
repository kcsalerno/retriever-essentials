package re.api.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class PurchaseOrder {
    private int purchaseId;
    private AppUser admin;
    private Vendor vendor;
    private LocalDateTime purchaseDate;

    public PurchaseOrder() {}

    public PurchaseOrder(int purchaseId, AppUser admin, Vendor vendor, LocalDateTime purchaseDate) {
        this.purchaseId = purchaseId;
        this.admin = admin;
        this.vendor = vendor;
        this.purchaseDate = purchaseDate;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public AppUser getAdmin() {
        return admin;
    }

    public void setAdmin(AppUser admin) {
        this.admin = admin;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return purchaseId == that.purchaseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId);
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "purchaseId=" + purchaseId +
                ", admin=" + admin +
                ", vendor=" + vendor +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
