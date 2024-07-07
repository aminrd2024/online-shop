package nz.co.chergenet.demo.repository;

import nz.co.chergenet.demo.model.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    @Override
    List<Product> findAll();

    Optional<Product> findByProductId(long productId);

    @Transactional
    @Modifying
    @Query("update Product p set p.name = ?1, p.price = ?2, p.quantity = ?3 where p.productId = ?4")
    void updateProduct(String name, double price, int quantity, long product_Id);
}
