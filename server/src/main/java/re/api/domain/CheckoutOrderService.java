package re.api.domain;

import org.springframework.stereotype.Service;
import re.api.data.CheckoutOrderRepository;
import re.api.models.CheckoutOrder;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutOrderService {
    private final CheckoutOrderRepository repository;

    public CheckoutOrderService(CheckoutOrderRepository repository) {
        this.repository = repository;
    }

    public List<CheckoutOrder> findAll() {
        return repository.findAll();
    }

    public List<Map<String, Object>> getTopBusiestHours() {
        return repository.findTopBusiestHours();
    }

    public CheckoutOrder findById(int checkoutId) {
        return repository.findById(checkoutId);
    }

    public CheckoutOrder add(CheckoutOrder checkoutOrder) {
        return repository.add(checkoutOrder);
    }

    public boolean update(CheckoutOrder checkoutOrder) {
        return repository.update(checkoutOrder);
    }

    public boolean deleteById(int checkoutId) {
        return repository.deleteById(checkoutId);
    }
}
