package re.api.controllers;

import org.springframework.web.bind.annotation.*;
import re.api.domain.PurchaseOrderService;
import re.api.models.PurchaseOrder;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {
    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<PurchaseOrder> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PurchaseOrder findById(@PathVariable int id) {
        return service.findById(id);
    }
}
