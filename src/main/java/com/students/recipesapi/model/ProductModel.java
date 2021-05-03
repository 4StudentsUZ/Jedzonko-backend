package com.students.recipesapi.model;

import com.students.recipesapi.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.postgresql.util.Base64;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    public Long id;
    public String name;
    public String barcode;
    public String image;

    public ProductModel(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.barcode = product.getBarcode();
        if (product.getImage() != null) {
            this.image = Base64.encodeBytes(product.getImage());
        } else {
            this.image = "";
        }
    }
}
