package re.api.data;

import re.api.models.CheckoutOrder;
import java.util.List;

public interface CheckoutOrderRepository {
    List<CheckoutOrder> findAll();

    CheckoutOrder findById(int checkoutId);

    CheckoutOrder add(CheckoutOrder checkoutOrder);

    boolean update(CheckoutOrder checkoutOrder);

    boolean deleteById(int checkoutId);
}
