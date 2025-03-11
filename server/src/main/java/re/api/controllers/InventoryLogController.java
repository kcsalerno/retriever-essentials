package re.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.api.domain.InventoryLogService;
import re.api.models.InventoryLog;
import re.api.domain.Result;
import re.api.domain.ResultType;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-log")
public class InventoryLogController {
    private final InventoryLogService service;

    public InventoryLogController(InventoryLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<InventoryLog> findAll() {
        return service.findAll();
    }

    @GetMapping("/item/{itemName}")
    public List<InventoryLog> findByItemName(@PathVariable String itemName) {
        return service.findByItemName(itemName);
    }

    @GetMapping("/authority/{authorityEmail}")
    public List<InventoryLog> findByAuthorityEmail(@PathVariable String authorityEmail) {
        return service.findByAuthorityEmail(authorityEmail);
    }

    @GetMapping("/{logId}")
    public ResponseEntity<InventoryLog> findById(@PathVariable int logId) {
        InventoryLog log = service.findById(logId);
        return log != null ? ResponseEntity.ok(log) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody InventoryLog log) {
        Result<InventoryLog> result = service.add(log);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{logId}")
    public ResponseEntity<Object> update(@PathVariable int logId, @RequestBody InventoryLog log) {
        if (logId != log.getLogId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<InventoryLog> result = service.update(log);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteById(@PathVariable int logId) {
        Result<InventoryLog> result = service.deleteById(logId);
        if (result.getType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}