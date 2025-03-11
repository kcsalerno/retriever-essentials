package re.api.data;

import re.api.models.InventoryLog;
import java.util.List;

public interface InventoryLogRepository {
    List<InventoryLog> findAll();
    List<InventoryLog> findByItemName(String itemName);
    List<InventoryLog> findByAuthorityEmail(String authorityEmail);
    InventoryLog findById(int logId);
    InventoryLog add(InventoryLog log);
    boolean update(InventoryLog log);
    boolean deleteById(int logId);
}