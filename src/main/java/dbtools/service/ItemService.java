package dbtools.service;

import dbtools.dao.CarDAO;
import dbtools.dao.ItemDAO;
import dbtools.entities.CarAttribute;
import dbtools.entities.Item;
import dbtools.entities.ItemAttribute;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ItemService {
    public Item checkItem(Session session, Item item) {
        Item dbItem = ItemDAO.getExistingItem(session, item);
        Set<ItemAttribute> checkedAttributes = checkAttributes(session, item.getItemAttributes());
        if (dbItem==null){
            item.setItemAttributes(checkedAttributes);
        }
        else {
            dbItem.getItemAttributes().addAll(checkedAttributes);
            item = dbItem;
        }
        ItemDAO.saveItem(session, item);

        return item;
    }

    private Set<ItemAttribute> checkAttributes(Session session, Set<ItemAttribute> itemAttributes) {
        Set<ItemAttribute> finalAttributes = new HashSet<>();
        itemAttributes.forEach(attribute->{
            ItemAttribute testAttribute = ItemDAO.getExistingAttribute(attribute, session);
            finalAttributes.add(Objects.requireNonNullElse(testAttribute, attribute));
        });

        return finalAttributes;
    }
}
