package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.PurchaseOrderMapper;
import re.api.models.PurchaseOrder;
import java.util.List;

@Repository
public class PurchaseOrderJdbcTemplateRepository implements PurchaseOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public PurchaseOrderJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PurchaseOrder> findAll() {
        final String sql = "SELECT purchase_id, admin_id, vendor_id, purchase_date FROM purchase_order;";
        return jdbcTemplate.query(sql, new PurchaseOrderMapper());
    }

    @Override
    public PurchaseOrder findById(int purchaseId) {
        final String sql = "SELECT purchase_id, admin_id, vendor_id, purchase_date FROM purchase_order WHERE purchase_id = ?;";
        return jdbcTemplate.query(sql, new PurchaseOrderMapper(), purchaseId).stream()
                .findFirst().orElse(null);
    }

    @Override
    public PurchaseOrder add(PurchaseOrder purchaseOrder) {
        final String sql = "INSERT INTO purchase_order (admin_id, vendor_id, purchase_date) VALUES (?, ?, ?);";
        int rowsAffected = jdbcTemplate.update(sql,
                purchaseOrder.getAdminId(),
                purchaseOrder.getVendorId(),
                purchaseOrder.getPurchaseDate());

        if (rowsAffected <= 0) {
            return null;
        }

        int generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        purchaseOrder.setPurchaseId(generatedId);
        return purchaseOrder;
    }

    @Override
    public boolean update(PurchaseOrder purchaseOrder) {
        final String sql = "UPDATE purchase_order SET admin_id = ?, vendor_id = ?, purchase_date = ? WHERE purchase_id = ?;";
        return jdbcTemplate.update(sql,
                purchaseOrder.getAdminId(),
                purchaseOrder.getVendorId(),
                purchaseOrder.getPurchaseDate(),
                purchaseOrder.getPurchaseId()) > 0;
    }

    @Override
    public boolean deleteById(int purchaseId) {
        final String sql = "DELETE FROM purchase_order WHERE purchase_id = ?;";
        return jdbcTemplate.update(sql, purchaseId) > 0;
    }
}
