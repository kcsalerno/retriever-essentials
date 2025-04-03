package re.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.api.domain.CheckoutItemService;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.models.CheckoutItem;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout-item")
@CrossOrigin
public class CheckoutItemController {

    private final CheckoutItemService service;

    public CheckoutItemController(CheckoutItemService service) {
        this.service = service;
    }

    @GetMapping("/{checkoutItemId}")
    public ResponseEntity<CheckoutItem> findById(@PathVariable int checkoutItemId) {
        CheckoutItem checkoutItem = service.findById(checkoutItemId);
        if (checkoutItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(checkoutItem);
    }

    @GetMapping("/popular-items")
    public List<Map<String, Object>> findPopularItems() {
        return service.findPopularItems();
    }

    @GetMapping("/popular-categories")
    public List<Map<String, Object>> findPopularCategories() {
        return service.findPopularCategories();
    }

    @PutMapping("/{checkoutItemId}")
    public ResponseEntity<Object> update(@PathVariable int checkoutItemId, @RequestBody CheckoutItem checkoutItem) {
        if (checkoutItemId != checkoutItem.getCheckoutItemId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<CheckoutItem> result = service.update(checkoutItem);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{checkoutItemId}")
    public ResponseEntity<Void> deleteById(@PathVariable int checkoutItemId) {
        Result<CheckoutItem> result = service.deleteById(checkoutItemId);

        if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
