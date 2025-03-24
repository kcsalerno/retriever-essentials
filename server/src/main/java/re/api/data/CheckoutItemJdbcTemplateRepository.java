package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.CheckoutItemMapper;
import re.api.models.CheckoutItem;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class CheckoutItemJdbcTemplateRepository implements CheckoutItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public CheckoutItemJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CheckoutItem findById(int checkoutItemId) {
        final String sql = """
                SELECT checkout_item_id, checkout_id, item_id, quantity
                FROM checkout_item
                WHERE checkout_item_id = ?
                """;

        return jdbcTemplate.query(sql, new CheckoutItemMapper(), checkoutItemId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CheckoutItem> findByCheckoutOrderId(int checkoutId) {
        final String sql = """
                SELECT checkout_item_id, checkout_id, item_id, quantity
                FROM checkout_item
                WHERE checkout_id = ?
                """;

        return jdbcTemplate.query(sql, new CheckoutItemMapper(), checkoutId);
    }

    @Override
    public List<Map<String, Object>> findPopularItems() {
        final String sql = """
        SELECT i.item_name, SUM(ci.quantity) AS total_quantity
        FROM checkout_item ci
        JOIN item i ON ci.item_id = i.item_id
        GROUP BY i.item_name
        ORDER BY total_quantity DESC
        LIMIT 5;
        """;

        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> findPopularCategories() {
        final String sql = """
        SELECT i.category, SUM(ci.quantity) AS total_quantity
        FROM checkout_item ci
        JOIN item i ON ci.item_id = i.item_id
        GROUP BY i.category
        ORDER BY total_quantity DESC
        LIMIT 5;
        """;

        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public CheckoutItem add(CheckoutItem checkoutItem) {
        final String sql = """
                INSERT INTO checkout_item (checkout_id, item_id, quantity)
                VALUES (?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, checkoutItem.getCheckoutOrderId());
            ps.setInt(2, checkoutItem.getItemId());
            ps.setInt(3, checkoutItem.getQuantity());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        checkoutItem.setCheckoutItemId(keyHolder.getKey().intValue());
        return checkoutItem;
    }

    @Override
    public boolean update(CheckoutItem checkoutItem) {
        final String sql = """
                UPDATE checkout_item
                SET checkout_id = ?, item_id = ?, quantity = ?
                WHERE checkout_item_id = ?
                """;

        return jdbcTemplate.update(sql,
                checkoutItem.getCheckoutOrderId(),
                checkoutItem.getItemId(),
                checkoutItem.getQuantity(),
                checkoutItem.getCheckoutItemId()) > 0;
    }

    @Override
    public boolean deleteById(int checkoutItemId) {
        final String sql = """
                DELETE FROM checkout_item
                WHERE checkout_item_id = ?
                """;

        return jdbcTemplate.update(sql, checkoutItemId) > 0;
    }

    @Override
    public boolean deleteByCheckoutOrderId(int checkoutOrderId) {
        final String sql = """
            DELETE FROM checkout_item
            WHERE checkout_id = ?
            """;

        return jdbcTemplate.update(sql, checkoutOrderId) > 0;
    }
}
