package vn.hoidanit.laptopshop.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Product save (Product product);

    // List<Product> findAll();
}
