package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.VendorMapper;
import re.api.models.Vendor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class VendorJdbcTemplateRepository implements VendorRepository{
    private final JdbcTemplate jdbcTemplate;

    public VendorJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Vendor> findAll() {
       final String sql = """
                SELECT vendor_id, vendor_name, phone_number, contact_email, enabled
                FROM vendor;
                """;

       return jdbcTemplate.query(sql, new VendorMapper());
    }

    @Override
    public Vendor findById(int vendorId) {
        final String sql = """
                SELECT vendor_id, vendor_name, phone_number, contact_email, enabled
                FROM vendor
                WHERE vendor_id = ?;
                """;

        return jdbcTemplate.query(sql, new VendorMapper(), vendorId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Vendor findByName(String vendorName) {
        final String sql = """
                SELECT vendor_id, vendor_name, phone_number, contact_email, enabled
                FROM vendor
                WHERE vendor_name = ?;
                """;

        return jdbcTemplate.query(sql, new VendorMapper(), vendorName)
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

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, vendor.getVendorName());
            ps.setString(2, vendor.getPhoneNumber());
            ps.setString(3, vendor.getContactEmail());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        vendor.setVendorId(keyHolder.getKey().intValue());
        return vendor;
    }

    @Override
    public boolean update(Vendor vendor) {
        final String sql = """
                UPDATE vendor
                SET vendor_name = ?, phone_number = ?, contact_email = ?, enabled = ?
                WHERE vendor_id = ?;
                """;

        return jdbcTemplate.update(sql,
                vendor.getVendorName(),
                vendor.getPhoneNumber(),
                vendor.getContactEmail(),
                vendor.isEnabled(),
                vendor.getVendorId()) > 0;
    }

    @Override
    public boolean deleteById(int vendorId) {
       final String sql = """
               DELETE FROM vendor
               WHERE vendor_id = ?;
               """;

       return jdbcTemplate.update(sql, vendorId) > 0;
    }
}
