package dbtools.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    @Column(name = "CAR_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int carID;

    @Transient
    private int carExcelID;

    @Column(name = "YEAR_START")
    private int yearStart;

    @Column(name = "YEAR_FINISH")
    private int yearFinish;

    @Column(name = "CAR_MAKE")
    private String make;

    @Column(name = "CAR_MODEL")
    private String model;

    @Column(name = "CAR_SUBMODEL")
    private String subModel = "base";

    @Column(name = "CAR_DRIVE")
    private String drive = "";

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "car_attributes_link",
            joinColumns = { @JoinColumn(name = "CAR_ID") },
            inverseJoinColumns = { @JoinColumn(name = "CAR_ATT_ID") }
    )
    private Set<CarAttribute> attributes = new HashSet<>();

    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "car")
    @Transient
    private Set<Fitment> fitments = new HashSet<>();

    @Override
    public String toString() {
        return "Car{" +
                "carID=" + carID +
                ", carExcelID=" + carExcelID +
                ", yearStart=" + yearStart +
                ", yearFinish=" + yearFinish +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", subModel='" + subModel + '\'' +
                ", drive='" + drive + '\'' +
                '}';
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public int getCarExcelID() {
        return carExcelID;
    }

    public void setCarExcelID(int carExcelID) {
        this.carExcelID = carExcelID;
    }

    public int getYearStart() {
        return yearStart;
    }

    public void setYearStart(int yearStart) {
        this.yearStart = yearStart;
    }

    public int getYearFinish() {
        return yearFinish;
    }

    public void setYearFinish(int yearFinish) {
        this.yearFinish = yearFinish;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public Set<CarAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<CarAttribute> attributes) {
        this.attributes = attributes;
    }

    public Set<Fitment> getFitments() {
        return fitments;
    }
    public void setFitments(Set<Fitment> fitments) {
        this.fitments = fitments;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return yearStart == car.yearStart &&
                yearFinish == car.yearFinish &&
                make.equals(car.make) &&
                model.equals(car.model) &&
                subModel.equals(car.subModel) &&
                drive.equals(car.drive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yearStart, yearFinish, make, model, subModel, drive);
    }
}
