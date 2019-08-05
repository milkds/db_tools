package dbtools.dao;

import dbtools.entities.Car;
import dbtools.entities.CarAttribute;
import dbtools.entities.Item;
import dbtools.entities.ItemAttribute;
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

public class ItemDAO {
    private static final Logger logger = LogManager.getLogger(ItemDAO.class.getName());


    public static Item getExistingItem(Session session, Item item) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Item> crQ = builder.createQuery(Item.class);
        Root<Item> root = crQ.from(Item.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("itemPartNo"), item.getItemPartNo()));
        predicates.add(builder.equal(root.get("itemManufacturer"), item.getItemManufacturer()));
        predicates.add(builder.equal(root.get("itemType"), item.getItemType()));
        Predicate[] preds = predicates.toArray(new Predicate[0]);
        crQ.where(builder.and(preds));
        Query q = session.createQuery(crQ);
        Item testItem = null;
        try {
            testItem = (Item) q.getSingleResult();
            logger.debug("item exists " + testItem);
            logger.debug("item has attributes " + testItem.getItemAttributes().size());
        } catch (NoResultException e) {
            logger.debug("item doesn't exist " + item);
            return null;
        }

        return testItem;
    }

    public static ItemAttribute getExistingAttribute(ItemAttribute attribute, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ItemAttribute> crQ = builder.createQuery(ItemAttribute.class);
        Root<ItemAttribute> root = crQ.from(ItemAttribute.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("itemAttName"), attribute.getItemAttName()));
        predicates.add(builder.equal(root.get("itemAttValue"), attribute.getItemAttValue()));
        Predicate[] preds = predicates.toArray(new Predicate[0]);
        crQ.where(builder.and(preds));
        Query q = session.createQuery(crQ);
        ItemAttribute testAtt = null;
        try {
            testAtt = (ItemAttribute) q.getSingleResult();
            logger.debug("item attribute exists " + testAtt);
        } catch (NoResultException e) {
            logger.debug("item attribute doesn't exist " + attribute);
            return null;
        }

        return testAtt;
    }

    public static void saveItem(Session session, Item item) {
        if (item.getItemID()==0){
            item.setItemID((Integer) session.save(item));
        }
        else {
            session.update(item);
        }
    }
}
