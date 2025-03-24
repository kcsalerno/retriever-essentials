package re.api.data;

import re.api.models.CheckoutItem;
import java.util.List;
import java.util.Map;

public interface CheckoutItemRepository {
    CheckoutItem findById(int checkoutItemId);

    List<CheckoutItem> findByCheckoutOrderId(int checkoutOrderId);

    List<Map<String, Object>> findPopularItems();

    List<Map<String, Object>> findPopularCategories();

    CheckoutItem add(CheckoutItem checkoutItem);

    boolean update(CheckoutItem checkoutItem);

    boolean deleteById(int checkoutItemId);

    boolean deleteByCheckoutOrderId(int checkoutId);
}
