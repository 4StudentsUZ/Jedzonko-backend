package com.students.recipesapi;

import com.students.recipesapi.entity.*;
import com.students.recipesapi.repository.ProductRepository;
import com.students.recipesapi.repository.RecipeIngredientRepository;
import com.students.recipesapi.repository.RecipeRepository;
import com.students.recipesapi.repository.UserRepository;
import org.h2.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);


    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, ProductRepository productRepository, RecipeRepository recipeRepository, RecipeIngredientRepository ingredientRepository) {
        return args -> {
            UserEntity jan = userRepository.save(new UserEntity("jkow@email.pl", "Jan", "Kowalski", "$2b$10$skJlwF3pkXBslldjZtIkmOdUJcImTbeWlweqZsJ7FoscY3shfx4Wq"));
            log.info("Preloaded: " + jan);

            Product maka = productRepository.save(new Product(
                    -1L,
                    "Mąką",
                    "123456789",
                    loadImage("mąka.png"),
                    jan
            ));
            log.info("Preloaded: " + maka);

            Product olej = productRepository.save(new Product(
                    -1L,
                    "Olej",
                    "123456789",
                    loadImage("olej.png"),
                    jan
            ));
            log.info("Preloaded: " + olej);

            Product serbialy = productRepository.save(new Product(
                    -1L,
                    "Ser biały",
                    "123456789",
                    loadImage("ser biały.png"),
                    jan
            ));
            log.info("Preloaded: " + serbialy);

            Product woda = productRepository.save(new Product(
                    -1L,
                    "Woda",
                    "123456789",
                    loadImage("woda.png"),
                    jan
            ));
            log.info("Preloaded: " + woda);

            Product ziemniaki = productRepository.save(new Product(
                    -1L,
                    "Ziemniaki",
                    "123456789",
                    loadImage("ziemniaki.png"),
                    jan
            ));
            log.info("Preloaded: " + ziemniaki);

            Product sospomidorowy = productRepository.save(new Product(
                    -1L,
                    "Sos pomidorowy",
                    "123456789",
                    loadImage("sos.png"),
                    jan
            ));
            log.info("Preloaded: " + sospomidorowy);

            Product drozdze = productRepository.save(new Product(
                    -1L,
                    "Drożdże",
                    "123456789",
                    loadImage("drożdże.png"),
                    jan
            ));
            log.info("Preloaded: " + drozdze);

            Product serzolty = productRepository.save(new Product(
                    -1L,
                    "Ser żółty",
                    "123456789",
                    loadImage("ser żółty.png"),
                    jan
            ));
            log.info("Preloaded: " + serzolty);

            Product schab = productRepository.save(new Product(
                    -1L,
                    "Schab",
                    "123456789",
                    loadImage("schab.png"),
                    jan
            ));
            log.info("Preloaded: " + schab);

            Product smalec = productRepository.save(new Product(
                    -1L,
                    "Smalec",
                    "123456789",
                    loadImage("smalec.png"),
                    jan
            ));
            log.info("Preloaded: " + smalec);

            Product kapustakiszona = productRepository.save(new Product(
                    -1L,
                    "Kapusta",
                    "123456789",
                    loadImage("kapusta.png"),
                    jan
            ));
            log.info("Preloaded: " + kapustakiszona);

            Product miesomielone = productRepository.save(new Product(
                    -1L,
                    "Mięso mielone",
                    "123456789",
                    loadImage("mięso mielone.png"),
                    jan
            ));
            log.info("Preloaded: " + miesomielone);

            Product cebula = productRepository.save(new Product(
                    -1L,
                    "Cebula",
                    "123456789",
                    loadImage("cebula.png"),
                    jan
            ));
            log.info("Preloaded: " + cebula);

            Product makaron = productRepository.save(new Product(
                    -1L,
                    "makaron",
                    "123456789",
                    loadImage("makaron.png"),
                    jan
            ));
            log.info("Preloaded: " + makaron);

            Product zurekwiniary = productRepository.save(new Product(
                    -1L,
                    "Żurek Winiary",
                    "123456789",
                    loadImage("żurek winiary.png"),
                    jan
            ));
            log.info("Preloaded: " + zurekwiniary);

            Product kielbasa = productRepository.save(new Product(
                    -1L,
                    "kielbasa",
                    "123456789",
                    loadImage("kiełbasa.png"),
                    jan
            ));
            log.info("Preloaded: " + kielbasa);

            Product jajka = productRepository.save(new Product(
                    -1L,
                    "Jajka",
                    "123456789",
                    loadImage("jajka.png"),
                    jan
            ));
            log.info("Preloaded: " + jajka);

            createRecipe(new Recipe(
                            -1L,
                            "Pierogi Ruskie",
                            "Smaczne i proste pierogi ze wschodu",
                            jan,
                            Collections.emptySet(),
                            new LinkedHashSet<>(Arrays.asList("Drugie Danie")),
                            loadImage("pierogi.png"),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            0.0),

                    new LinkedHashSet<>(Arrays.asList(ziemniaki, serbialy, maka, woda, olej)),
                    Arrays.asList("500g", "500g", "1kg", "800ml", "100ml"),
                    recipeRepository,
                    ingredientRepository);

            createRecipe(new Recipe(
                            -1L,
                            "Pizza",
                            "Niebanalna Margherita na grubym cieście",
                            jan,
                            Collections.emptySet(),
                            new LinkedHashSet<>(Arrays.asList("Z pieca")),
                            loadImage("pizza.png"),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            0.0),

                    new LinkedHashSet<>(Arrays.asList(maka, sospomidorowy, woda, drozdze, serzolty)),
                    Arrays.asList("500g", "100ml", "200ml", "10g", "100g"),
                    recipeRepository,
                    ingredientRepository);

            createRecipe(new Recipe(
                            -1L,
                            "Schabowy z kartoflami",
                            "Tradycyjny polski chabowy z ziemniakami",
                            jan,
                            Collections.emptySet(),
                            new LinkedHashSet<>(Arrays.asList("Mięso", "Drugie danie")),
                            loadImage("schabowy.png"),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            0.0),

                    new LinkedHashSet<>(Arrays.asList(ziemniaki, schab, smalec, kapustakiszona)),
                    Arrays.asList("1kg", "200g", "1", "1"),
                    recipeRepository,
                    ingredientRepository);

            createRecipe(new Recipe(
                            -1L,
                            "Spaghetti",
                            "Przegląd tygodnia w sosie pomidorowym",
                            jan,
                            Collections.emptySet(),
                            new LinkedHashSet<>(Arrays.asList("Szybkie", "Mięso")),
                            loadImage("spaghetii.png"),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            0.0),

                    new LinkedHashSet<>(Arrays.asList(miesomielone, sospomidorowy, cebula, makaron, olej)),
                    Arrays.asList("1kg", "500ml", "2", "1", "100ml"),
                    recipeRepository,
                    ingredientRepository);

            createRecipe(new Recipe(
                            -1L,
                            "Żurek",
                            "Szybki żurek z kiełbasą i jajkiem",
                            jan,
                            Collections.emptySet(),
                            new LinkedHashSet<>(Arrays.asList("Zupa")),
                            loadImage("żurek.png"),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString(),
                            0.0),

                    new LinkedHashSet<>(Arrays.asList(zurekwiniary, kielbasa, jajka)),
                    Arrays.asList("1", "1", "2"),
                    recipeRepository,
                    ingredientRepository);
        };
    }

    private static void createRecipe(Recipe recipe, LinkedHashSet<Product> products, List<String> quantities, RecipeRepository recipeRepository, RecipeIngredientRepository ingredientRepository) {
        recipe = recipeRepository.save(recipe);

        Set<RecipeIngredient> ingredients = new HashSet<>();
        int i = 0;
        for (Product product : products) {
            RecipeIngredient ingredient = new RecipeIngredient();
            ingredient.setId(new RecipeIngredientKey(recipe.getId(), product.getId()));
            ingredient.setRecipe(recipe);
            ingredient.setProduct(product);
            ingredient.setQuantity(quantities.get(i));
            ingredient = ingredientRepository.save(ingredient);
            ingredients.add(ingredient);
            i++;
        }
        recipe.setIngredients(ingredients);
        recipe = recipeRepository.save(recipe);
        log.info("Preloaded: " + recipe);
    }

    private static byte[] loadImage(String imageName) {
        Resource resource = new ClassPathResource("examples/" + imageName);
        try {
            InputStream input = resource.getInputStream();
            return IOUtils.readBytesAndClose(input, (int) resource.contentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
