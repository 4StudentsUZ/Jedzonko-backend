package com.students.recipesapi;

import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.ProductModel;
import com.students.recipesapi.model.RegisterModel;
import com.students.recipesapi.service.ProductService;
import com.students.recipesapi.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.postgresql.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductTests {
    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    private final String registeredUsername = "test@gmail.com";
    private UserEntity registeredUser;

    private static final String exampleBase64Image = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAADPSURBVEhL7Y1BEsUgCEN7/0v7GaE2YpDWqZs/fRsxCeQom/kKUqzgqOg8MrcU+w9cRhRK9qulGUFFpCvgifi6gC5ft7cyJkShaw10aTgvsCnABV4uENcFiGLvCdpj2kFdJyYFNgVEBaiTArXbMCEKoE4SaqfXhfUCxf4xUQb1MBEtI1EG9b8saHba8UIBRkeo67Z8wttLBTZVSIFNJ5OOxwXijTtUVFDXmGJSxRfY1DPREVN77hZQK9KRy55HqZteF+4WLLPlKPIVpGwuKOUHRXOc8NtT3yEAAAAASUVORK5CYII=";

    @BeforeAll
    void setup() {
        registeredUser = userService.register(new RegisterModel(registeredUsername, "12345678"));
    }

    @Test
    void create_CorrectProductModel_AddProductToDatabase() {
        // Given
        ProductModel productModel = new ProductModel(0L, "Pomidor", "12345678", "imageima");

        // When
        Product createdProduct = productService.create(registeredUsername, productModel);

        // Then
        Product returnedProduct = productService.findById(createdProduct.getId());
        assertEquals(returnedProduct.getAuthor().getUsername(), registeredUsername);
        assertEquals(returnedProduct.getName(), productModel.getName());
        assertEquals(returnedProduct.getBarcode(), productModel.getBarcode());
        assertEquals(Base64.encodeBytes(returnedProduct.getImage()), productModel.getImage());
    }

    @Test
    void create_CorrectProductModel_ReturnedImageShouldBeTheSame() {
        // Given
        ProductModel productModel = new ProductModel(0L, "Pomidor", "12345678", exampleBase64Image);

        // When
        Product createdProduct = productService.create(registeredUsername, productModel);

        // Then
        Product returnedProduct = productService.findById(createdProduct.getId());
        assertEquals(returnedProduct.getImageBase64(), productModel.getImage());
    }

    @Test
    void update_CorrectProductModel_UpdateGivenProduct() {
        // Given
        ProductModel productModel = new ProductModel(0L, "Pomidor", "12345678", exampleBase64Image);
        Product createdProduct = productService.create(registeredUsername, productModel);
        String newProductName = "NewName";
        String newProductBarcode = "NewBarcode";
        String newProductImage = "NewImage";
        ProductModel updateModel = new ProductModel(createdProduct.getId(), newProductName, newProductBarcode, newProductImage);

        // When
        productService.update(registeredUsername, updateModel);

        // Then
        Product returnedProduct = productService.findById(createdProduct.getId());
        assertEquals(returnedProduct.getAuthor().getUsername(), registeredUsername);
        assertEquals(returnedProduct.getName(), updateModel.getName());
        assertEquals(returnedProduct.getBarcode(), updateModel.getBarcode());
        assertEquals(Base64.encodeBytes(returnedProduct.getImage()), updateModel.getImage());
    }

    @Test
    void update_NonExistingProduct_ThrowNotFoundException() {
        String newProductName = "NewName";
        String newProductBarcode = "NewBarcode";
        String newProductImage = "NewImage";
        ProductModel updateModel = new ProductModel(Long.MAX_VALUE, newProductName, newProductBarcode, newProductImage);

        assertThrows (
                NotFoundException.class,
                () -> productService.update(registeredUsername, updateModel)
        );
    }

    @Test
    void delete_ExistingProduct_DeleteGivenProduct() {
        // Given
        ProductModel productModel = new ProductModel(0L, "Pomidor", "12345678", "imageima");
        Product createdProduct = productService.create(registeredUsername, productModel);

        // When
        productService.delete(registeredUsername, createdProduct.getId());

        // Then
        assertThrows (
                NotFoundException.class,
                () -> productService.findById(createdProduct.getId())
        );
    }

    @Test
    void delete_NonExistingProduct_ThrowNotFoundException() {
        Long productId = Long.MAX_VALUE;

        assertThrows (
                NotFoundException.class,
                () -> productService.delete(registeredUsername, productId)
        );
    }
}
