package dbtools.service;

import dbtools.dao.FitmentDAO;
import dbtools.dao.ItemDAO;
import dbtools.entities.*;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FitmentService {
    public void checkFitmentsForCar(Session session, Car car) {
        Set<Fitment> rawFitments = car.getFitments();
        if (rawFitments.size()==0){
            return;
        }
        rawFitments.forEach(fitment -> {
            fitment.setCarID(car.getCarID());
            Item checkedItem = new ItemService().checkItem(session, fitment.getItem());
            fitment.setItemID(checkedItem.getItemID());   //will return item saved to db
            Fitment dbFitment = FitmentDAO.getExistingFitment (session, fitment);
            Set<FitmentAttribute> checkedAttributes = checkFitmentAttributes(session, fitment.getFitmentAttributes());
            if (dbFitment==null){
                fitment.setFitmentAttributes(checkedAttributes);
            }
            else {
                dbFitment.getFitmentAttributes().addAll(checkedAttributes);
                fitment = dbFitment;
            }
            FitmentDAO.saveFitment(fitment, session);
        });
    }

    private Set<FitmentAttribute> checkFitmentAttributes(Session session, Set<FitmentAttribute> fitmentAttributes) {
        Set<FitmentAttribute> finalAttributes = new HashSet<>();
        fitmentAttributes.forEach(attribute->{
            FitmentAttribute testAttribute = FitmentDAO.getExistingAttribute(attribute, session);
            finalAttributes.add(Objects.requireNonNullElse(testAttribute, attribute));
        });

        return finalAttributes;
    }
}
