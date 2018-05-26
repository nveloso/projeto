package pt.isel.ps.gis.model.outputModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import pt.isel.ps.gis.hypermedia.siren.components.subentities.Action;
import pt.isel.ps.gis.hypermedia.siren.components.subentities.Entity;
import pt.isel.ps.gis.hypermedia.siren.components.subentities.Link;
import pt.isel.ps.gis.model.ExpirationDate;
import pt.isel.ps.gis.model.StockItem;
import pt.isel.ps.gis.utils.UriBuilderUtils;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"class", "properties", "entities", "links"})
public class StockItemOutputModel {

    private final static String ENTITY_CLASS = "stock-item";

    @JsonProperty(value = "class")
    private final String[] klass;
    @JsonProperty
    private final Map<String, Object> properties;
    @JsonProperty
    private final Entity[] entities;
    @JsonProperty
    private final Link[] links;

    // Ctor
    public StockItemOutputModel(StockItem stockItem) {
        this.klass = initKlass();
        this.properties = initProperties(stockItem);
        this.entities = initEntities(stockItem);
        this.links = initLinks(stockItem);
    }

    // Initters
    private String[] initKlass() {
        return new String[]{ENTITY_CLASS};
    }

    private HashMap<String, Object> initProperties(StockItem stockItem) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("house-id", stockItem.getId().getHouseId());
        properties.put("stock-item-sku", stockItem.getId().getStockitemSku());
        properties.put("category-id", stockItem.getCategoryId());
        properties.put("product-id", stockItem.getProductId());
        properties.put("stock-item-brand", stockItem.getStockitemBrand());
        properties.put("stock-item-conservation-storage", stockItem.getStockitemConservationstorage());
        properties.put("stock-item-description", stockItem.getStockitemDescription());
        properties.put("stock-item-quantity", stockItem.getStockitemQuantity());
        properties.put("stock-item-segment", stockItem.getStockitemSegment() + stockItem.getStockitemSegmentunit());
        properties.put("stock-item-variety", stockItem.getStockitemVariety());
        return properties;
    }

    private Entity[] initEntities(StockItem stockItem) {
        Entity[] entities = new Entity[stockItem.getExpirationdates().size()];
        int i = 0;
        for (ExpirationDate expirationDate : stockItem.getExpirationdates()) {
            //Properties
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("expiration-date", expirationDate.getId().getDateString());
            properties.put("quantity", expirationDate.getDateQuantity());

            entities[i++] = new Entity(
                    new String[]{"expiration-date"},
                    new String[]{"item"},
                    properties,
                    null,
                    null);
        }

        return entities;
    }

    private Link[] initLinks(StockItem stockItem) {
        long houseId = stockItem.getId().getHouseId();
        String sku = stockItem.getId().getStockitemSku();

        // Link-self
        String stockItemUri = UriBuilderUtils.buildStockItemUri(houseId, sku);
        Link self = new Link(new String[]{"self"}, new String[]{ENTITY_CLASS}, stockItemUri);

        //Link-related-stockItems
        String stockItemsUri = UriBuilderUtils.buildStockItemsUri(houseId);
        Link stockItemsLink = new Link(new String[]{"related"}, new String[]{"stock-items", "collection"}, stockItemsUri);

        // Link-related-allergens
        String itemAllergiesUri = UriBuilderUtils.buildAllergiesStockItemUri(houseId, sku);
        Link itemAllergiesLink = new Link(new String[]{"self"}, new String[]{"stock-item-allergens", "collection"}, itemAllergiesUri);

        return new Link[]{self, stockItemsLink, itemAllergiesLink};
    }
}
