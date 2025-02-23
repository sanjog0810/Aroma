package com.aroma.aromaBack.repository;

import com.aroma.aromaBack.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {
    Product findById(int id);
    void deleteById(int id);
}
