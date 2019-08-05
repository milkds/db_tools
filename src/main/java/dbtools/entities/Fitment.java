package dbtools.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fitments")
public class Fitment {

    @Id
    @Column(name = "FIT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fitmentID;

    @Transient
    private int fitmentExcelID;

    @Transient
    private int carExcelID;

    @Transient
    private int itemExcelID;

    @Column(name = "CAR_ID")
    private int carID;

    @Column(name = "ITEM_ID")
    private int itemID;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "fitment_attributes_link",
            joinColumns = { @JoinColumn(name = "FIT_ID") },
            inverseJoinColumns = { @JoinColumn(name = "FIT_ATT_ID") }
    )
    private Set<FitmentAttribute> fitmentAttributes = new HashSet<>();

    @Transient
    private Item item;

    @Transient
    private Car car;

    @Override
    public String toString() {
        return "Fitment{" +
                "fitmentID=" + fitmentID +
                ", fitmentExcelID=" + fitmentExcelID +
                ", carExcelID=" + carExcelID +
                ", itemExcelID=" + itemExcelID +
                ", carID=" + carID +
                ", itemID=" + itemID +
                '}';
    }

    public int getFitmentID() {
        return fitmentID;
    }

    public void setFitmentID(int fitmentID) {
        this.fitmentID = fitmentID;
    }

    public int getFitmentExcelID() {
        return fitmentExcelID;
    }

    public void setFitmentExcelID(int fitmentExcelID) {
        this.fitmentExcelID = fitmentExcelID;
    }

    public int getCarExcelID() {
        return carExcelID;
    }

    public void setCarExcelID(int carExcelID) {
        this.carExcelID = carExcelID;
    }

    public int getItemExcelID() {
        return itemExcelID;
    }

    public void setItemExcelID(int itemExcelID) {
        this.itemExcelID = itemExcelID;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public Set<FitmentAttribute> getFitmentAttributes() {
        return fitmentAttributes;
    }

    public void setFitmentAttributes(Set<FitmentAttribute> fitmentAttributes) {
        this.fitmentAttributes = fitmentAttributes;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
