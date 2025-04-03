package re.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PurchaseOrder {
    private int purchaseId;
    private int adminId; // FK
    private int vendorId; // FK
    private LocalDateTime purchaseDate;

    // Enriched objects (for GET requests)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AppUser admin;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Vendor vendor;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PurchaseItem> purchaseItems;

    public PurchaseOrder() {}

    public PurchaseOrder(int purchaseId, int adminId, int vendorId, LocalDateTime purchaseDate) {
        this.purchaseId = purchaseId;
        this.adminId = adminId;
        this.vendorId = vendorId;
        this.purchaseDate = purchaseDate;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
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

    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItem> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return adminId == that.adminId
                && vendorId == that.vendorId
                && Objects.equals(purchaseDate, that.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId, vendorId, purchaseDate);
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
