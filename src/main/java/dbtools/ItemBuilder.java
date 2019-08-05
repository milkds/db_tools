package dbtools;

import dbtools.entities.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ItemBuilder {
    private static final Logger logger = LogManager.getLogger(ItemBuilder.class.getName());

    public Item buildItem(Row currentRow) {
        Item item = new Item();
        int itemExcelID = getExcelID(currentRow);
        String partNo = getPartNo(currentRow);
        String manufacturer = getManufacturer(currentRow);
        String type = getType(currentRow);

        item.setItemExcelID(itemExcelID);
        item.setItemPartNo(partNo);
        item.setItemManufacturer(manufacturer);
        item.setItemType(type);

        logger.debug("item built: " + item);

        return item;
    }

    private String getType(Row currentRow) {
        return getStringCellValue("item_type", currentRow, 3);
    }

    private String getManufacturer(Row currentRow) {
        return getStringCellValue("manufacturer", currentRow, 2);
    }

    private String getPartNo(Row currentRow) {
        return getStringCellValue("part_number", currentRow, 1);
    }

    private int getExcelID(Row currentRow) {
        return getIntCellValue("item_id", currentRow, 0);
    }

    private int getIntCellValue(String columnName, Row row, int cellNum){
        Cell cell = row.getCell(cellNum);
        if (cell==null){
            logger.error("Blank cell at "+ columnName +" at Items sheet at row " + row.getRowNum());
            System.exit(1);
        }
        double result = 0d;
        try {
            result = cell.getNumericCellValue();
        }
        catch (Exception e){
            logger.error("unexpected value type for " + columnName + " at Items sheet at row " + row.getRowNum());
            System.exit(1);
        }

        return (int) result;
    }

    private String getStringCellValue(String columnName, Row row, int cellNum){
        Cell cell = row.getCell(cellNum);
        if (cell==null){
            logger.error("Blank cell at "+ columnName +" at Items sheet at row " + row.getRowNum());
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
                logger.error("Unknown cell format for " + columnName + "at Items sheet at row " + row.getRowNum());
                System.exit(1);
            }
        }

        return result;
    }
}
