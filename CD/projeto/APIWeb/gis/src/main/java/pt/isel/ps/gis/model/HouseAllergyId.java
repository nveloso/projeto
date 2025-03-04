package pt.isel.ps.gis.model;

import pt.isel.ps.gis.exceptions.EntityException;
import pt.isel.ps.gis.utils.RestrictionsUtils;
import pt.isel.ps.gis.utils.ValidationsUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HouseAllergyId implements Serializable {

    /**
     * COLUNAS
     */
    @Column(name = "house_id", nullable = false)
    private Long houseId;

    @Column(name = "allergy_allergen", length = RestrictionsUtils.ALLERGY_ALLERGEN_MAX_LENGTH, nullable = false)
    private String allergyAllergen;

    /**
     * CONSTRUTORES
     */
    protected HouseAllergyId() {
    }

    public HouseAllergyId(Long houseId, String allergy) throws EntityException {
        setHouseId(houseId);
        setAllergyAllergen(allergy);
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

    public String getAllergyAllergen() {
        return allergyAllergen;
    }

    public void setAllergyAllergen(String allergyAllergen) throws EntityException {
        ValidationsUtils.validateAllergyAllergen(allergyAllergen);
        this.allergyAllergen = allergyAllergen;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HouseAllergyId that = (HouseAllergyId) obj;
        return Objects.equals(houseId, that.houseId) &&
                Objects.equals(allergyAllergen, that.allergyAllergen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseId, allergyAllergen);
    }
}
