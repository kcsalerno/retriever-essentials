package re.api.models;

import java.math.BigDecimal;
import java.util.Objects;

public class Item {
    private int itemId;
    private String itemName;
    private String itemDescription;
    private String nutritionFacts;
    private String picturePath;
    private String category;
    private int currentCount;
    private BigDecimal pricePerUnit;

    public Item() {}

    public Item(int itemId, String itemName, String itemDescription, String nutritionFacts,
                String picturePath, String category, int currentCount, BigDecimal pricePerUnit) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.nutritionFacts = nutritionFacts;
        this.picturePath = picturePath;
        this.category = category;
        this.currentCount = currentCount;
        this.pricePerUnit = pricePerUnit;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getNutritionFacts() {
        return nutritionFacts;
    }

    public void setNutritionFacts(String nutritionFacts) {
        this.nutritionFacts = nutritionFacts;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return itemId == item.itemId && Objects.equals(itemName, item.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, itemName);
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", nutritionFacts='" + nutritionFacts + '\'' +
                ", picturePath='" + picturePath + '\'' +
                ", category='" + category + '\'' +
                ", currentCount=" + currentCount +
                ", pricePerUnit=" + pricePerUnit +
                '}';
    }
}
