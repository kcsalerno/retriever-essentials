package re.api.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import re.api.models.Item;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setItemDescription(rs.getString("item_description"));
        item.setNutritionFacts(rs.getString("nutrition_facts"));
        item.setPicturePath(rs.getString("picture_path"));
        item.setCategory(rs.getString("category"));
        item.setCurrentCount(rs.getInt("current_count"));
        item.setItemLimit(rs.getInt("item_limit"));
        BigDecimal price = rs.getBigDecimal("price_per_unit"); // Ensure null safety for price_per_unit
        item.setPricePerUnit(price != null ? price : BigDecimal.ZERO);
        item.setEnabled(rs.getBoolean("enabled"));
        return item;
    }
}
