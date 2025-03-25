package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.PurchaseItemMapper;
import re.api.models.PurchaseItem;
import java.util.List;

@Repository
public class PurchaseItemJdbcTemplateRepository implements PurchaseItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public PurchaseItemJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PurchaseItem findById(int purchaseItemId) {
        final String sql = """
                SELECT purchase_item_id, purchase_id, item_id, quantity
                FROM purchase_item
                WHERE purchase_item_id = ?
                """;

        return jdbcTemplate.query(sql, new PurchaseItemMapper(), purchaseItemId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<PurchaseItem> findByPurchaseOrderId(int purchaseId) {
        final String sql = "SELECT purchase_item_id, purchase_id, item_id, quantity FROM purchase_item WHERE purchase_id = ?;";
        return jdbcTemplate.query(sql, new PurchaseItemMapper(), purchaseId);
    }

    @Override
    public PurchaseItem add(PurchaseItem purchaseItem) {
        final String sql = "INSERT INTO purchase_item (purchase_id, item_id, quantity) VALUES (?, ?, ?);";
        int rowsAffected = jdbcTemplate.update(sql,
                purchaseItem.getPurchaseOrderId(),
                purchaseItem.getItemId(),
                purchaseItem.getQuantity());

        if (rowsAffected <= 0) {
            return null;
        }

        int generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        purchaseItem.setPurchaseItemId(generatedId);
        return purchaseItem;
    }

    @Override
    public boolean update(PurchaseItem purchaseItem) {
        final String sql = "UPDATE purchase_item SET purchase_id = ?, item_id = ?, quantity = ? WHERE purchase_item_id = ?;";
        return jdbcTemplate.update(sql,
                purchaseItem.getPurchaseOrderId(),
                purchaseItem.getItemId(),
                purchaseItem.getQuantity(),
                purchaseItem.getPurchaseItemId()) > 0;
    }

    @Override
    public boolean deleteById(int purchaseItemId) {
        final String sql = "DELETE FROM purchase_item WHERE purchase_item_id = ?;";
        return jdbcTemplate.update(sql, purchaseItemId) > 0;
    }

    @Override
    public boolean deleteByPurchaseOrderId(int purchaseOrderId) {
        final String sql = "DELETE FROM purchase_item WHERE purchase_id = ?;";
        return jdbcTemplate.update(sql, purchaseOrderId) > 0;
    }
}