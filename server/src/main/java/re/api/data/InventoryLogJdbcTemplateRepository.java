package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.InventoryLogMapper;
import re.api.models.InventoryLog;
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
            SELECT il.*, au.email AS authority_email, i.item_name
            FROM inventory_log il
            LEFT JOIN app_user au ON il.authority_id = au.app_user_id
            JOIN item i ON il.item_id = i.item_id
            ORDER BY il.time_stamp DESC
        """;
        return jdbcTemplate.query(sql, new InventoryLogMapper());
    }

    @Override
    public List<InventoryLog> findByItemName(String itemName) {
        final String sql = """
            SELECT il.*, au.email AS authority_email, i.item_name
            FROM inventory_log il
            LEFT JOIN app_user au ON il.authority_id = au.app_user_id
            JOIN item i ON il.item_id = i.item_id
            WHERE i.item_name LIKE ?
            ORDER BY il.time_stamp DESC
        """;
        return jdbcTemplate.query(sql, new InventoryLogMapper(), "%" + itemName + "%");
    }

    @Override
    public List<InventoryLog> findByAuthorityEmail(String authorityEmail) {
        final String sql = """
            SELECT il.*, au.email AS authority_email, i.item_name
            FROM inventory_log il
            LEFT JOIN app_user au ON il.authority_id = au.app_user_id
            JOIN item i ON il.item_id = i.item_id
            WHERE au.email LIKE ?
            ORDER BY il.time_stamp DESC
        """;
        return jdbcTemplate.query(sql, new InventoryLogMapper(), "%" + authorityEmail + "%");
    }

    @Override
    public InventoryLog findById(int logId) {
        final String sql = """
            SELECT il.*, au.email AS authority_email, i.item_name
            FROM inventory_log il
            LEFT JOIN app_user au ON il.authority_id = au.app_user_id
            JOIN item i ON il.item_id = i.item_id
            WHERE il.log_id = ?
        """;
        return jdbcTemplate.query(sql, new InventoryLogMapper(), logId)
                .stream().findFirst().orElse(null);
    }

    @Override
    public InventoryLog add(InventoryLog log) {
        final String sql = """
            INSERT INTO inventory_log (authority_id, item_id, quantity_change, reason, time_stamp)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql, log.getAuthorityId(), log.getItemId(), log.getQuantityChange(), log.getReason(), log.getTimeStamp());
        return log;
    }

    @Override
    public boolean update(InventoryLog log) {
        final String sql = """
            UPDATE inventory_log
            SET authority_id = ?, item_id = ?, quantity_change = ?, reason = ?, time_stamp = ?
            WHERE log_id = ?
        """;
        return jdbcTemplate.update(sql, log.getAuthorityId(), log.getItemId(), log.getQuantityChange(), log.getReason(), log.getTimeStamp(), log.getLogId()) > 0;
    }

    @Override
    public boolean deleteById(int logId) {
        final String sql = "DELETE FROM inventory_log WHERE log_id = ?";
        return jdbcTemplate.update(sql, logId) > 0;
    }
}