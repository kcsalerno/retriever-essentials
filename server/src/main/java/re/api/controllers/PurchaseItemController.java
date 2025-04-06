package re.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.api.domain.PurchaseItemService;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.models.PurchaseItem;

@RestController
@RequestMapping("/api/purchase-item")
@CrossOrigin
public class PurchaseItemController {

    private final PurchaseItemService service;

    public PurchaseItemController(PurchaseItemService service) {
        this.service = service;
    }

    @GetMapping("/{purchaseItemId}")
    public ResponseEntity<PurchaseItem> findById(@PathVariable int purchaseItemId) {
        PurchaseItem purchaseItem = service.findById(purchaseItemId);
        if (purchaseItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(purchaseItem);
    }

    @PutMapping("/{purchaseItemId}")
    public ResponseEntity<Object> update(@PathVariable int purchaseItemId, @RequestBody PurchaseItem purchaseItem) {
        if (purchaseItemId != purchaseItem.getPurchaseItemId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<PurchaseItem> result = service.update(purchaseItem);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{purchaseItemId}")
    public ResponseEntity<Void> deleteById(@PathVariable int purchaseItemId) {
        Result<PurchaseItem> result = service.deleteById(purchaseItemId);

        if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (result.getType() == ResultType.INVALID) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
