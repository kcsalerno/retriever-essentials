package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.ItemMapper;
import re.api.models.Item;

import java.util.List;

@Repository
public class ItemJdbcTemplateRepository implements ItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public ItemJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Item> findAll() {
        final String sql = "SELECT * FROM item;";
        return jdbcTemplate.query(sql, new ItemMapper());
    }

    @Override
    public List<Item> findMostPopularItems() {
        final String sql = """
                SELECT i.* FROM item i
                JOIN checkout_item ci ON i.item_id = ci.item_id
                GROUP BY i.item_id
                ORDER BY SUM(ci.quantity) DESC
                LIMIT 10;
                """;

        return jdbcTemplate.query(sql, new ItemMapper());
    }

    @Override
    public List<String> findMostPopularCategories() {
        String sql = """
            SELECT category 
            FROM item 
            JOIN checkout_item ON item.item_id = checkout_item.item_id
            GROUP BY category 
            ORDER BY COUNT(checkout_item.item_id) DESC
            LIMIT 5;
        """;
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public Item findById(int itemId) {
        final String sql = "SELECT * FROM item WHERE item_id = ?;";
        return jdbcTemplate.query(sql, new ItemMapper(), itemId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item findByName(String name) {
        final String sql = "SELECT * FROM item WHERE item_name = ?;";
        return jdbcTemplate.query(sql, new ItemMapper(), name)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item add(Item item) {
        final String sql = """
                INSERT INTO item (item_name, item_description, nutrition_facts, picture_path, category, current_count, price_per_unit)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        int rowsAffected = jdbcTemplate.update(sql,
                item.getItemName(),
                item.getItemDescription(),
                item.getNutritionFacts(),
                item.getPicturePath(),
                item.getCategory(),
                item.getCurrentCount(),
                item.getPricePerUnit());

        if (rowsAffected > 0) {
            return item;
        }
        return null;
    }

    @Override
    public boolean update(Item item) {
        final String sql = """
                UPDATE item SET item_name = ?, item_description = ?, nutrition_facts = ?, picture_path = ?,
                category = ?, current_count = ?, price_per_unit = ? WHERE item_id = ?;
                """;

        return jdbcTemplate.update(sql,
                item.getItemName(),
                item.getItemDescription(),
                item.getNutritionFacts(),
                item.getPicturePath(),
                item.getCategory(),
                item.getCurrentCount(),
                item.getPricePerUnit(),
                item.getItemId()) > 0;
    }

    @Override
    public boolean deleteById(int itemId) {
        final String sql = "DELETE FROM item WHERE item_id = ?;";
        return jdbcTemplate.update(sql, itemId) > 0;
    }
}