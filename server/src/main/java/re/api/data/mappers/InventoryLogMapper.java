package re.api.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import re.api.models.InventoryLog;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryLogMapper implements RowMapper<InventoryLog> {
    @Override
    public InventoryLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setLogId(rs.getInt("log_id"));
        inventoryLog.setAuthorityId(rs.getInt("authority_id"));
        inventoryLog.setItemId(rs.getInt("item_id"));
        inventoryLog.setQuantityChange(rs.getInt("quantity_change"));
        inventoryLog.setReason(rs.getString("reason"));
        inventoryLog.setTimeStamp(rs.getTimestamp("time_stamp").toLocalDateTime());
        return inventoryLog;
    }
}