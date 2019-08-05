package dbtools;

import dbtools.entities.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class CarBuilder {
    private static final Logger logger = LogManager.getLogger(CarBuilder.class.getName());
    public Car buildCarFromRow(Row row) {
        Car result = new Car();
        int excelCarID = getExcelCarID(row);
        int yearStart = getYearStart(row);
        int yearFinish = getYearFinish(row);
        String make = getMake(row);
        String model = getModel(row);
        String subModel = getSubModel(row);
        String drive = getDrive(row);
        if (yearStart>yearFinish){
            logger.error("Year start is greater that year finish at cars sheet at row " + row.getRowNum());
            System.exit(1);
        }
        result.setCarExcelID(excelCarID);
        result.setYearStart(yearStart);
        result.setYearFinish(yearFinish);
        result.setMake(make);
        result.setModel(model);
        result.setSubModel(subModel);
        result.setDrive(drive);
        logger.debug("Car built: " + result);

        return result;
    }

    private String getDrive(Row row) {
        return getStringCellValue("Drive", row, 6);
    }

    private String getSubModel(Row row) {
        return getStringCellValue("SubModel", row, 5);
    }

    private String getModel(Row row) {
        return getStringCellValue("Model", row, 4);
    }

    private String getMake(Row row) {
        return getStringCellValue("Make", row, 3);
    }

    private int getYearFinish(Row row) {
        return getIntCellValue("Year finish", row, 2);
    }

    private int getYearStart(Row row) {
        return getIntCellValue("Year start", row, 1);
    }

    private int getExcelCarID(Row row) {
        return getIntCellValue("CarExcelID", row, 0);
    }

    private int getIntCellValue(String columnName, Row row, int cellNum){
        Cell cell = row.getCell(cellNum);
        if (cell==null){
            logger.error("Blank cell at "+ columnName +" at Cars sheet at row " + row.getRowNum());
            System.exit(1);
        }
        double result = 0d;
        try {
            result = cell.getNumericCellValue();
        }
        catch (Exception e){
            logger.error("unexpected value type for " + columnName + " at Cars sheet at row " + row.getRowNum());
        }

        return (int) result;
    }
    private String getStringCellValue(String columnName, Row row, int cellNum){
        Cell cell = row.getCell(cellNum);
        if (cell==null){
            logger.error("Blank cell at "+ columnName +" at Cars sheet at row " + row.getRowNum());
            System.exit(1);
        }
        String result = "";
        try {
            result = cell.getStringCellValue();
        }
        catch (Exception e){
            try {
                result = (int)cell.getNumericCellValue()+"";
            }
            catch (Exception e1){
                logger.error("Unknown cell format for " + columnName + "at Cars sheet at row " + row.getRowNum());
                System.exit(1);
            }
        }

        return result;
    }
}
