package com.clothes.store.config;

import com.clothes.store.model.Product;
import com.clothes.store.model.ProductVariant;
import com.clothes.store.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedCatalog(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            Product linenShirt = product(
                    "linen-resort-shirt",
                    "Linen Resort Shirt",
                    "North Loom",
                    "Shirts",
                    "Breathable long-staple linen shirt with a soft camp collar and relaxed drape.",
                    "100% linen",
                    "Relaxed",
                    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=900&q=80",
                    "1899.00",
                    "NLM-LRS"
            );

            Product denimJacket = product(
                    "selvedge-denim-jacket",
                    "Selvedge Denim Jacket",
                    "Thread Haus",
                    "Jackets",
                    "Mid-weight selvedge denim jacket with reinforced seams and matte metal hardware.",
                    "13 oz cotton denim",
                    "Regular",
                    "https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80",
                    "4299.00",
                    "THD-SDJ"
            );

            Product pleatedTrouser = product(
                    "tailored-pleated-trouser",
                    "Tailored Pleated Trouser",
                    "Atelier Row",
                    "Trousers",
                    "Clean pleated trouser with a tapered leg, deep pockets, and structured waistband.",
                    "Cotton-viscose blend",
                    "Tapered",
                    "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=900&q=80",
                    "2599.00",
                    "ATR-TPT"
            );

            Product knitPolo = product(
                    "textured-knit-polo",
                    "Textured Knit Polo",
                    "Mono Stitch",
                    "Polos",
                    "Fine-gauge knit polo with tonal buttons and a smooth ribbed hem.",
                    "Cotton knit",
                    "Slim",
                    "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?auto=format&fit=crop&w=900&q=80",
                    "2199.00",
                    "MST-TKP"
            );

            Product summerDress = product(
                    "bias-cut-midi-dress",
                    "Bias Cut Midi Dress",
                    "Cedar & Silk",
                    "Dresses",
                    "Fluid midi dress with a bias-cut silhouette and adjustable shoulder straps.",
                    "Viscose satin",
                    "Easy",
                    "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=900&q=80",
                    "3499.00",
                    "CDS-BCM"
            );

            Product overshirt = product(
                    "utility-canvas-overshirt",
                    "Utility Canvas Overshirt",
                    "Field Form",
                    "Overshirts",
                    "Durable canvas overshirt with oversized pockets and a softened garment wash.",
                    "Cotton canvas",
                    "Boxy",
                    "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=900&q=80",
                    "2999.00",
                    "FDF-UCO"
            );

            Product organicTee = product(
                    "organic-heavyweight-tee",
                    "Organic Heavyweight Tee",
                    "Everyday Works",
                    "T-Shirts",
                    "Dense organic cotton tee with a smooth hand feel and a clean everyday neckline.",
                    "Organic cotton jersey",
                    "Regular",
                    "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80",
                    "999.00",
                    "EDW-OHT"
            );

            Product straightJeans = product(
                    "straight-leg-vintage-jeans",
                    "Straight Leg Vintage Jeans",
                    "Blue Mill",
                    "Jeans",
                    "Washed straight-leg jeans with a mid rise, classic five-pocket styling, and soft fading.",
                    "Cotton denim",
                    "Straight",
                    "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80",
                    "2799.00",
                    "BLM-SVJ"
            );

            Product bomber = product(
                    "cropped-nylon-bomber",
                    "Cropped Nylon Bomber",
                    "Aero Lane",
                    "Jackets",
                    "Lightweight cropped bomber with rib trims, snap pockets, and a smooth satin lining.",
                    "Recycled nylon",
                    "Cropped",
                    "https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80",
                    "3799.00",
                    "ARL-CNB"
            );

            Product slipSkirt = product(
                    "satin-slip-skirt",
                    "Satin Slip Skirt",
                    "Cedar & Silk",
                    "Skirts",
                    "Bias-cut satin skirt with a soft elastic waist and an easy midi length.",
                    "Viscose satin",
                    "Easy",
                    "https://images.unsplash.com/photo-1583496661160-fb5886a13d44?auto=format&fit=crop&w=900&q=80",
                    "2299.00",
                    "CDS-SSK"
            );

            Product hoodie = product(
                    "performance-zip-hoodie",
                    "Performance Zip Hoodie",
                    "Motion Lab",
                    "Hoodies",
                    "Technical zip hoodie with stretch panels, a structured hood, and breathable pockets.",
                    "Cotton-poly stretch fleece",
                    "Athletic",
                    "https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=900&q=80",
                    "2499.00",
                    "MTL-PZH"
            );

            Product tank = product(
                    "ribbed-cotton-tank",
                    "Ribbed Cotton Tank",
                    "Mono Stitch",
                    "Tops",
                    "Ribbed cotton tank with a scoop neck, clean binding, and a close layering fit.",
                    "Ribbed cotton",
                    "Fitted",
                    "https://images.unsplash.com/photo-1618354691229-88d47f285158?auto=format&fit=crop&w=900&q=80",
                    "899.00",
                    "MST-RCT"
            );

            productRepository.saveAll(List.of(
                    linenShirt,
                    denimJacket,
                    pleatedTrouser,
                    knitPolo,
                    summerDress,
                    overshirt,
                    organicTee,
                    straightJeans,
                    bomber,
                    slipSkirt,
                    hoodie,
                    tank
            ));
        };
    }

    private Product product(String slug, String name, String brand, String category, String description,
                            String material, String fit, String imageUrl, String price, String skuPrefix) {
        Product product = new Product(slug, name, brand, category, description, material, fit, imageUrl, new BigDecimal(price));
        product.addVariant(new ProductVariant(skuPrefix + "-S-BLK", "S", "Black", 8));
        product.addVariant(new ProductVariant(skuPrefix + "-M-BLK", "M", "Black", 12));
        product.addVariant(new ProductVariant(skuPrefix + "-L-BLK", "L", "Black", 7));
        product.addVariant(new ProductVariant(skuPrefix + "-M-NAV", "M", "Navy", 9));
        return product;
    }
}
