package dbtools.dao;

import dbtools.entities.CarAttribute;
import dbtools.entities.Fitment;
import dbtools.entities.FitmentAttribute;
import dbtools.entities.Item;
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

public class FitmentDAO {
    private static final Logger logger = LogManager.getLogger(FitmentDAO.class.getName());

    public static Fitment getExistingFitment(Session session, Fitment fitment) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Fitment> crQ = builder.createQuery(Fitment.class);
        Root<Fitment> root = crQ.from(Fitment.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("carID"), fitment.getCarID()));
        predicates.add(builder.equal(root.get("itemID"), fitment.getItemID()));
        Predicate[] preds = predicates.toArray(new Predicate[0]);
        crQ.where(builder.and(preds));
        Query q = session.createQuery(crQ);
        Fitment testFitment = null;
        try {
            testFitment = (Fitment) q.getSingleResult();
            logger.debug("fitment exists " + testFitment);
        } catch (NoResultException e) {
            logger.debug("fitment doesn't exist " + fitment);
            return null;
        }

        return testFitment;
    }

    public static FitmentAttribute getExistingAttribute(FitmentAttribute attribute, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<FitmentAttribute> crQ = builder.createQuery(FitmentAttribute.class);
        Root<FitmentAttribute> root = crQ.from(FitmentAttribute.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("fitmentAttName"), attribute.getFitmentAttName()));
        predicates.add(builder.equal(root.get("fitmentAttValue"), attribute.getFitmentAttValue()));
        Predicate[] preds = predicates.toArray(new Predicate[0]);
        crQ.where(builder.and(preds));
        Query q = session.createQuery(crQ);
        FitmentAttribute testAtt = null;
        try {
            testAtt = (FitmentAttribute) q.getSingleResult();
            logger.debug("fitment attribute exists " + testAtt);
        } catch (NoResultException e) {
            logger.debug("fitment attribute doesn't exist " + attribute);
            return null;
        }

        return testAtt;
    }

    public static void saveFitment(Fitment fitment, Session session) {
        if (fitment.getFitmentID()==0){
            fitment.setFitmentID((Integer) session.save(fitment));
        }
        else {
            session.update(fitment);
        }
    }
}
