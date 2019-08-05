package dbtools.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemID;

    @Transient
    private int itemExcelID;

    @Column(name = "ITEM_PART_NO")
    private String itemPartNo;

    @Column(name = "ITEM_MANUFACTURER")
    private String itemManufacturer;

    @Column(name = "ITEM_TYPE")
    private String itemType;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "item_attributes_link",
            joinColumns = { @JoinColumn(name = "ITEM_ID") },
            inverseJoinColumns = { @JoinColumn(name = "ITEM_ATT_ID") }
    )
    private Set<ItemAttribute> itemAttributes = new HashSet<>();

    @Transient
    private List<Fitment> fitments = new ArrayList<>();

    @Override
    public String toString() {
        return "Item{" +
                "itemID=" + itemID +
                ", itemExcelID=" + itemExcelID +
                ", itemPartNo='" + itemPartNo + '\'' +
                ", itemManufacturer='" + itemManufacturer + '\'' +
                ", itemType='" + itemType + '\'' +
                '}';
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getItemExcelID() {
        return itemExcelID;
    }

    public void setItemExcelID(int itemExcelID) {
        this.itemExcelID = itemExcelID;
    }

    public String getItemPartNo() {
        return itemPartNo;
    }

    public void setItemPartNo(String itemPartNo) {
        this.itemPartNo = itemPartNo;
    }

    public String getItemManufacturer() {
        return itemManufacturer;
    }

    public void setItemManufacturer(String itemManufacturer) {
        this.itemManufacturer = itemManufacturer;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Set<ItemAttribute> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Set<ItemAttribute> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    public List<Fitment> getFitments() {
        return fitments;
    }

    public void setFitments(List<Fitment> fitments) {
        this.fitments = fitments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return itemPartNo.equals(item.itemPartNo) &&
                itemManufacturer.equals(item.itemManufacturer) &&
                itemType.equals(item.itemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemPartNo, itemManufacturer, itemType);
    }
}
