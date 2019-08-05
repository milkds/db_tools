package dbtools.service;

import dbtools.HibernateUtil;
import dbtools.dao.CarDAO;
import dbtools.entities.Car;
import dbtools.entities.CarAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CarService {
    private static final Logger logger = LogManager.getLogger(CarService.class.getName());

    public void saveCars(Set<Car> cars){
        logger.debug("Saving cars");
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            cars.forEach(car -> {
                Car dbCar = CarDAO.getExistingCar(session, car);
                Set<CarAttribute> checkedAttributes = checkCarAttributes(session, car.getAttributes());
                if (dbCar==null){
                    car.setAttributes(checkedAttributes);
                }
                else {
                    dbCar.getAttributes().addAll(checkedAttributes);
                    dbCar.getFitments().addAll(car.getFitments());
                    car = dbCar; //will move this call till fitment and items checked
                }
                CarDAO.saveCar(car, session);
                new FitmentService().checkFitmentsForCar(session, car);
            });
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }

        session.close();
    }

    private Set<CarAttribute> checkCarAttributes(Session session, Set<CarAttribute> attributes) {
        Set<CarAttribute> finalAttributes = new HashSet<>();
        attributes.forEach(attribute->{
            CarAttribute testAttribute = CarDAO.getExistingAttribute(attribute, session);
            finalAttributes.add(Objects.requireNonNullElse(testAttribute, attribute));
        });

        return finalAttributes;
    }
}
