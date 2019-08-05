package dbtools.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fitment_attributes")
public class FitmentAttribute {

    @Id
    @Column(name = "FIT_ATT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fitmentAttID;

    @Transient
    private int fitmentExcelID;

    @Column(name = "FIT_ATT_NAME")
    private String fitmentAttName;

    @Column(name = "FIT_ATT_VALUE")
    private String fitmentAttValue;

    @ManyToMany(mappedBy = "fitmentAttributes")
    private Set<Fitment> fitments = new HashSet<>();

    @Override
    public String toString() {
        return "FitmentAttribute{" +
                "fitmentAttID=" + fitmentAttID +
                ", fitmentExcelID=" + fitmentExcelID +
                ", fitmentAttName='" + fitmentAttName + '\'' +
                ", fitmentAttValue='" + fitmentAttValue + '\'' +
                '}';
    }

    public int getFitmentAttID() {
        return fitmentAttID;
    }

    public void setFitmentAttID(int fitmentAttID) {
        this.fitmentAttID = fitmentAttID;
    }

    public int getFitmentExcelID() {
        return fitmentExcelID;
    }

    public void setFitmentExcelID(int fitmentExcelID) {
        this.fitmentExcelID = fitmentExcelID;
    }

    public String getFitmentAttName() {
        return fitmentAttName;
    }

    public void setFitmentAttName(String fitmentAttName) {
        this.fitmentAttName = fitmentAttName;
    }

    public String getFitmentAttValue() {
        return fitmentAttValue;
    }

    public void setFitmentAttValue(String fitmentAttValue) {
        this.fitmentAttValue = fitmentAttValue;
    }

    public Set<Fitment> getFitments() {
        return fitments;
    }

    public void setFitments(Set<Fitment> fitments) {
        this.fitments = fitments;
    }
}
