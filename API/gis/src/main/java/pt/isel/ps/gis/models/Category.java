package pt.isel.ps.gis.models;

import pt.isel.ps.gis.exceptions.ModelException;
import pt.isel.ps.gis.utils.ValidationsUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "category")
public class Category {

    /**
     * COLUNAS
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Basic
    @Column(name = "category_name", length = 35, nullable = false, unique = true)
    private String categoryName;

    /**
     * ASSOCIAÇÕES
     */
    @OneToMany(mappedBy = "categoryByCategoryId")
    private Collection<Product> productsByCategoryId;

    /**
     * CONSTRUTORES
     */
    protected Category() {
    }

    public Category(Integer categoryId) throws ModelException {
        setCategoryId(categoryId);
    }

    public Category(Integer categoryId, String categoryName) throws ModelException {
        setCategoryId(categoryId);
        setCategoryName(categoryName);
    }

    /**
     * GETTERS E SETTERS
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) throws ModelException {
        ValidationsUtils.validateCategoryId(categoryId);
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) throws ModelException {
        ValidationsUtils.validateCategoryName(categoryName);
        this.categoryName = categoryName;
    }

    public Collection<Product> getProductsByCategoryId() {
        return productsByCategoryId;
    }

    public void setProductsByCategoryId(Collection<Product> productsByCategoryId) {
        this.productsByCategoryId = productsByCategoryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category that = (Category) obj;
        return Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryName);
    }
}
