package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.CheckoutOrderMapper;
import re.api.models.CheckoutOrder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class CheckoutOrderJdbcTemplateRepository implements CheckoutOrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public CheckoutOrderJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CheckoutOrder> findAll() {
        final String sql = """
                 SELECT checkout_id, student_id, authority_id, self_checkout, checkout_date
                 FROM checkout_order;
                 """;

        return jdbcTemplate.query(sql, new CheckoutOrderMapper());
    }

    @Override
    public CheckoutOrder findById(int checkoutId) {
        final String sql = """
                SELECT checkout_id, student_id, authority_id, self_checkout, checkout_date
                FROM checkout_order
                WHERE checkout_id = ?;
                """;

        return jdbcTemplate.query(sql, new CheckoutOrderMapper(), checkoutId).stream()
                .findFirst().orElse(null);
    }

    @Override
    public List<Map<String, Object>> findHourlyCheckoutSummary() {
        final String sql = """
                SELECT
                    DAYNAME(checkout_date) AS day,
                    HOUR(checkout_date) AS hour,
                    COUNT(*) AS total_checkouts
                FROM checkout_order
                GROUP BY day, hour
                ORDER BY
                    FIELD(day, 'Sunday', 'Monday', 'Tuesday', 'Wednesday','Thursday', 'Friday', 'Saturday'), hour;
                """;

        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public CheckoutOrder add(CheckoutOrder checkoutOrder) {
        final String sql = """
                INSERT INTO checkout_order (student_id, authority_id, self_checkout, checkout_date)
                VALUES (?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, checkoutOrder.getStudentId());
            ps.setInt(2, checkoutOrder.getAuthorityId());
            ps.setBoolean(3, checkoutOrder.isSelfCheckout());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(checkoutOrder.getCheckoutDate()));
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        checkoutOrder.setCheckoutOrderId(keyHolder.getKey().intValue());
        return checkoutOrder;
    }

    @Override
    public boolean update(CheckoutOrder checkoutOrder) {
        final String sql = """
                UPDATE checkout_order
                SET student_id = ?, authority_id = ?, self_checkout = ?, checkout_date = ?
                WHERE checkout_id = ?;
                """;

        return jdbcTemplate.update(sql,
                checkoutOrder.getStudentId(),
                checkoutOrder.getAuthorityId(),
                checkoutOrder.isSelfCheckout(),
                checkoutOrder.getCheckoutDate(),
                checkoutOrder.getCheckoutOrderId()) > 0;
    }

    @Override
    public boolean deleteById(int checkoutId) {
        final String sql = """
                DELETE FROM checkout_order
                WHERE checkout_id = ?;
                """;

        return jdbcTemplate.update(sql, checkoutId) > 0;
    }
}