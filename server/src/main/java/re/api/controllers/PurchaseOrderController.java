package re.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.api.domain.PurchaseOrderService;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.models.PurchaseOrder;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
@CrossOrigin
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<PurchaseOrder> findAll() {
        return service.findAll();
    }

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseOrder> findById(@PathVariable int purchaseId) {
        PurchaseOrder purchaseOrder = service.findById(purchaseId);
        if (purchaseOrder == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(purchaseOrder);
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") — add when security is configured
    public ResponseEntity<Object> add(@RequestBody PurchaseOrder purchaseOrder) {
        Result<PurchaseOrder> result = service.add(purchaseOrder);

        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }

        return ErrorResponse.build(result);
    }

    @PutMapping("/{purchaseId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> update(@PathVariable int purchaseId, @RequestBody PurchaseOrder purchaseOrder) {
        if (purchaseId != purchaseOrder.getPurchaseId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<PurchaseOrder> result = service.update(purchaseOrder);

        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{purchaseId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable int purchaseId) {
        Result<PurchaseOrder> result = service.deleteById(purchaseId);

        if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
