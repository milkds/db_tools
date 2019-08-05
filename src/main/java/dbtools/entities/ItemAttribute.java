package dbtools.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "item_attributes")
public class ItemAttribute {
    @Id
    @Column(name = "ITEM_ATT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemAttID;

    @Transient
    private int itemExcelID;

    @Column(name = "ITEM_ATT_NAME")
    private String itemAttName;

    @Column(name = "ITEM_ATT_VALUE")
    private String itemAttValue;

    @ManyToMany(mappedBy = "itemAttributes")
    private Set<Item> items = new HashSet<>();

    @Override
    public String toString() {
        return "ItemAttribute{" +
                "itemAttID=" + itemAttID +
                ", itemExcelID=" + itemExcelID +
                ", itemAttName='" + itemAttName + '\'' +
                ", itemAttValue='" + itemAttValue + '\'' +
                '}';
    }

    public int getItemAttID() {
        return itemAttID;
    }

    public void setItemAttID(int itemAttID) {
        this.itemAttID = itemAttID;
    }

    public int getItemExcelID() {
        return itemExcelID;
    }

    public void setItemExcelID(int itemExcelID) {
        this.itemExcelID = itemExcelID;
    }

    public String getItemAttName() {
        return itemAttName;
    }

    public void setItemAttName(String itemAttName) {
        this.itemAttName = itemAttName;
    }

    public String getItemAttValue() {
        return itemAttValue;
    }

    public void setItemAttValue(String itemAttValue) {
        this.itemAttValue = itemAttValue;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }
}
