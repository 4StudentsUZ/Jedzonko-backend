package com.students.recipesapi.service;

import com.students.recipesapi.entity.Product;
import com.students.recipesapi.entity.UserEntity;
import com.students.recipesapi.exception.InvalidInputException;
import com.students.recipesapi.exception.NotFoundException;
import com.students.recipesapi.model.ProductModel;
import com.students.recipesapi.repository.ProductRepository;
import com.students.recipesapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ProductService(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public Product findById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Product with id %d not found.", id)));
    }

    public Product create(String username, ProductModel productModel) {
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("User with username \"%s\" not found.", username)));

        Product product = new Product();
        product.setName(productModel.getName());
        product.setBarcode(productModel.getBarcode());
        product.setImage(productModel.getImage());
        product.setAuthor(userEntity);

        return productRepository.save(product);
    }

    public Product update(String username, ProductModel productModel) {
        if (!userRepository.existsByUsername(username)) {
            throw new NotFoundException(String.format("User with username \"%s\" not found.", username));
        }

        Product originalProduct = productRepository
                .findById(productModel.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Product with id %d not found", productModel.getId())));

        if (!username.equals(originalProduct.getAuthor().getUsername())) {
            throw new InvalidInputException("Tried to update a recipe user is not allowed to.");
        }

        if (productModel.getName() != null) originalProduct.setName(productModel.getName());
        if (productModel.getBarcode() != null) originalProduct.setBarcode(productModel.getBarcode());
        if (productModel.getImage() != null) originalProduct.setImage(productModel.getImage());

        return productRepository.save(originalProduct);
    }

    public void delete(String username, Long productId) {
        if (!userRepository.existsByUsername(username)) {
            throw new NotFoundException(String.format("User with username \"%s\" not found.", username));
        }

        Product originalProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format("Product with id %d not found", productId)));

        if (!username.equals(originalProduct.getAuthor().getUsername())) {
            throw new InvalidInputException("Tried to update a recipe user is not allowed to.");
        }

        productRepository.delete(originalProduct);
    }
}
