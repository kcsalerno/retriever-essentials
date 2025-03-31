package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.VendorRepository;
import re.api.models.Vendor;

import java.util.List;

@Service
public class VendorService {
    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) { this.vendorRepository = vendorRepository; }

    public List<Vendor> findAll() { return vendorRepository.findAll(); }

    public Vendor findById(int id) { return vendorRepository.findById(id); }

    public Vendor findByName(String name) { return vendorRepository.findByName(name); }

    @Transactional
    public Result<Vendor> add(Vendor vendor) {
        Result<Vendor> result = validate(vendor);

        if (!result.isSuccess()) {
            return result;
        }
        if (vendor.getVendorId() != 0) {
            result.addMessage(ResultType.INVALID, "Vendor ID cannot be set for `add` operation.");
        }
        if (result.isSuccess()) {
            vendor = vendorRepository.add(vendor);
            result.setPayload(vendor);
        }

        return result;
    }

    @Transactional
    public Result<Vendor> update(Vendor vendor) {
        Result<Vendor> result = validate(vendor);

        if (!result.isSuccess()) {
            return result;
        }
        if (vendor.getVendorId() <= 0) {
            result.addMessage(ResultType.INVALID, "Vendor ID must be set for `update` operation.");
        }
        if (result.isSuccess()) {
            if (vendorRepository.update(vendor)) {
                result.setPayload(vendor);
            } else {
                result.addMessage(ResultType.INVALID, "Vendor ID not found.");
            }
        }

        return result;
    }

    @Transactional
    public Result<Vendor> deleteById(int vendorId) {
        Result<Vendor> result = new Result<>();

        if (!vendorRepository.deleteById(vendorId)) {
            result.addMessage(ResultType.INVALID, "Vendor ID not found..");
        }

        return result;
    }

    public Result<Vendor> validate(Vendor vendor) {
        Result<Vendor> result = new Result<>();

        if (vendor == null) {
            result.addMessage(ResultType.INVALID, "Vendor cannot be null");
            return result;
        }
        if (Validations.isNullOrBlank(vendor.getVendorName())) {
            result.addMessage(ResultType.INVALID, "Vendor name is required");
        } else if (vendor.getVendorName().length() > 255) {
            result.addMessage(ResultType.INVALID, "Vendor name is too long");
        }
        if (Validations.isNullOrBlank(vendor.getContactEmail())) {
            result.addMessage(ResultType.INVALID, "Vendor contact email is required");
        } else if (vendor.getContactEmail().length() > 255) {
            result.addMessage(ResultType.INVALID, "Vendor contact email is too long");
        } else if (!Validations.isValidEmail(vendor.getContactEmail())) {
            result.addMessage(ResultType.INVALID, "Vendor contact email is not valid");
        }
        if (vendor.getPhoneNumber().length() > 20) {
            result.addMessage(ResultType.INVALID, "Phone number must be 20 characters or fewer");
        }

        List<Vendor> vendors = vendorRepository.findAll();
        for (Vendor existingVendor : vendors) {
//            if (existingVendor.getVendorName().equalsIgnoreCase(vendor.getVendorName())) {
//                result.addMessage(ResultType.INVALID, "Vendor name already exists");
//            }
            if (existingVendor.equals(vendor)) {
                result.addMessage(ResultType.INVALID, "Duplicate vendors are not allowed");
            }
        }

        return result;
    }
}
