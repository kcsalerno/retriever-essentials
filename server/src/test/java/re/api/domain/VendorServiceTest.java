package re.api.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import re.api.data.VendorRepository;
import re.api.models.Vendor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class VendorServiceTest {

    @MockitoBean
    private VendorRepository vendorRepository;

    @Autowired
    private VendorService vendorService;

    @Test
    void shouldFindAll() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        // When
        when(vendorRepository.findAll()).thenReturn(testVendors);
        // Then
        List<Vendor> vendors = vendorService.findAll();
        assertNotNull(vendors);
        assertEquals(3, vendors.size());
        assertEquals(testVendors, vendors);
        assertEquals("Vendor A", vendors.getFirst().getVendorName());
    }

    @Test
    void shouldFindById() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        // When
        when(vendorRepository.findById(1)).thenReturn(testVendors.getFirst());
        // Then
        Vendor vendor = vendorService.findById(1);
        assertNotNull(vendor);
        assertEquals(1, vendor.getVendorId());
        assertEquals("Vendor A", vendor.getVendorName());
    }

    @Test
    void shouldNotFindById() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        // When
        when(vendorRepository.findById(4)).thenReturn(null);
        // Then
        Vendor vendor = vendorService.findById(4);
        assertNull(vendor);
    }

    @Test
    void shouldFindByName() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        // When
        when(vendorRepository.findByName("Vendor A")).thenReturn(testVendors.getFirst());
        // Then
        Vendor vendor = vendorService.findByName("Vendor A");
        assertNotNull(vendor);
        assertEquals(1, vendor.getVendorId());
        assertEquals("Vendor A", vendor.getVendorName());
        assertEquals("vendora@contact.email", vendor.getContactEmail());
    }

    @Test
    void shouldNotFindByName() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        // When
        when(vendorRepository.findByName("Vendor D")).thenReturn(null);
        // Then
        Vendor vendor = vendorService.findByName("Vendor D");
        assertNull(vendor);
    }

    @Test
    void shouldAdd() {
        // Given
        Vendor newVendor = new Vendor(0, "Vendor D", "111-222-3333",
                "vendord@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(newVendor, result.getPayload());
    }

    @Test
    void shouldNotAddWithIdSet() {
        // Given
        Vendor newVendor = new Vendor(1, "Vendor D", "111-222-3333",
                "vendord@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor ID cannot be set for `add` operation.", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithNullVendor() {
        // Given
        Vendor newVendor = null;
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor cannot be null", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithNullOrBlankName() {
        // Given
        Vendor newVendor = new Vendor(0, "", "111-222-3333",
                "vendord@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor name is required", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithLongName() {
        // Given
        String longName = "Vendor" + "A".repeat(256);
        Vendor newVendor = new Vendor(0, longName, "111-222-3333",
                "testvendor@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor name is too long", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithNullOrBlankEmail() {
        // Given
        Vendor newVendor = new Vendor(0, "Vendor D", "111-222-3333",
                "", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor contact email is required", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithInvalidEmail() {
        // Given
        Vendor newVendor = new Vendor(0, "Vendor D", "111-222-3333",
                "invalid-email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor contact email is invalid", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithEmailTooLong() {
        // Given
        String longEmail = "vendor" + "d".repeat(255) + "@email.com";
        Vendor newVendor = new Vendor(0, "Vendor D", "111-222-3333",
                longEmail, true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor contact email must be 255 characters or less", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithNullOrBlankPhoneNumber() {
        // Given
        Vendor newVendor = new Vendor(0, "Vendor D", "",
                "vendord@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor phone number is required", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithInvalidPhoneNumber() {
        // Given
        Vendor newVendor = new Vendor(0, "Vendor D", "(9) 999-999-9999-9999",
                "vendord@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor phone number must be 20 characters or less", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithDuplicateVendor() {
        // Given
        Vendor newVendor = new Vendor(0, "Vendor A", "123-456-7890",
                "vendora@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        when(vendorRepository.findAll()).thenReturn(makeTestVendors());
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Duplicate vendors are not allowed", result.getMessages().getFirst());
    }

    @Test
    void shouldNotAddWithDuplicateVendorName() {
        // Given
        Vendor newVendor = new Vendor(0, "Vendor A", "123-456-7890",
                "vendora@contact.email", true);
        // When
        when(vendorRepository.add(newVendor)).thenReturn(newVendor);
        when(vendorRepository.findAll()).thenReturn(makeTestVendors());
        // Then
        Result<Vendor> result = vendorService.add(newVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Duplicate vendors are not allowed", result.getMessages().getFirst());
    }

    @Test
    void shouldUpdate() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        Vendor updatedVendor = testVendors.get(1);
        updatedVendor.setVendorName("Updated Vendor B");
        // When
        when(vendorRepository.update(updatedVendor)).thenReturn(true);
        when(vendorRepository.findById(2)).thenReturn(updatedVendor);
        // Then
        Result<Vendor> result = vendorService.update(updatedVendor);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(updatedVendor, result.getPayload());
        assertEquals("Updated Vendor B", vendorRepository.findById(2).getVendorName());
    }

    @Test
    void shouldNotUpdateWithIdNotSet() {
        // Given
        Vendor updatedVendor = new Vendor(0, "Updated Vendor A", "123-456-7890",
                "vendora@contact.email", true);
        // When
        when(vendorRepository.update(updatedVendor)).thenReturn(true);
        // Then
        Result<Vendor> result = vendorService.update(updatedVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor ID must be set for `update` operation.", result.getMessages().getFirst());
    }

    @Test
    void shouldNotUpdateWithIdNotFound() {
        // Given
        Vendor updatedVendor = new Vendor(4, "Updated Vendor A", "123-456-7890",
                "vendord@contact.email", true);
        // When
        when(vendorRepository.update(updatedVendor)).thenReturn(false);
        // Then
        Result<Vendor> result = vendorService.update(updatedVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor ID not found.", result.getMessages().getFirst());
    }

    @Test
    void shouldNotUpdateWithNullOrBlankName() {
        // Given
        Vendor updatedVendor = new Vendor(1, "", "123-456-7890",
                "vendora@contact.email", true);
        // When
        when(vendorRepository.update(updatedVendor)).thenReturn(true);
        // Then
        Result<Vendor> result = vendorService.update(updatedVendor);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor name is required", result.getMessages().getFirst());
    }

    @Test
    void shouldDisableById() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        Vendor disabledVendor = testVendors.get(2);
        disabledVendor.setEnabled(false);
        // When
        when(vendorRepository.disableById(3)).thenReturn(true);
        when(vendorRepository.findById(3)).thenReturn(disabledVendor);
        // Then
        Result<Vendor> result = vendorService.disableById(3);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        Vendor vendor = vendorService.findById(3);
        assertNotNull(vendor);
        assertFalse(vendor.isEnabled());
    }

    @Test
    void shouldNotDisableById() {
        // Given
        List<Vendor> testVendors = makeTestVendors();
        // When
        when(vendorRepository.disableById(4)).thenReturn(false);
        // Then
        Result<Vendor> result = vendorService.disableById(4);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getType());
        assertEquals(1, result.getMessages().size());
        assertEquals("Vendor ID not found.", result.getMessages().getFirst());
    }

    private List<Vendor> makeTestVendors() {
        return List.of(
                new Vendor(1, "Vendor A", "123-456-7890",
                        "vendora@contact.email", true),
                new Vendor(2, "Vendor B", "987-654-3210",
                        "vendorb@contact.email", true),
                new Vendor(3, "Vendor C", "555-555-5555",
                        "vendorc@contact.email", true)
        );
    }
}