package dbtools;

import dbtools.entities.Fitment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class FitmentBuilder {

    private static final Logger logger = LogManager.getLogger(FitmentBuilder.class.getName());
    public Fitment buildFitment(Row currentRow) {
        Fitment fitment = new Fitment();

        int fitmentExcelID = getIntCellValue("FITMENT_ID", currentRow, 0);
        int carExcelID = getIntCellValue("CAR_ID", currentRow, 1);
        int itemExcelID = getIntCellValue("ITEM_ID", currentRow, 2);

        fitment.setFitmentExcelID(fitmentExcelID);
        fitment.setCarExcelID(carExcelID);
        fitment.setItemExcelID(itemExcelID);


        return fitment;
    }


    private int getIntCellValue(String columnName, Row row, int cellNum){
        Cell cell = row.getCell(cellNum);
        if (cell==null){
            logger.error("Blank cell at "+ columnName +" at Fitment sheet at row " + row.getRowNum());
            System.exit(1);
        }
        double result = 0d;
        try {
            result = cell.getNumericCellValue();
        }
        catch (Exception e){
            logger.error("unexpected value type for " + columnName + " at Fitment sheet at row " + row.getRowNum());
            System.exit(1);
        }

        return (int) result;
    }
}
