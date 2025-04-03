package re.api.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.api.data.AppUserRepository;
import re.api.data.InventoryLogRepository;
import re.api.data.ItemRepository;
import re.api.models.AppUser;
import re.api.models.InventoryLog;
import re.api.models.Item;

import java.util.List;

@Service
public class InventoryLogService {
    private static final int MAX_REASON_LENGTH = 255; // Enforcing a reasonable limit

    private final InventoryLogRepository logRepository;
    private final ItemRepository itemRepository;
    private final AppUserRepository appUserRepository;

    public InventoryLogService(InventoryLogRepository logRepository,
                               ItemRepository itemRepository,
                               AppUserRepository appUserRepository) {
        this.logRepository = logRepository;
        this.itemRepository = itemRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<InventoryLog> findAll() {
        List<InventoryLog> inventoryLogList = logRepository.findAll();
        if (inventoryLogList == null || inventoryLogList.isEmpty()) {
            return inventoryLogList;
        }

        for (InventoryLog inventoryLog : inventoryLogList) {
            enrichLogWithItemAndAuthority(inventoryLog);
        }

        return inventoryLogList;
    }

    public InventoryLog findById(int logId) {
        InventoryLog inventoryLog = logRepository.findById(logId);
        if (inventoryLog != null) {
            enrichLogWithItemAndAuthority(inventoryLog);
        }

        return inventoryLog;
    }

    public List<InventoryLog> findByItemId(int itemId) {
        List<InventoryLog> inventoryLogList = logRepository.findByItemId(itemId);
        if (inventoryLogList == null || inventoryLogList.isEmpty()) {
            return inventoryLogList;
        }

        for (InventoryLog inventoryLog : inventoryLogList) {
            enrichLogWithItemAndAuthority(inventoryLog);
        }

        return inventoryLogList;
    }

    public List<InventoryLog> findByAuthorityId(int authorityId) {
        List<InventoryLog> inventoryLogList = logRepository.findByAuthorityId(authorityId);
        if (inventoryLogList == null || inventoryLogList.isEmpty()) {
            return inventoryLogList;
        }

        for (InventoryLog inventoryLog : inventoryLogList) {
            enrichLogWithItemAndAuthority(inventoryLog);
        }

        return inventoryLogList;
    }

    public List<InventoryLog> findByItemName(String itemName) {
        Item item = itemRepository.findByName(itemName);
        if (item == null) {
            return List.of();
        }

        List<InventoryLog> inventoryLogList = logRepository.findByItemId(item.getItemId());

        if (inventoryLogList == null || inventoryLogList.isEmpty()) {
            return inventoryLogList;
        }

        for (InventoryLog inventoryLog : inventoryLogList) {
            enrichLogWithItemAndAuthority(inventoryLog);
        }

        return inventoryLogList;
    }

    public List<InventoryLog> findByAuthorityEmail(String authorityEmail) {
        Integer authorityId = appUserRepository.findIdByEmail(authorityEmail);
        if (authorityId == null) {
            return List.of();
        }

        List<InventoryLog> inventoryLogList = logRepository.findByAuthorityId(authorityId);

        if (inventoryLogList == null || inventoryLogList.isEmpty()) {
            return inventoryLogList;
        }

        for (InventoryLog inventoryLog : inventoryLogList) {
            enrichLogWithItemAndAuthority(inventoryLog);
        }

        return inventoryLogList;
    }

    @Transactional
    public Result<InventoryLog> add(InventoryLog inventoryLog) {
        Result<InventoryLog> result = validate(inventoryLog);

        if (!result.isSuccess()) {
            return result;
        }

        if (inventoryLog.getLogId() != 0) {
            result.addMessage(ResultType.INVALID, "Inventory Log ID cannot be set for add operation.");
        }

        InventoryLog addedLog = logRepository.add(inventoryLog);

        if (addedLog == null) {
            result.addMessage(ResultType.INVALID, "Failed to add inventory log.");
            return result;
        }

        result.setPayload(addedLog);
        return result;
    }

    @Transactional
    public Result<InventoryLog> update(InventoryLog inventoryLog) {
        Result<InventoryLog> result = validate(inventoryLog);

        if (!result.isSuccess()) {
            return result;
        }

        if (inventoryLog.getLogId() <= 0) {
            result.addMessage(ResultType.INVALID, "Inventory Log ID must be set for update.");
            return result;
        }

        boolean updated = logRepository.update(inventoryLog);

        if (!updated) {
            result.addMessage(ResultType.NOT_FOUND, "Inventory Log ID not found.");
        } else {
            result.setPayload(inventoryLog);
        }

        return result;
    }

    @Transactional
    public Result<InventoryLog> deleteById(int logId) {
        Result<InventoryLog> result = new Result<>();

        if (!logRepository.deleteById(logId)) {
            result.addMessage(ResultType.NOT_FOUND, "Inventory Log ID not found.");
        }

        return result;
    }

    private Result<InventoryLog> validate(InventoryLog inventoryLog) {
        Result<InventoryLog> result = new Result<>();

        if (inventoryLog == null) {
            result.addMessage(ResultType.INVALID, "Inventory log cannot be null.");
            return result;
        }

        AppUser authority = null;
        if (inventoryLog.getAuthorityId() <= 0) {
            result.addMessage(ResultType.INVALID, "Invalid authority ID.");
        } else {
            authority = appUserRepository.findById(inventoryLog.getAuthorityId());
            if (authority == null || !authority.isEnabled()) {
                result.addMessage(ResultType.NOT_FOUND, "Authority ID does not exist or is disabled.");
            }
        }

        Item item = null;
        if (inventoryLog.getItemId() <= 0) {
            result.addMessage(ResultType.INVALID, "Valid item ID is required.");
        } else {
            item = itemRepository.findById(inventoryLog.getItemId());
            if (item == null || !item.isEnabled()) {
                result.addMessage(ResultType.NOT_FOUND, "Item ID does not exist or is disabled.");
            }
        }

        if (inventoryLog.getQuantityChange() == 0) {
            result.addMessage(ResultType.INVALID, "Quantity change cannot be zero.");
        }

        if (Validations.isNullOrBlank(inventoryLog.getReason())) {
            result.addMessage(ResultType.INVALID, "Reason for inventory change is required.");
        } else if (inventoryLog.getReason().length() > MAX_REASON_LENGTH) {
            result.addMessage(ResultType.INVALID, "Reason must not exceed " + MAX_REASON_LENGTH + " characters.");
        }

        if (inventoryLog.getTimeStamp() == null) {
            result.addMessage(ResultType.INVALID, "Log date is required.");
        }

        List<InventoryLog> existingLogs = logRepository.findAll();
        for (InventoryLog existingLog : existingLogs) {
            if (existingLog.equals(inventoryLog)
                    && existingLog.getLogId() != inventoryLog.getLogId()) {
                result.addMessage(ResultType.INVALID, "Duplicate log entry detected.");
                break;
            }
        }

        return result;
    }

    private void enrichLogWithItem(InventoryLog inventoryLog) {
        if (inventoryLog.getItemId() > 0) {
            Item item = itemRepository.findById(inventoryLog.getItemId());
            if (item != null) {
                inventoryLog.setItem(item);
            }
        }
    }

    private void enrichLogWithAuthority(InventoryLog inventoryLog) {
        if (inventoryLog.getAuthorityId() > 0) {
            AppUser authority = appUserRepository.findById(inventoryLog.getAuthorityId());
            if (authority != null) {
                inventoryLog.setAuthority(authority);
            }
        }
    }

    private void enrichLogWithItemAndAuthority (InventoryLog inventoryLog) {
        enrichLogWithItem(inventoryLog);
        enrichLogWithAuthority(inventoryLog);
    }
}