package pt.isel.ps.gis.model;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import pt.isel.ps.gis.exceptions.EntityException;
import pt.isel.ps.gis.model.types.CharacteristicsJsonUserType;
import pt.isel.ps.gis.utils.RestrictionsUtils;
import pt.isel.ps.gis.utils.ValidationsUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "house")
@TypeDef(name = "CharacteristicsJsonUserType", typeClass = CharacteristicsJsonUserType.class)
public class House {

    /**
     * COLUNAS
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id", nullable = false)
    private Long houseId;

    @Basic
    @Column(name = "house_name", length = RestrictionsUtils.HOUSE_NAME_MAX_LENGTH, nullable = false)
    private String houseName;

    @Basic
    @Column(name = "house_characteristics", nullable = false, columnDefinition = "json")
    @Type(type = "CharacteristicsJsonUserType")
    private Characteristics houseCharacteristics;

    /**
     * COLEÇÕES
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "houseByHouseId")
    private Collection<HouseAllergy> houseallergiesByHouseId = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "houseByHouseId")
    private Collection<List> listsByHouseId = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "houseByHouseId")
    private Collection<StockItem> stockitemsByHouseId = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "houseByHouseId")
    private Collection<Storage> storagesByHouseId = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "houseByHouseId")
    private Collection<UserHouse> userhousesByHouseId = new ArrayList<>();

    /**
     * CONSTRUTORES
     */
    protected House() {
    }

    public House(String houseName, Characteristics houseCharacteristics) throws EntityException {
        setHouseName(houseName);
        setHouseCharacteristics(houseCharacteristics);
    }

    public House(Long houseId, String houseName, Characteristics houseCharacteristics) throws EntityException {
        this(houseName, houseCharacteristics);
        setHouseId(houseId);
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

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) throws EntityException {
        ValidationsUtils.validateHouseName(houseName);
        this.houseName = houseName;
    }

    public Characteristics getHouseCharacteristics() {
        return houseCharacteristics;
    }

    public void setHouseCharacteristics(Characteristics houseCharacteristics) {
        this.houseCharacteristics = houseCharacteristics;
    }

    public Collection<HouseAllergy> getHouseallergiesByHouseId() {
        return houseallergiesByHouseId;
    }

    public void setHouseallergiesByHouseId(Collection<HouseAllergy> houseallergiesByHouseId) {
        this.houseallergiesByHouseId = houseallergiesByHouseId;
    }

    public Collection<List> getListsByHouseId() {
        return listsByHouseId;
    }

    public void setListsByHouseId(Collection<List> listsByHouseId) {
        this.listsByHouseId = listsByHouseId;
    }

    public Collection<StockItem> getStockitemsByHouseId() {
        return stockitemsByHouseId;
    }

    public void setStockitemsByHouseId(Collection<StockItem> stockitemsByHouseId) {
        this.stockitemsByHouseId = stockitemsByHouseId;
    }

    public Collection<Storage> getStoragesByHouseId() {
        return storagesByHouseId;
    }

    public void setStoragesByHouseId(Collection<Storage> storagesByHouseId) {
        this.storagesByHouseId = storagesByHouseId;
    }

    public Collection<UserHouse> getUserhousesByHouseId() {
        return userhousesByHouseId;
    }

    public void setUserhousesByHouseId(Collection<UserHouse> userhousesByHouseId) {
        this.userhousesByHouseId = userhousesByHouseId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        House that = (House) obj;
        return Objects.equals(houseId, that.houseId) &&
                Objects.equals(houseName, that.houseName) &&
                Objects.equals(houseCharacteristics, that.houseCharacteristics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseId, houseName, houseCharacteristics);
    }
}
