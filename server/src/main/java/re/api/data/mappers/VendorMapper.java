package re.api.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import re.api.models.Vendor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VendorMapper implements RowMapper<Vendor> {
    @Override
    public Vendor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setVendorId(rs.getInt("vendor_id"));
        vendor.setVendorName(rs.getString("vendor_name"));
        vendor.setPhoneNumber(rs.getString("phone_number"));
        vendor.setContactEmail(rs.getString("contact_email"));
        vendor.setEnabled(rs.getBoolean("enabled"));
        return vendor;
    }
}
