package com.students.recipesapi.controller;


import com.students.recipesapi.entity.Product;
import com.students.recipesapi.model.ProductModel;
import com.students.recipesapi.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/get/all")
    @ResponseBody
    ResponseEntity<List<Product>> all() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    ResponseEntity<Product> one(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping(value = "/create/}", consumes = "application/json", produces = "application/json")
    ResponseEntity<Product> create(@RequestBody ProductModel product, Principal principal) {
        return ResponseEntity.ok(productService.create(principal.getName(), product));
    }

    @PutMapping(value = "/update/{productId}", consumes = "application/json", produces = "application/json")
    ResponseEntity<Product> update(@PathVariable Long productId, @RequestBody ProductModel product, Principal principal) {
        product.setId(productId);
        return ResponseEntity.ok(productService.update(principal.getName(), product));
    }

    @DeleteMapping(value = "/delete/{productId}", consumes = "application/json", produces = "application/json")
    ResponseEntity<String> delete(@PathVariable Long productId, Principal principal) {
        productService.delete(principal.getName(), productId);
        return ResponseEntity.ok("Successfully deleted the product.");
    }
}
