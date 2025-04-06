package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.PurchaseOrderMapper;
import re.api.models.PurchaseOrder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PurchaseOrderJdbcTemplateRepository implements PurchaseOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public PurchaseOrderJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PurchaseOrder> findAll() {
        final String sql = """
                SELECT purchase_id, admin_id, vendor_id, purchase_date
                FROM purchase_order;
                """;

        return jdbcTemplate.query(sql, new PurchaseOrderMapper());
    }

    @Override
    public PurchaseOrder findById(int purchaseId) {
        final String sql = """
                SELECT purchase_id, admin_id, vendor_id, purchase_date FROM purchase_order
                WHERE purchase_id = ?;
                """;

        return jdbcTemplate.query(sql, new PurchaseOrderMapper(), purchaseId).stream()
                .findFirst().orElse(null);
    }

    @Override
    public PurchaseOrder add(PurchaseOrder purchaseOrder) {
        final String sql = """
            INSERT INTO purchase_order (admin_id, vendor_id, purchase_date)
            VALUES (?, ?, ?);
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, purchaseOrder.getAdminId());
            ps.setInt(2, purchaseOrder.getVendorId());
            ps.setObject(3, purchaseOrder.getPurchaseDate());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        purchaseOrder.setPurchaseId(keyHolder.getKey().intValue());
        return purchaseOrder;
    }

    @Override
    public boolean update(PurchaseOrder purchaseOrder) {
        final String sql = """
                UPDATE purchase_order
                SET admin_id = ?, vendor_id = ?, purchase_date = ?
                WHERE purchase_id = ?;
                """;

        return jdbcTemplate.update(sql,
                purchaseOrder.getAdminId(),
                purchaseOrder.getVendorId(),
                purchaseOrder.getPurchaseDate(),
                purchaseOrder.getPurchaseId()) > 0;
    }

    @Override
    public boolean deleteById(int purchaseId) {
        final String sql = """
                DELETE FROM purchase_order
                WHERE purchase_id = ?;
                """;

        return jdbcTemplate.update(sql, purchaseId) > 0;
    }
}
