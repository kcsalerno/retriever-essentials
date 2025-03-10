package re.api.controllers;

import org.springframework.web.bind.annotation.*;
import re.api.domain.ItemService;
import re.api.models.Item;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<Item> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Item findById(@PathVariable int id) {
        return service.findById(id);
    }

    @GetMapping("/popular")
    public List<Item> findMostPopularItems() {
        return service.findMostPopularItems();
    }
}
