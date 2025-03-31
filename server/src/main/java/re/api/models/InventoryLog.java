package re.api.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class InventoryLog {
    private int logId;
    private int authorityId;
    private int itemId;
    private int quantityChange;
    private String reason;
    private LocalDateTime timeStamp;

    // Enriched objects (for GET requests)
    private AppUser authority;
    private Item item;

    public InventoryLog() {}

    public InventoryLog(int logId, int authorityId, int itemId, int quantityChange, String reason, LocalDateTime timeStamp) {
        this.logId = logId;
        this.authorityId = authorityId;
        this.itemId = itemId;
        this.quantityChange = quantityChange;
        this.reason = reason;
        this.timeStamp = timeStamp;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(int authorityId) {
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

    public AppUser getAuthority() {
        return authority;
    }

    public void setAuthority(AppUser authority) {
        this.authority = authority;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryLog that = (InventoryLog) o;
        return authorityId == that.authorityId && itemId == that.itemId && quantityChange == that.quantityChange && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId, itemId, quantityChange, timeStamp);
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
                ", authority=" + authority +
                ", item=" + item +
                '}';
    }
}