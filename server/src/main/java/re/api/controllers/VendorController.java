package re.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.api.domain.VendorService;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.models.Vendor;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/vendor")
public class VendorController {
    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vendor> findAll() {
        return service.findAll();
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<Vendor> findById(@PathVariable int vendorId) {
        Vendor vendor = service.findById(vendorId);
        if (vendor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vendor);
    }

    @GetMapping("/{vendorName}")
    public ResponseEntity<Vendor> findByName(@PathVariable String vendorName) {
        Vendor vendor = service.findByName(vendorName);
        if (vendor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vendor);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody Vendor vendor) {
        Result<Vendor> result = service.add(vendor);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{vendorId}")
    public ResponseEntity<Object> update(@PathVariable int vendorId, @RequestBody Vendor vendor) {
        if (vendorId != vendor.getVendorId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Vendor> result = service.update(vendor);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{vendorId}")
    public ResponseEntity<Void> disableById(@PathVariable int vendorId) {
        Result<Vendor> result = service.disableById(vendorId);
        if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}