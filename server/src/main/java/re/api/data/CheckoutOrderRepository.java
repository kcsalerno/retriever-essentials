package re.api.data;

import re.api.models.CheckoutOrder;
import java.util.List;
import java.util.Map;

public interface CheckoutOrderRepository {
    List<CheckoutOrder> findAll();

    CheckoutOrder findById(int checkoutId);

    List<Map<String, Object>> findHourlyCheckoutSummary();

    CheckoutOrder add(CheckoutOrder checkoutOrder);

    boolean update(CheckoutOrder checkoutOrder);

    boolean deleteById(int checkoutId);
}
