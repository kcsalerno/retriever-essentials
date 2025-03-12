package re.api.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class InventoryLog {
    private int logId;
    private Integer authorityId; // Nullable in case an authority is deleted
    private int itemId;
    private int quantityChange;
    private String reason;
    private LocalDateTime timeStamp;
    // Additional fields for display
    private String authorityEmail; // (Username) Retrieved from app_user
    private String itemName; // Retrieved from item

    public InventoryLog() {}

    public InventoryLog(int logId, Integer authorityId, int itemId, int quantityChange, String reason, LocalDateTime timeStamp, String authorityEmail, String itemName) {
        this.logId = logId;
        this.authorityId = authorityId;
        this.itemId = itemId;
        this.quantityChange = quantityChange;
        this.reason = reason;
        this.timeStamp = timeStamp;
        this.authorityEmail = authorityEmail;
        this.itemName = itemName;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAuthorityEmail() {
        return authorityEmail;
    }

    public void setAuthorityEmail(String authorityEmail) {
        this.authorityEmail = authorityEmail;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;     // Self-check
        if (o == null || getClass() != o.getClass()) return false;
        InventoryLog that = (InventoryLog) o;
        return logId == that.logId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(logId);
    }

    @Override
    public String toString() {
        return "InventoryLog{" +
                "logId=" + logId +
                ", authorityId=" + authorityId +
                ", itemId=" + itemId +
                ", quantityChange=" + quantityChange +
                ", reason='" + reason + '\'' +
                ", timeStamp=" + timeStamp +
                ", authorityEmail='" + authorityEmail + '\'' +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}