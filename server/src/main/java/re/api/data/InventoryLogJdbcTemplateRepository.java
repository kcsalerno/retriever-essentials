package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.InventoryLogMapper;
import re.api.models.InventoryLog;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class InventoryLogJdbcTemplateRepository implements InventoryLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public InventoryLogJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<InventoryLog> findAll() {
        final String sql = """
               SELECT log_id, authority_id, item_id, quantity_change, reason, time_stamp
               FROM inventory_log
               ORDER BY time_stamp DESC
               """;

        return jdbcTemplate.query(sql, new InventoryLogMapper());
    }

    @Override
    public List<InventoryLog> findByItemId(int itemId) {
        final String sql = """
                SELECT log_id, authority_id, item_id, quantity_change, reason, time_stamp
                FROM inventory_log
                WHERE item_id = ?
                ORDER BY time_stamp DESC
                """;

        return jdbcTemplate.query(sql, new InventoryLogMapper(), itemId);
    }

    @Override
    public List<InventoryLog> findByAuthorityId(int authorityId) {
        final String sql = """
                SELECT log_id, authority_id, item_id, quantity_change, reason, time_stamp
                FROM inventory_log
                WHERE authority_id = ?
                ORDER BY time_stamp DESC
                """;

        return jdbcTemplate.query(sql, new InventoryLogMapper(), authorityId);
    }

    @Override
    public InventoryLog findById(int logId) {
        final String sql = """
                SELECT log_id, authority_id, item_id, quantity_change, reason, time_stamp
                FROM inventory_log
                WHERE log_id = ?
                """;

        return jdbcTemplate.query(sql, new InventoryLogMapper(), logId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public InventoryLog add(InventoryLog inventoryLog) {
        final String sql = """
                INSERT INTO inventory_log (authority_id, item_id, quantity_change, reason, time_stamp)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, inventoryLog.getAuthorityId());
            ps.setInt(2, inventoryLog.getItemId());
            ps.setInt(3, inventoryLog.getQuantityChange());
            ps.setString(4, inventoryLog.getReason());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(inventoryLog.getTimeStamp()));
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        inventoryLog.setLogId(keyHolder.getKey().intValue());
        return inventoryLog;
    }

    @Override
    public boolean update(InventoryLog inventoryLog) {
        final String sql = """
                UPDATE inventory_log
                SET authority_id = ?, item_id = ?, quantity_change = ?, reason = ?, time_stamp = ?
                WHERE log_id = ?
                """;

        return jdbcTemplate.update(sql,
                inventoryLog.getAuthorityId(),
                inventoryLog.getItemId(),
                inventoryLog.getQuantityChange(),
                inventoryLog.getReason(),
                inventoryLog.getTimeStamp(),
                inventoryLog.getLogId()) > 0;
    }

    @Override
    public boolean deleteById(int logId) {
        final String sql = """
                DELETE FROM inventory_log
                WHERE log_id = ?
                """;

        return jdbcTemplate.update(sql, logId) > 0;
    }
}