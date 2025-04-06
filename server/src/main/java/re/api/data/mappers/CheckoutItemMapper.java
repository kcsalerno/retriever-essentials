package re.api.data.mappers;

import re.api.models.CheckoutItem;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckoutItemMapper implements RowMapper<CheckoutItem> {
    @Override
    public CheckoutItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        CheckoutItem checkoutItem = new CheckoutItem();
        checkoutItem.setCheckoutItemId(rs.getInt("checkout_item_id"));
        checkoutItem.setCheckoutOrderId(rs.getInt("checkout_id"));
        checkoutItem.setItemId(rs.getInt("item_id"));
        checkoutItem.setQuantity(rs.getInt("quantity"));
        return checkoutItem;
    }
}
