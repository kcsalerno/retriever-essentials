package re.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.api.domain.CheckoutOrderService;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.models.CheckoutOrder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout-order")
@CrossOrigin
public class CheckoutOrderController {

    private final CheckoutOrderService service;

    public CheckoutOrderController(CheckoutOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<CheckoutOrder> findAll() {
        return service.findAll();
    }

    @GetMapping("/{checkoutOrderId}")
    public ResponseEntity<CheckoutOrder> findById(@PathVariable int checkoutOrderId) {
        CheckoutOrder order = service.findById(checkoutOrderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/busiest")
    public List<Map<String, Object>> findBusiestHours() {
        return service.findTopBusiestHours();
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHORITY')")
    public ResponseEntity<Object> add(@RequestBody CheckoutOrder checkoutOrder) {
        Result<CheckoutOrder> result = service.add(checkoutOrder);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{checkoutOrderId}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHORITY')")
    public ResponseEntity<Object> update(@PathVariable int checkoutOrderId, @RequestBody CheckoutOrder checkoutOrder) {
        if (checkoutOrderId != checkoutOrder.getCheckoutOrderId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<CheckoutOrder> result = service.update(checkoutOrder);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{checkoutOrderId}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHORITY')")
    public ResponseEntity<Void> deleteById(@PathVariable int checkoutOrderId) {
        Result<CheckoutOrder> result = service.deleteById(checkoutOrderId);

        if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
