package re.api.data;

import re.api.models.Vendor;
import java.util.List;

public interface VendorRepository {
    List<Vendor> findAll();
    Vendor findById(int itemId);
    Vendor findByName(String name);
    Vendor add(Vendor vendor);
    boolean update(Vendor vendor);
    boolean deleteById(int vendorId);
}
