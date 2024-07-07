package nz.co.chergenet.demo.service;

import lombok.extern.slf4j.Slf4j;
import nz.co.chergenet.demo.model.Card;
import nz.co.chergenet.demo.model.Product;
import nz.co.chergenet.demo.model.PurchasedItemDto;
import nz.co.chergenet.demo.repository.CardRepository;
import nz.co.chergenet.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
@Transactional
@Slf4j
public class StoreService {

    private final ProductRepository productRepository;
    private final CardRepository cardRepository;

    public StoreService(ProductRepository productRepository, CardRepository cardRepository) {
        this.productRepository = productRepository;
        this.cardRepository = cardRepository;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void updateProduct(Product product) {
        productRepository.updateProduct(product.getName(), product.getPrice(), product.getQuantity(), product.getProductId());
    }

    public void deleteProduct(long productId) {
        productRepository.deleteById(productId);
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public Card saveCard(Card card) {
        card.getPurchasedItemList()
                .forEach(item -> {
                    Optional<Product> product = productRepository.findByProductId(item.getProductId());
                    if (product.isPresent()) {
                        int quantity = product.get().getQuantity();
                        if (quantity <= 0 || quantity < item.getQuantity()) {
                            log.info("Product {} is not available.", product.get().getName());
                            throw new RuntimeException(String.format("Product %s is not available.", product.get().getName()));
                        } else {
                            saveProduct(Product.builder()
                                    .name(product.get().getName())
                                    .productId(item.getProductId())
                                    .price(product.get().getPrice())
                                    .quantity(quantity - item.getQuantity())
                                    .build());
                        }
                    }else {
                        log.info("Product {} is not found.", item.getProductId());
                        throw new RuntimeException(String.format("Product %s is not available.", item.getProductId()));
                    }
                });
        return cardRepository.save(card);
    }


}
