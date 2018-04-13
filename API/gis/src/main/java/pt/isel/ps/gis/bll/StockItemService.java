package pt.isel.ps.gis.bll;

import pt.isel.ps.gis.model.StockItem;
import pt.isel.ps.gis.model.StockItemId;

import java.util.List;

public interface StockItemService {

    /**
     * Obter um item em stock através do seu ID
     *
     * @param stockItemId identificador do item em stock
     * @return StockItem
     */
    StockItem getStockItemByStockItemId(StockItemId stockItemId);

    /**
     * Listar todos os itens em stock duma casa através do ID da casa
     *
     * @param houseId identificador da casa
     * @return List<StockItem>
     */
    List<StockItem> getStockItemsByHouseId(Long houseId);

    /**
     * Listar todos os itens em stock filtrados duma casa através do ID da casa
     *
     * @param houseId identificador da casa
     * @param filters filtros para aplicar na filtragem dos resultados
     * @return List<StockItem>
     */
    List<StockItem> getStockItemsByHouseIdFiltered(Long houseId, StockItemFilters filters);

    /**
     * Adicionar um item ao stock duma casa
     *
     * @param stockItem item a adicionar ao stock
     * @return StockItem
     */
    StockItem addStockItem(StockItem stockItem);

    /**
     * Filtros - filtragem dos itens em stock
     */
    class StockItemFilters {
        public final String product;
        public final String brand;
        public final String variety;
        public final Float segment;
        public final Short storage;

        public StockItemFilters(String product, String brand, String variety, Float segment, Short storage) {
            this.product = product;
            this.brand = brand;
            this.variety = variety;
            this.segment = segment;
            this.storage = storage;
        }
    }
}
