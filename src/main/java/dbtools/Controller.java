package dbtools;

import dbtools.entities.Car;
import java.util.Set;

public class Controller {

    public static void main(String[] args) {
        new Controller().saveCarsToInternalDB();
    }

    private void saveCarsToInternalDB(){
        Set<Car> cars = new ExcelReader().getAllCarsFromExcel();
        BackupHandler.backupDB();
        new DatabaseSaver().saveToInternalDB(cars);
        HibernateUtil.shutdown();
    }
}
