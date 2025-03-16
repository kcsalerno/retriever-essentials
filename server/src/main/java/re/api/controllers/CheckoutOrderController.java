package re.api.controllers;

import org.springframework.web.bind.annotation.*;
import re.api.domain.CheckoutOrderService;
import re.api.models.CheckoutOrder;
import java.util.List;

@RestController
@RequestMapping("/api/checkout-order")
public class CheckoutOrderController {
    private final CheckoutOrderService service;

    public CheckoutOrderController(CheckoutOrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<CheckoutOrder> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CheckoutOrder findById(@PathVariable int id) {
        return service.findById(id);
    }
}
