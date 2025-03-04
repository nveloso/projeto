package pt.isel.ps.gis.model;

import pt.isel.ps.gis.exceptions.EntityException;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "stockitemallergy")
public class StockItemAllergy {

    /**
     * COLUNAS
     */
    @EmbeddedId
    private StockItemAllergyId id;

    /**
     * ASSOCIAÇÕES
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "house_id", referencedColumnName = "house_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "stockitem_sku", referencedColumnName = "stockitem_sku", nullable = false, insertable = false, updatable = false)
    })
    private StockItem stockitem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_allergen", referencedColumnName = "allergy_allergen", nullable = false, insertable = false, updatable = false)
    private Allergy allergyByAllergyAllergen;

    /**
     * CONSTRUTORES
     */
    protected StockItemAllergy() {
    }

    public StockItemAllergy(StockItemAllergyId id) {
        this.id = id;
    }

    public StockItemAllergy(Long houseId, String stockitemSku, String allergen) throws EntityException {
        setId(houseId, stockitemSku, allergen);
    }

    /**
     * GETTERS E SETTERS
     */
    public StockItemAllergyId getId() {
        return id;
    }

    public void setId(StockItemAllergyId id) {
        this.id = id;
    }

    public void setId(Long houseId, String stockitemSku, String allergen) throws EntityException {
        setId(new StockItemAllergyId(houseId, stockitemSku, allergen));
    }

    public StockItem getStockitem() {
        return stockitem;
    }

    public void setStockitem(StockItem stockitem) {
        this.stockitem = stockitem;
    }

    public Allergy getAllergyByAllergyAllergen() {
        return allergyByAllergyAllergen;
    }

    public void setAllergyByAllergyAllergen(Allergy allergyByAllergyAllergen) {
        this.allergyByAllergyAllergen = allergyByAllergyAllergen;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StockItemAllergy that = (StockItemAllergy) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
