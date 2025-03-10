package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.ItemMapper;
import re.api.data.mappers.VendorMapper;
import re.api.models.Vendor;

import java.util.List;

@Repository
public class VendorJdbcTemplateRepository implements VendorRepository{
    private final JdbcTemplate jdbcTemplate;

    public VendorJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Vendor> findAll() {
       final String sql = "SELECT * FROM vendor;";
       return jdbcTemplate.query(sql, new VendorMapper());
    }

    @Override
    public Vendor findById(int itemId) {
        final String sql = "SELECT * FROM vendor WHERE vendor_id = ?;";
        return jdbcTemplate.query(sql, new VendorMapper(), itemId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Vendor findByName(String name) {
        final String sql = "SELECT * FROM vendor WHERE vendor_name = ?;";
        return jdbcTemplate.query(sql, new VendorMapper(), name)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Vendor add(Vendor vendor) {
        final String sql = """
                INSERT INTO vendor (vendor_name, phone_number, contact_email)
                VALUES (?, ?, ?);
                """;

        int rowsAffected = jdbcTemplate.update(sql,
                vendor.getVendorName(),
                vendor.getPhoneNumber(),
                vendor.getContactEmail());

        if (rowsAffected > 0) {
            return vendor;
        }
        return null;
    }

    @Override
    public boolean update(Vendor vendor) {
        final String sql = """
                UPDATE vendor SET vendor_name = ?, phone_number = ?, contact_email = ? WHERE vendor_id = ?;
                """;

        return jdbcTemplate.update(sql,
                vendor.getVendorName(),
                vendor.getPhoneNumber(),
                vendor.getContactEmail(),
                vendor.getVendorId()) > 0;
    }

    @Override
    public boolean deleteById(int vendorId) {
       final String sql = "DELETE FROM vendor WHERE vendor_id = ?;";
       return jdbcTemplate.update(sql, vendorId) > 0;
    }
}
