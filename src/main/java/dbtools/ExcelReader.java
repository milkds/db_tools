package dbtools;

import dbtools.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExcelReader {
    private static final Logger logger = LogManager.getLogger(ExcelReader.class.getName());
    private static final String EXCEL_PATH = "src\\main\\resources\\template.xls";

    /**
     * Gets cars from excel file
     * @return list of cars from excel file. If no items in file
     * item list in each car will have zero length
     */
    public  Set<Car> getAllCarsFromExcel() {
       Workbook workbook = getWorkBook();
       Set<Car>cars = getCars(workbook);
       List<CarAttribute> carAttributes = getCarAttributes(workbook);
       setCarAttributes(cars, carAttributes);
       List<Item> items = getItems(workbook);
       //checking if only cars present in workbook
       if (items.size()==0){
           return cars;
       }
       List<ItemAttribute> itemAttributes = getItemAttributes(workbook);
       setItemAttributes(itemAttributes, items);
       List<Fitment> fitments = getFitments(workbook); //will crash if no fitments found - that's expected behaviour
       List<FitmentAttribute> fitmentAttributes = getFitmentAttributes(workbook);
       setFitmentAttributes(fitments, fitmentAttributes);
       compileCars(cars, fitments, items);

       cars.forEach(logger::debug);
       cars.forEach(car->{
           car.getAttributes().forEach(logger::debug);
       });

        return cars;
    }

    private void compileCars(Set<Car> cars, List<Fitment> fitments, List<Item> items) {
        Map<Integer, Car> carMap = new HashMap<>(); //k = car excel ID, v = car
        cars.forEach(car-> carMap.put(car.getCarExcelID(), car));
        Map<Integer, Item> itemMap = new HashMap<>(); //k = item excel ID, v = car
        items.forEach(item -> itemMap.put(item.getItemExcelID(), item));
        fitments.forEach(fitment -> {
            Car car = carMap.get(fitment.getCarExcelID());
            if (car==null){
                logger.error("Found fitment to non-existing car: " + fitment);
                System.exit(1);
            }
            car.getFitments().add(fitment);
            fitment.setCar(car);

            Item item = itemMap.get(fitment.getItemExcelID());
            if (item==null){
                logger.error("Found fitment for non-existing item: " + fitment);
                System.exit(1);
            }
            item.getFitments().add(fitment);
            fitment.setItem(item);
        });
        logger.debug("Cars compiled");

        //searching for cars without fitments for logging purpose
        Map<Integer, Fitment> fitmentMap = new HashMap<>();
        fitments.forEach(fitment -> fitmentMap.put(fitment.getCarExcelID(), fitment));
        cars.forEach(car -> {
            Fitment fitment = fitmentMap.get(car.getCarExcelID());
            if (fitment==null){
                logger.info("Found car without fitment " + car);
            }
        });
    }

    private void setFitmentAttributes(List<Fitment> fitments, List<FitmentAttribute> fitmentAttributes) {
        if (fitmentAttributes.size()==0){
            return;
        }
        Map<Integer, Fitment> fitmentMap = new HashMap<>();
        fitments.forEach(fitment -> fitmentMap.put(fitment.getFitmentExcelID(), fitment));
        fitmentAttributes.forEach(attribute->{
            Fitment fitment = fitmentMap.get(attribute.getFitmentExcelID());
            if (fitment==null){
                logger.error("Found fitment attribute with non-existing fitment id: " + attribute);
                System.exit(1);
            }
            fitment.getFitmentAttributes().add(attribute);
        });
    }

    //will return zero size list, if no attributes found at fitment_att sheet
    private List<FitmentAttribute> getFitmentAttributes(Workbook workbook) {
        logger.debug("Getting fitment attributes");
        List<FitmentAttribute> result = new ArrayList<>();
        Sheet fitmentAttSheet = getSheetByName(workbook, "Fitment_att");
        if (fitmentAttSheet==null){
            logger.error("No Fitment_att sheet found");
            System.exit(1);
        }
        int totalRows = fitmentAttSheet.getPhysicalNumberOfRows();
        if (totalRows<2){
            logger.info("No fitment attributes in Fitment_att sheet");
            return result;
        }
        //at this moment we sure that we have at least one row in sheet except headers
        logger.debug("Getting raw attributes");
        List<UnderfinedAttribute> rawAttributes = getRawAttributes(fitmentAttSheet);
        logger.debug("got raw attributes, building item attributes");
        rawAttributes.forEach(rawAttribute->{
            FitmentAttribute fitmentAtt = new FitmentAttribute();
            fitmentAtt.setFitmentExcelID(rawAttribute.getId());
            fitmentAtt.setFitmentAttName(rawAttribute.getName());
            fitmentAtt.setFitmentAttValue(rawAttribute.getValue());
            result.add(fitmentAtt);
        });
        logger.debug("Got fitment attributes:");
        result.forEach(logger::debug);

        return result;
    }

    private List<Fitment> getFitments(Workbook workbook) {
        logger.debug("searching for fitment sheet");
        Sheet itemSheet = getSheetByName(workbook, "Fitment");
        if (itemSheet==null){
            logger.error("No Fitment sheet found");
            System.exit(1);
        }
        List<Fitment> result = new ArrayList<>();
        int totalRows = itemSheet.getPhysicalNumberOfRows();
        if (totalRows<2){
            logger.error("No fitment found in fitment sheet");
            System.exit(1);
        }
        //by here we sure that we have items to add.
        for (int i = 1; i < totalRows; i++) {
            Row currentRow = itemSheet.getRow(i);
            if (currentRow==null){
                logger.error("Empty row at Fitment sheet at " + i);
                System.exit(1);
            }
            result.add(new FitmentBuilder().buildFitment(currentRow));
        }
        logger.debug("got fitments:");
        result.forEach(logger::debug);

        return result;
    }

    private void setItemAttributes(List<ItemAttribute> itemAttributes, List<Item> items) {
        if (itemAttributes.size()==0){
            return;
        }
        Map<Integer, Item> itemMap = new HashMap<>();
        items.forEach(item->itemMap.put(item.getItemExcelID(), item));
        itemAttributes.forEach(attribute->{
            Item item = itemMap.get(attribute.getItemExcelID());
            if (item==null){
                logger.error("Found item attribute for non-existing item id: " + attribute);
                System.exit(1);
            }
            item.getItemAttributes().add(attribute);
        });
    }

    //will return zero size list if no attributes found at items_att sheet
    private List<ItemAttribute> getItemAttributes(Workbook workbook) {
        logger.debug("Getting item attributes");
        List<ItemAttribute> result = new ArrayList<>();
        Sheet itemAttSheet = getSheetByName(workbook, "Items_att");
        if (itemAttSheet==null){
            logger.error("No Items_att sheet found");
            System.exit(1);
        }
        int totalRows = itemAttSheet.getPhysicalNumberOfRows();
        if (totalRows<2){
            logger.info("No item attributes in Items_att sheet");
            return result;
        }
        //at this moment we sure that we have at least one row in sheet except headers
        logger.debug("Getting raw attributes");
        List<UnderfinedAttribute> rawAttributes = getRawAttributes(itemAttSheet);
        logger.debug("got raw attributes, building item attributes");
        rawAttributes.forEach(rawAttribute->{
            ItemAttribute itemAtt = new ItemAttribute();
            itemAtt.setItemExcelID(rawAttribute.getId());
            itemAtt.setItemAttName(rawAttribute.getName());
            itemAtt.setItemAttValue(rawAttribute.getValue());
            result.add(itemAtt);
        });
        logger.debug("Got item attributes:");


        return result;
    }

    private void setCarAttributes(Set<Car> cars, List<CarAttribute> carAttributes) {
        //checking if any attributes present
        if (carAttributes.size()==0){
            return;
        }
        Map<Integer, Car> carMap = new HashMap<>();//k - car id, v - car
        cars.forEach(car -> carMap.put(car.getCarExcelID(), car));
        carAttributes.forEach(attribute->{
            int carID = attribute.getCarExcelID();
            Car car = carMap.get(carID);
            if  (car==null){
                logger.error("Found car attribute with non-existing car ID: " + attribute);
                System.exit(1);
            }
            car.getAttributes().add(attribute);
            attribute.getCars().add(car);
        });
    }

    /**
     * Gets items from items sheet
     * @param workbook - workbook with possibly present items sheet
     * @return List of items from item sheet. Return list with 0 size, if no items present
     * in workbook.
     */
    private List<Item> getItems(Workbook workbook) {
        logger.debug("searching for items sheet");
        Sheet itemSheet = getSheetByName(workbook, "Items");
        if (itemSheet==null){
            logger.error("No Items sheet found");
            System.exit(1);
        }
        List<Item> result = new ArrayList<>();
        int totalRows = itemSheet.getPhysicalNumberOfRows();
        if (totalRows<2){
            logger.info("No items to add in Items sheet");
            return result;
        }
        //by here we sure that we have items to add.
        for (int i = 1; i < totalRows; i++) {
            Row currentRow = itemSheet.getRow(i);
            if (currentRow==null){
                logger.error("Empty row at Items sheet at " + i);
                System.exit(1);
            }
            result.add(new ItemBuilder().buildItem(currentRow));
        }

        return result;
    }

    private Set<Car> getCars(Workbook workbook) {
        logger.debug("getting cars from cars sheet");
        Set<Car> result  = new HashSet<>();
        logger.debug("Searching for /Cars/ sheet");
        Sheet carsSheet = getSheetByName(workbook, "Cars");
        if (carsSheet==null){
            logger.error("No Cars sheet found");
            System.exit(1);
        }
        int totalRows = carsSheet.getPhysicalNumberOfRows();
        if (totalRows<2){
            logger.error("No cars to add in Cars sheet");
            System.exit(1);
        }
        for (int i = 1; i < totalRows; i++) {
            Row currentRow = carsSheet.getRow(i);
            if (currentRow==null){
                logger.error("Empty row at Cars sheet at " + i);
                System.exit(1);
            }
            result.add(new CarBuilder().buildCarFromRow(currentRow));
        }

        Set<Car> carSet = new HashSet<>();
        result.forEach(car -> {
            if (carSet.contains(car)){
                logger.error("Duplicate Car found: " + car);
                System.exit(1);
            }
            else {
                carSet.add(car);
            }
        });

        return result;
    }

    /**
     * Gets car attributes from car_att sheet, if finds such
     * @param workbook - workbook which presumably contains car_att sheet
     * @return list of car attributes.
     * List will have 0 size, if car_att sheet is present, but
     * contains no info, or only headers.
     */
    private List<CarAttribute> getCarAttributes(Workbook workbook) {
        logger.debug("Getting car attributes");
        List<CarAttribute> result = new ArrayList<>();
        Sheet carAttSheet = getSheetByName(workbook, "Car_att");
        if (carAttSheet==null){
            logger.error("No Car_att sheet found");
            System.exit(1);
        }
        int totalRows = carAttSheet.getPhysicalNumberOfRows();
        if (totalRows<2){
            logger.info("No car attributes in car_att sheet");
            return result;
        }
        //at this moment we sure that we have at least one row in sheet except headers
        logger.debug("Getting raw attributes");
        List<UnderfinedAttribute> rawAttributes = getRawAttributes(carAttSheet);
        logger.debug("got raw attributes, building car attributes");
        rawAttributes.forEach(rawAttribute->{
            CarAttribute carAtt = new CarAttribute();
            carAtt.setCarExcelID(rawAttribute.getId());
            carAtt.setCarAttName(rawAttribute.getName());
            carAtt.setCarAttValue(rawAttribute.getValue());
            result.add(carAtt);
        });
        logger.debug("Got car attributes:");
        result.forEach(logger::debug);

        return result;
    }

    private List<UnderfinedAttribute> getRawAttributes(Sheet sheet) {
        Map<Integer, String> headerMap = getHeaderMap(sheet.getRow(0), sheet.getSheetName());
        int totalCells = headerMap.size()+1;//needed for checking if row has values without headers
        List<UnderfinedAttribute> result = new ArrayList<>();
        //iterating rows from sheet
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row currentRow = sheet.getRow(i);
            boolean valuePresent = false;
            if (currentRow.getPhysicalNumberOfCells()>totalCells){
                logger.error("Value without header detected at row " + i + " at sheet " + sheet.getSheetName());
                System.exit(1);
            }
            //getting id
            Cell idCell = currentRow.getCell(0);
            if (idCell==null){
                logger.error("Blank id cell at row " + i + " at sheet " + sheet.getSheetName());
                System.exit(1);
            }
            int id = 0;
            try {
                id=getIntCellValue(idCell);
            }
            catch (Exception e){
                logger.error("Unexpected ID format at row " + i + " at sheet " + sheet.getSheetName());
            }
            //iterating cells from row
            for (int j = 1; j < totalCells; j++) {
                Cell curCell = currentRow.getCell(j);
                if (curCell!=null){
                    valuePresent=true;
                    String cellValue = "";
                    try {
                        cellValue = getStringCellValue(curCell);
                    }
                    catch (Exception e){
                        logger.error("Unexpected value format for cell "+ j + " at row " + i + " at sheet " + sheet.getSheetName());
                        System.exit(1);
                    }
                    UnderfinedAttribute attribute = new UnderfinedAttribute();
                    attribute.setId(id);
                    attribute.setName(headerMap.get(j));
                    attribute.setValue(cellValue);
                    result.add(attribute);
                }
            }
            //checking if row contains attribute values
            if (!valuePresent){
                logger.error("Row without attribute values at " + i + " at sheet " + sheet.getSheetName());
                System.exit(1);
            }
        }

        return result;
    }

    private static String getStringCellValue(Cell cell) {
        String result = "";
        try {
            result = cell.getStringCellValue();
        }
        catch (Exception e){
            result = (int)cell.getNumericCellValue()+"";
        }

        return result;
    }

    private static int getIntCellValue(Cell idCell) {
        double result = idCell.getNumericCellValue();
        return (int) result;
    }

    private static Map<Integer, String> getHeaderMap(Row row, String sheetName) {
        logger.debug("Getting header map for sheet " + sheetName);
        Map<Integer, String> result = new HashMap<>();
        int totalCells = row.getPhysicalNumberOfCells();
        if (totalCells<2){
            logger.error("No attribute headers at " + sheetName + " sheet.");
            System.exit(1);
        }
        logger.debug("Found headers, reading...");
        for (int i = 1; i < totalCells ; i++) {
            Cell currentCell = row.getCell(i);
            if (currentCell==null){
                logger.error("Blank header at cell " + i + ", at sheet " + sheetName);
                System.exit(1);
            }
            String cellValue = "";
            try {
                cellValue = currentCell.getStringCellValue();
            }
            catch (Exception e){
                try {
                    cellValue = (int)currentCell.getNumericCellValue()+"";
                }
                catch (Exception e1){
                    logger.error("Unknown cell format for header " + i + "at sheet "+sheetName);
                    System.exit(1);
                }
            }
            result.put(i, cellValue);
        }
        logger.debug("headers reading finished.");

        return result;
    }

    private static Sheet getSheetByName(Workbook workbook, String sheetName) {
        Sheet result = null;
        for (Sheet sheet : workbook) {
            if (sheet.getSheetName().equals(sheetName)) {
                result = sheet;
                logger.debug("Found sheet " + sheetName);
                break;
            }
        }

        return result;
    }

    /**
     * Opens file and assures that it has 6 sheets.
     * @return workbook with exactly 6 sheets
     */
    private static Workbook getWorkBook() {
        Workbook workbook = null;
        logger.debug("opening file...");
        try {
            workbook = WorkbookFactory.create(new File(EXCEL_PATH));
        } catch (IOException|InvalidFormatException e) {
           logger.error("Couldn't open excel file");
           System.exit(1);
        }
        logger.debug("file opened");
        int totalSheets = workbook.getNumberOfSheets();
        if (totalSheets!=6){
            logger.error("File contains " +totalSheets + " sheets. Must be 6");
            System.exit(1);
        }
        logger.info("excel file successfully opened. It contains "+ totalSheets +" sheets.");

        return workbook;
    }

    private class UnderfinedAttribute{
        private Integer id;
        private String name;
        private String value;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "UnderfinedAttribute{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

}
