package re.api.models;

import java.util.Objects;

public class Vendor {
    private int vendorId;
    private String vendorName;
    private String phoneNumber;
    private String contactEmail;

    public Vendor() {}

    public Vendor(int vendorId, String vendorName, String phoneNumber, String contactEmail) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.phoneNumber = phoneNumber;
        this.contactEmail = contactEmail;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vendor vendor = (Vendor) o;
        return Objects.equals(vendorName, vendor.vendorName) && Objects.equals(phoneNumber, vendor.phoneNumber) && Objects.equals(contactEmail, vendor.contactEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorName, phoneNumber, contactEmail);
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "vendorId=" + vendorId +
                ", vendorName='" + vendorName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                '}';
    }
}
