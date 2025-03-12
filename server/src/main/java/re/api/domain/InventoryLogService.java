package re.api.domain;

import org.springframework.stereotype.Service;
import re.api.data.InventoryLogRepository;
import re.api.models.InventoryLog;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.domain.Validations;

import java.util.List;

@Service
public class InventoryLogService {
    private static final int MAX_REASON_LENGTH = 255; // Enforcing a reasonable limit

    private final InventoryLogRepository repository;

    public InventoryLogService(InventoryLogRepository repository) {
        this.repository = repository;
    }

    public List<InventoryLog> findAll() {
        return repository.findAll();
    }

    public List<InventoryLog> findByItemName(String itemName) {
        return repository.findByItemName(itemName);
    }

    public List<InventoryLog> findByAuthorityEmail(String authorityEmail) {
        return repository.findByAuthorityEmail(authorityEmail);
    }

    public InventoryLog findById(int logId) {
        return repository.findById(logId);
    }

    public Result<InventoryLog> add(InventoryLog log) {
        Result<InventoryLog> result = validate(log);
        if (!result.isSuccess()) {
            return result;
        }
        log = repository.add(log);
        result.setPayload(log);
        return result;
    }

    public Result<InventoryLog> update(InventoryLog log) {
        Result<InventoryLog> result = validate(log);
        if (!result.isSuccess()) {
            return result;
        }
        if (log.getLogId() <= 0) {
            result.addMessage(ResultType.INVALID, "Log ID must be set for update.");
        }
        if (result.isSuccess()) {
            if (!repository.update(log)) {
                result.addMessage(ResultType.NOT_FOUND, "Log ID not found.");
            }
        }
        return result;
    }

    public Result<InventoryLog> deleteById(int logId) {
        Result<InventoryLog> result = new Result<>();
        if (!repository.deleteById(logId)) {
            result.addMessage(ResultType.NOT_FOUND, "Log ID not found.");
        }
        return result;
    }

    private Result<InventoryLog> validate(InventoryLog log) {
        Result<InventoryLog> result = new Result<>();

        if (log == null) {
            result.addMessage(ResultType.INVALID, "Inventory log cannot be null.");
            return result;
        }

        if (log.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Valid item ID is required.");
        }

        if (log.getQuantityChange() == 0) {
            result.addMessage(ResultType.INVALID, "Quantity change cannot be zero.");
        }

        if (Validations.isNullOrBlank(log.getReason())) {
            result.addMessage(ResultType.INVALID, "Reason for inventory change is required.");
        } else if (log.getReason().length() > MAX_REASON_LENGTH) {
            result.addMessage(ResultType.INVALID, "Reason must not exceed " + MAX_REASON_LENGTH + " characters.");
        }

        if (log.getAuthorityId() != null && log.getAuthorityId() <= 0) {
            result.addMessage(ResultType.INVALID, "Invalid authority ID.");
        }

        return result;
    }
}