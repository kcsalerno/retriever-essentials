package re.api.data;

import org.junit.jupiter.api.BeforeEach;
import re.api.models.Vendor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VendorJdbcTemplateRepositoryTest {
    private final int VENDOR_COUNT = 2;

    @Autowired
    VendorJdbcTemplateRepository vendorJdbcTemplateRepository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindAll() {
        // Arrange
        // Act
        List<Vendor> vendors = vendorJdbcTemplateRepository.findAll();
        // Assert
        assertNotNull(vendors);
        assertFalse(vendors.isEmpty());
        assertTrue(vendors.size() == VENDOR_COUNT
                || vendors.size() == VENDOR_COUNT + 1);
    }

    @Test
    void shouldFindById() {
        // Arrange
        int vendorId = 1;
        // Act
        Vendor vendor = vendorJdbcTemplateRepository.findById(vendorId);
        // Assert
        assertNotNull(vendor);
        assertEquals(vendorId, vendor.getVendorId());
        assertEquals("Patel Brothers", vendor.getVendorName());
        assertEquals("contact@patelbros.com", vendor.getContactEmail());
    }

    @Test
    void shouldNotFindByBadId() {
        // Arrange
        int vendorId = 9999;
        // Act
        Vendor vendor = vendorJdbcTemplateRepository.findById(vendorId);
        // Assert
        assertNull(vendor);
    }

    @Test
    void shouldFindByName() {
        // Arrange
        String vendorName = "Patel Brothers";
        // Act
        Vendor vendor = vendorJdbcTemplateRepository.findByName(vendorName);
        // Assert
        assertNotNull(vendor);
        assertEquals(1, vendor.getVendorId());
        assertEquals(vendorName, vendor.getVendorName());
    }

    @Test
    void shouldNotFindByBadName() {
        // Arrange
        String vendorName = "Bad Vendor";
        // Act
        Vendor vendor = vendorJdbcTemplateRepository.findByName(vendorName);
        // Assert
        assertNull(vendor);
    }

    @Test
    void shouldAdd() {
        // Arrange
        Vendor testVendor = new Vendor();
        testVendor.setVendorName("Test Vendor");
        testVendor.setPhoneNumber("123-456-7890");
        testVendor.setContactEmail("test@email.com");

        // Act
        Vendor addedVendor = vendorJdbcTemplateRepository.add(testVendor);

        // Assert
        assertNotNull(addedVendor);
        assertEquals(testVendor.getVendorName(), addedVendor.getVendorName());
        assertEquals(VENDOR_COUNT + 1, addedVendor.getVendorId());
    }

    @Test
    void shouldUpdate() {
        // Arrange
        int vendorId = 2;
        Vendor vendorToUpdate = vendorJdbcTemplateRepository.findById(vendorId);
        assertNotNull(vendorToUpdate);
        vendorToUpdate.setVendorName("Updated Vendor");

        // Act
        boolean updated = vendorJdbcTemplateRepository.update(vendorToUpdate);

        // Assert
        assertTrue(updated);
        assertEquals("Updated Vendor", vendorJdbcTemplateRepository.findById(vendorId).getVendorName());
    }

    @Test
    void shouldNotUpdateBadId() {
        // Arrange
        Vendor vendorToUpdate = new Vendor();
        vendorToUpdate.setVendorId(9999);
        vendorToUpdate.setVendorName("Updated Vendor");

        // Act
        boolean updated = vendorJdbcTemplateRepository.update(vendorToUpdate);

        // Assert
        assertFalse(updated);
    }

    @Test
    void shouldDisableById() {
        // Arrange
        int vendorId = 3;
        Vendor vendorToDisable = vendorJdbcTemplateRepository.findById(vendorId);
        assertNotNull(vendorToDisable);
        assertTrue(vendorToDisable.isEnabled());

        // Act
        boolean disabled = vendorJdbcTemplateRepository.disableById(vendorId);

        // Assert
        assertTrue(disabled);
        assertFalse(vendorJdbcTemplateRepository.findById(vendorId).isEnabled());
    }

    @Test
    void shouldNotDisableByBadId() {
        // Arrange
        int vendorId = 9999;
        Vendor vendorToDisable = vendorJdbcTemplateRepository.findById(vendorId);
        assertNull(vendorToDisable);

        // Act
        boolean disabled = vendorJdbcTemplateRepository.disableById(vendorId);

        // Assert
        assertFalse(disabled);
    }
}