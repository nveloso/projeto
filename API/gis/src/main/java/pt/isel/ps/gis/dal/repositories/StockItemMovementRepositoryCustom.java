package pt.isel.ps.gis.dal.repositories;

import pt.isel.ps.gis.model.StockItemMovement;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface StockItemMovementRepositoryCustom {

    /**
     * Find all movements associated with specific house filtered by stock item id, type of the movement, date of the
     * movement and storage id
     *
     * @param houseId   The id of the house
     * @param sku       Search for specific stock item
     * @param type      Search for movement type
     * @param date      Search for movement date
     * @param storageId Search for specific storage
     * @return List with all movements filtered associated with param houseId
     */
    List<StockItemMovement> findMovementsFiltered(Long houseId, String sku, Boolean type, Timestamp date, Short storageId);

    /**
     * Insert stock item movement
     *
     * @param houseId                The id of the house
     * @param storageId              The id of the storage
     * @param movementType           The movement type
     * @param quantity               The stock item quantity of movement
     * @param productName            The name of the product
     * @param brand                  The brand of stock item
     * @param variety                The variety of stock item
     * @param segment                The segment of stock item
     * @param segmentUnit            The segment unit of stock item
     * @param conservationConditions Simple string with conservation conditions
     * @param description            Description of the stock item
     * @param date                   Expiration date of the stock item
     * @return the stock item movement inserted
     */
    StockItemMovement insertStockItemMovement(long houseId, short storageId, boolean movementType, short quantity,
                                              String productName, String brand, String variety, float segment,
                                              String segmentUnit, String conservationConditions, String description,
                                              Date date);
}
