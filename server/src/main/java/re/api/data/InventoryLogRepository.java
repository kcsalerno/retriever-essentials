package re.api.data;

import re.api.models.InventoryLog;
import java.util.List;

public interface InventoryLogRepository {
    List<InventoryLog> findAll();

    InventoryLog findById(int logId);

    List<InventoryLog> findByItemId(int itemId);

    List<InventoryLog> findByAuthorityId(int authorityId);

    InventoryLog add(InventoryLog log);

    boolean update(InventoryLog log);

    boolean deleteById(int logId);
}