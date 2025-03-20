//package re.api.data.mappers;
//
//import re.api.models.CheckoutOrder;
//import org.springframework.jdbc.core.RowMapper;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class CheckoutOrderMapper implements RowMapper<CheckoutOrder> {
//    @Override
//    public CheckoutOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
//        CheckoutOrder checkoutOrder = new CheckoutOrder();
//        checkoutOrder.setCheckoutId(rs.getInt("checkout_id"));
//        checkoutOrder.setStudentId(rs.getString("student_id"));
//        checkoutOrder.setAuthorityId(rs.getInt("authority_id"));
//        checkoutOrder.setSelfCheckout(rs.getBoolean("self_checkout"));
//        checkoutOrder.setCheckoutDate(rs.getTimestamp("checkout_date").toLocalDateTime());
//        return checkoutOrder;
//    }
//}
