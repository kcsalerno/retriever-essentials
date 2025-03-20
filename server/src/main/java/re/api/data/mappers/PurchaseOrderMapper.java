//package re.api.data.mappers;
//
//import re.api.models.PurchaseOrder;
//import org.springframework.jdbc.core.RowMapper;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class PurchaseOrderMapper implements RowMapper<PurchaseOrder> {
//    @Override
//    public PurchaseOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
//        PurchaseOrder purchaseOrder = new PurchaseOrder();
//        purchaseOrder.setPurchaseId(rs.getInt("purchase_id"));
//        purchaseOrder.setAdminId(rs.getInt("admin_id"));
//        purchaseOrder.setVendorId(rs.getInt("vendor_id"));
//        purchaseOrder.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
//        return purchaseOrder;
//    }
//}
