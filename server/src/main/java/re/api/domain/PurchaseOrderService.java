package re.api.domain;

import org.springframework.stereotype.Service;
import re.api.data.PurchaseOrderRepository;
import re.api.models.PurchaseOrder;
import java.util.List;

@Service
public class PurchaseOrderService {
    private final PurchaseOrderRepository repository;

    public PurchaseOrderService(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    public List<PurchaseOrder> findAll() {
        return repository.findAll();
    }

    public PurchaseOrder findById(int purchaseId) {
        return repository.findById(purchaseId);
    }

    public PurchaseOrder add(PurchaseOrder purchaseOrder) {
        return repository.add(purchaseOrder);
    }

    public boolean update(PurchaseOrder purchaseOrder) {
        return repository.update(purchaseOrder);
    }

    public boolean deleteById(int purchaseId) {
        return repository.deleteById(purchaseId);
    }
}
