//package re.api.data.mappers;
//
//import re.api.models.PurchaseItem;
//import org.springframework.jdbc.core.RowMapper;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class PurchaseItemMapper implements RowMapper<PurchaseItem> {
//    @Override
//    public PurchaseItem mapRow(ResultSet rs, int rowNum) throws SQLException {
//        PurchaseItem purchaseItem = new PurchaseItem();
//        purchaseItem.setPurchaseItemId(rs.getInt("purchase_item_id"));
//        purchaseItem.setPurchaseId(rs.getInt("purchase_id"));
//        purchaseItem.setItemId(rs.getInt("item_id"));
//        purchaseItem.setQuantity(rs.getInt("quantity"));
//        return purchaseItem;
//    }
//}