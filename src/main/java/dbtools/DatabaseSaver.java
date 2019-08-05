package dbtools;

import dbtools.entities.Car;
import dbtools.service.CarService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class DatabaseSaver {
    private static final Logger logger = LogManager.getLogger(DatabaseSaver.class.getName());
    public void saveToInternalDB(Set<Car> cars) {
        logger.debug("Saving to db: ");
        saveCars(cars);
    }

    private void saveCars(Set<Car> cars) {
        new CarService().saveCars(cars);
    }
}
