package com.students.recipesapi.controller;


import com.students.recipesapi.entity.Product;
import com.students.recipesapi.model.ProductModel;
import com.students.recipesapi.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/get/all")
    @ResponseBody
    ResponseEntity<List<ProductModel>> all() {
        List<Product> products = productService.findAll();
        List<ProductModel> productModels = products
                .stream()
                .map(ProductModel::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productModels);
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    ResponseEntity<ProductModel> one(@PathVariable Long id) {
        Product product = productService.findById(id);
        ProductModel productModel = new ProductModel(product);
        return ResponseEntity.ok(productModel);
    }

    @PutMapping(value = "/create/}", consumes = "application/json", produces = "application/json")
    ResponseEntity<ProductModel> create(@RequestBody ProductModel product, Principal principal) {
        Product returnedProduct = productService.create(principal.getName(), product);
        ProductModel returnedProductModel = new ProductModel(returnedProduct);
        return ResponseEntity.ok(returnedProductModel);
    }

    @PutMapping(value = "/update/{productId}", consumes = "application/json", produces = "application/json")
    ResponseEntity<ProductModel> update(@PathVariable Long productId, @RequestBody ProductModel product, Principal principal) {
        product.setId(productId);
        Product returnedProduct = productService.update(principal.getName(), product);
        ProductModel returnedProductModel = new ProductModel(returnedProduct);
        return ResponseEntity.ok(returnedProductModel);
    }

    @DeleteMapping(value = "/delete/{productId}", consumes = "application/json", produces = "application/json")
    ResponseEntity<String> delete(@PathVariable Long productId, Principal principal) {
        productService.delete(principal.getName(), productId);
        return ResponseEntity.ok("Successfully deleted the product.");
    }
}
