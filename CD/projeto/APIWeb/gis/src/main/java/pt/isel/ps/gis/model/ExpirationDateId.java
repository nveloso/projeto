package pt.isel.ps.gis.model;

import pt.isel.ps.gis.exceptions.EntityException;
import pt.isel.ps.gis.utils.DateUtils;
import pt.isel.ps.gis.utils.RestrictionsUtils;
import pt.isel.ps.gis.utils.ValidationsUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Embeddable
public class ExpirationDateId implements Serializable {

    /**
     * COLUNAS
     */
    @Column(name = "house_id", nullable = false)
    private Long houseId;

    @Column(name = "stockitem_sku", length = RestrictionsUtils.STOCKITEM_SKU_MAX_LENGTH, nullable = false)
    private String stockitemSku;

    @Column(name = "date_date", nullable = false)
    private Date dateDate;

    /**
     * CONSTRUTOR
     */
    protected ExpirationDateId() {
    }

    public ExpirationDateId(Long houseId, String stockitemSku, String expirationDate) throws EntityException {
        setHouseId(houseId);
        setStockitemSku(stockitemSku);
        setDateDate(expirationDate);
    }

    /**
     * GETTERS E SETTERS
     */
    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) throws EntityException {
        ValidationsUtils.validateHouseId(houseId);
        this.houseId = houseId;
    }

    public String getStockitemSku() {
        return stockitemSku;
    }

    public void setStockitemSku(String stockitemSku) throws EntityException {
        ValidationsUtils.validateStockItemSku(stockitemSku);
        this.stockitemSku = stockitemSku;
    }

    public String getDateString() {
        return DateUtils.convertDateFormat(dateDate);
    }

    public void setDateDate(String date) throws EntityException {
        ValidationsUtils.validateDate(date);
        this.dateDate = Date.valueOf(date);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ExpirationDateId that = (ExpirationDateId) obj;
        return Objects.equals(houseId, that.houseId) &&
                Objects.equals(stockitemSku, that.stockitemSku) &&
                Objects.equals(dateDate, that.dateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseId, stockitemSku, dateDate);
    }
}
