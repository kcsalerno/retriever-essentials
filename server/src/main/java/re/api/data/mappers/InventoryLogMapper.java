package re.api.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import re.api.models.InventoryLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class InventoryLogMapper implements RowMapper<InventoryLog> {
    @Override
    public InventoryLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        InventoryLog log = new InventoryLog();
        log.setLogId(rs.getInt("log_id"));
        log.setAuthorityId(rs.getObject("authority_id") != null ? rs.getInt("authority_id") : null);
        log.setItemId(rs.getInt("item_id"));
        log.setQuantityChange(rs.getInt("quantity_change"));
        log.setReason(rs.getString("reason"));
        log.setTimeStamp(rs.getObject("time_stamp", LocalDateTime.class));
        // Fetch additional fields from the joined tables
        log.setAuthorityEmail(rs.getString("authority_email"));
        log.setItemName(rs.getString("item_name"));

        return log;
    }
}