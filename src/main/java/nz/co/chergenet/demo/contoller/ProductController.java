package nz.co.chergenet.demo.contoller;

import jakarta.validation.Valid;
import nz.co.chergenet.demo.model.Product;
import nz.co.chergenet.demo.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "shop/v1/admin")
@Validated
public class ProductController {

    private final StoreService service;

    public ProductController(StoreService service) {
        this.service = service;
    }

    @PostMapping(path = "product")
    public ResponseEntity<Product> saveProduct(@Valid @RequestBody Product product) {
        Product savedProduct = service.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping(path = "product")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody Product product) {
        service.updateProduct(product);
        return ResponseEntity.status(HttpStatus.OK).body("Product updated.");
    }

    @DeleteMapping(path = "product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") long productId) {
        service.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("product deleted");
    }

}
