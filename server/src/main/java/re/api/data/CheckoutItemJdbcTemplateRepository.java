//package re.api.data;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//import re.api.data.mappers.CheckoutItemMapper;
//import re.api.models.CheckoutItem;
//import java.util.List;
//
//@Repository
//public class CheckoutItemJdbcTemplateRepository implements CheckoutItemRepository {
//    private final JdbcTemplate jdbcTemplate;
//
//    public CheckoutItemJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public List<CheckoutItem> findByCheckoutId(int checkoutId) {
//        final String sql = "SELECT checkout_item_id, checkout_id, item_id, quantity FROM checkout_item WHERE checkout_id = ?;";
//        return jdbcTemplate.query(sql, new CheckoutItemMapper(), checkoutId);
//    }
//
//    @Override
//    public CheckoutItem add(CheckoutItem checkoutItem) {
//        final String sql = "INSERT INTO checkout_item (checkout_id, item_id, quantity) VALUES (?, ?, ?);";
//        int rowsAffected = jdbcTemplate.update(sql,
//                checkoutItem.getCheckoutId(),
//                checkoutItem.getItemId(),
//                checkoutItem.getQuantity());
//
//        if (rowsAffected <= 0) {
//            return null;
//        }
//
//        int generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
//        checkoutItem.setCheckoutItemId(generatedId);
//        return checkoutItem;
//    }
//
//    @Override
//    public boolean update(CheckoutItem checkoutItem) {
//        final String sql = "UPDATE checkout_item SET checkout_id = ?, item_id = ?, quantity = ? WHERE checkout_item_id = ?;";
//        return jdbcTemplate.update(sql,
//                checkoutItem.getCheckoutId(),
//                checkoutItem.getItemId(),
//                checkoutItem.getQuantity(),
//                checkoutItem.getCheckoutItemId()) > 0;
//    }
//
//    @Override
//    public boolean deleteById(int checkoutItemId) {
//        final String sql = "DELETE FROM checkout_item WHERE checkout_item_id = ?;";
//        return jdbcTemplate.update(sql, checkoutItemId) > 0;
//    }
//}