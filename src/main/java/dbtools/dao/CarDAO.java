package dbtools.dao;

import dbtools.entities.Car;
import dbtools.entities.CarAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {
    private static final Logger logger = LogManager.getLogger(CarDAO.class.getName());

    public static void saveCar(Car car, Session session){
        if (car.getCarID()==0){
            car.setCarID((Integer) session.save(car));
        }
       else {
           session.update(car);
        }
    }

    public static List<Car>getAllCars(Session session){


        return null;
    }

    //returns null, if car doesn't exist.
    public static Car getExistingCar(Session session, Car car) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Car> crQ = builder.createQuery(Car.class);
        Root<Car> root = crQ.from(Car.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("yearStart"), car.getYearStart()));
        predicates.add(builder.equal(root.get("yearFinish"), car.getYearFinish()));
        predicates.add(builder.equal(root.get("make"), car.getMake()));
        predicates.add(builder.equal(root.get("model"), car.getModel()));
        predicates.add(builder.equal(root.get("subModel"), car.getSubModel()));
        predicates.add(builder.equal(root.get("drive"), car.getDrive()));
        Predicate[] preds = predicates.toArray(new Predicate[0]);
        crQ.where(builder.and(preds));
        Query q = session.createQuery(crQ);
        Car testCar = null;
        try {
            testCar = (Car) q.getSingleResult();
            logger.debug("car exists " + testCar);
            logger.debug("car has attributes " + testCar.getAttributes().size());
        } catch (NoResultException e) {
            logger.debug("car doesn't exist " + car);
            return null;
        }

        return testCar;
    }

    //returns null, if attribute doesn't exist.
    public static CarAttribute getExistingAttribute(CarAttribute attribute, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<CarAttribute> crQ = builder.createQuery(CarAttribute.class);
        Root<CarAttribute> root = crQ.from(CarAttribute.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("carAttName"), attribute.getCarAttName()));
        predicates.add(builder.equal(root.get("carAttValue"), attribute.getCarAttValue()));
        Predicate[] preds = predicates.toArray(new Predicate[0]);
        crQ.where(builder.and(preds));
        Query q = session.createQuery(crQ);
        CarAttribute testAtt = null;
        try {
            testAtt = (CarAttribute) q.getSingleResult();
            logger.debug("car attribute exists " + testAtt);
        } catch (NoResultException e) {
            logger.debug("car attribute doesn't exist " + attribute);
            return null;
        }

        return testAtt;
    }
}
