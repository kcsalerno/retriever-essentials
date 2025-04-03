package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.ItemMapper;
import re.api.models.Item;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ItemJdbcTemplateRepository implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Item> findAll() {
        final String sql = """
                SELECT item_id, item_name, item_description, nutrition_facts,
                       picture_path, category, current_count, item_limit, price_per_unit, enabled
                FROM item;
                """;

        return jdbcTemplate.query(sql, new ItemMapper());
    }

    @Override
    public Item findById(int itemId) {
        final String sql = """
                SELECT item_id, item_name, item_description, nutrition_facts,
                       picture_path, category, current_count, item_limit, price_per_unit, enabled
                FROM item
                WHERE item_id = ?;
                """;

        return jdbcTemplate.query(sql, new ItemMapper(), itemId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item findByName(String itemName) {
        final String sql = """
                SELECT item_id, item_name, item_description, nutrition_facts,
                       picture_path, category, current_count, item_limit, price_per_unit, enabled
                FROM item
                WHERE item_name = ?;
                """;

        return jdbcTemplate.query(sql, new ItemMapper(), itemName)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item add(Item item) {
        final String sql = """
                INSERT INTO item (item_name, item_description, nutrition_facts,
                                  picture_path, category, current_count, item_limit, price_per_unit)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getItemName());
            ps.setString(2, item.getItemDescription());
            ps.setString(3, item.getNutritionFacts());
            ps.setString(4, item.getPicturePath());
            ps.setString(5, item.getCategory());
            ps.setInt(6, item.getCurrentCount());
            ps.setInt(7, item.getItemLimit());
            ps.setBigDecimal(8, item.getPricePerUnit());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        item.setItemId(keyHolder.getKey().intValue());
        return item;
    }

    @Override
    public boolean update(Item item) {
        final String sql = """
                UPDATE item
                SET item_name = ?, item_description = ?, nutrition_facts = ?,
                    picture_path = ?, category = ?, current_count = ?,
                    item_limit = ?, price_per_unit = ?, enabled = ?
                WHERE item_id = ?;
                """;

        return jdbcTemplate.update(sql,
                item.getItemName(),
                item.getItemDescription(),
                item.getNutritionFacts(),
                item.getPicturePath(),
                item.getCategory(),
                item.getCurrentCount(),
                item.getItemLimit(),
                item.getPricePerUnit(),
                item.isEnabled(),
                item.getItemId()) > 0;
    }

    @Override
    public boolean disableById(int itemId) {
        final String sql = """
                UPDATE item
                SET enabled = FALSE
                WHERE item_id = ?;
                """;

        return jdbcTemplate.update(sql, itemId) > 0;
    }
}
