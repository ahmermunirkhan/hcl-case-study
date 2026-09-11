package com.fulfilment.application.monolith.products.adapters.database;

import com.fulfilment.application.monolith.products.domain.models.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {}
