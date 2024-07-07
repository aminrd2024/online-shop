package nz.co.chergenet.demo;

import nz.co.chergenet.demo.model.Product;
import nz.co.chergenet.demo.model.Card;
import nz.co.chergenet.demo.model.PurchasedItemDto;
import nz.co.chergenet.demo.repository.CardRepository;
import nz.co.chergenet.demo.repository.ProductRepository;
import nz.co.chergenet.demo.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class StoreServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StoreService service;

    private Product product;
    private Card card;
    private PurchasedItemDto item;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setProductId(1);
        product.setName("test_item");
        product.setPrice(100.80);
        product.setQuantity(10);

        item = new PurchasedItemDto();
        item.setProductId(1);
        item.setQuantity(2);

        card = new Card();
        card.setId(1);
        card.setGuestCustomerUUID(UUID.randomUUID());
        card.setPurchasedItemList(Arrays.asList(item));
    }

    @Test
    public void whenSaveProduct_thenProductShouldBeSaved() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = service.saveProduct(product);

        assertThat(savedProduct).isNotNull();
        assertEquals(savedProduct.getProductId(), 1l);
        assertEquals(savedProduct.getName(), "test_item");
        assertEquals(savedProduct.getPrice(), 100.80);
        assertEquals(savedProduct.getQuantity(), 10);
    }

    @Test
    public void whenUpdateProduct_thenProductShouldBeUpdated() {
        doNothing().when(productRepository).updateProduct(
                eq(product.getName()),
                eq(product.getPrice()),
                eq(product.getQuantity()),
                eq(product.getProductId())
        );

        service.updateProduct(product);

        verify(productRepository, times(1)).updateProduct(
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getProductId()
        );
    }

    @Test
    public void whenDeleteProduct_thenProductShouldBeDeleted() {
        doNothing().when(productRepository).deleteById(product.getProductId());

        service.deleteProduct(product.getProductId());

        verify(productRepository, times(1)).deleteById(product.getProductId());
    }

    @Test
    public void whenGetProducts_thenAllProductsShouldBeReturned() {
        List<Product> products = Arrays.asList(
                product,
                new Product(2L, "test_item2", 200.50, 20),
                new Product(3L, "test_item3", 300.00, 30)
        );

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = service.getAllProduct();

        assertThat(result).isNotNull();
        assertEquals(3, result.size());

        // Verify all properties for all products
        Product product1 = result.get(0);
        assertEquals(1L, product1.getProductId());
        assertEquals("test_item", product1.getName());
        assertEquals(100.80, product1.getPrice());
        assertEquals(10, product1.getQuantity());

        Product product2 = result.get(1);
        assertEquals(2L, product2.getProductId());
        assertEquals("test_item2", product2.getName());
        assertEquals(200.50, product2.getPrice());
        assertEquals(20, product2.getQuantity());

        Product product3 = result.get(2);
        assertEquals(3L, product3.getProductId());
        assertEquals("test_item3", product3.getName());
        assertEquals(300.00, product3.getPrice());
        assertEquals(30, product3.getQuantity());
    }

    @Test
    public void whenSaveCard_thenCardShouldBeSaved() {
        // Mock findByProductId to return the product
        when(productRepository.findByProductId(1)).thenReturn(Optional.of(product));
        // Mock saveProduct to return the updated product
        when(productRepository.save(any(Product.class))).thenReturn(product);
        // Mock saveCard to return the card
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card savedCard = service.saveCard(card);

        assertThat(savedCard).isNotNull();
        assertEquals(savedCard.getId(), 1L);
        assertEquals(savedCard.getGuestCustomerUUID(), card.getGuestCustomerUUID());
        assertThat(savedCard.getPurchasedItemList()).hasSize(1);
        assertEquals(savedCard.getPurchasedItemList().get(0).getProductId(), item.getProductId());
        assertEquals(savedCard.getPurchasedItemList().get(0).getQuantity(), item.getQuantity());

        // Verify the product update
        verify(productRepository, times(1)).save(argThat(updatedProduct ->
                updatedProduct.getProductId() == product.getProductId() &&
                        updatedProduct.getName().equals(product.getName()) &&
                        updatedProduct.getPrice() == product.getPrice() &&
                        updatedProduct.getQuantity() == (product.getQuantity() - item.getQuantity())
        ));

        // Verify the card save
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    public void whenSaveCardWithUnavailableProduct_thenShouldThrowException() {
        // Mock findByProductId to return the product with insufficient quantity
        product.setQuantity(1);
        when(productRepository.findByProductId(1)).thenReturn(Optional.of(product));

        RuntimeException thrownException = null;
        try {
            service.saveCard(card);
        } catch (RuntimeException e) {
            thrownException = e;
        }

        assertThat(thrownException).isNotNull();
        assertEquals(thrownException.getMessage(), "Product test_item is not available.");

        // Verify the product was not saved
        verify(productRepository, never()).save(any(Product.class));

        // Verify the card was not saved
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    public void whenSaveCardWithNonExistingProduct_thenShouldThrowException() {
        // Mock findByProductId to return empty Optional for all product IDs
        when(productRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

        // Create a card with non-existing products
        PurchasedItemDto nonExistingItem = new PurchasedItemDto();
        nonExistingItem.setProductId(999L); // Assuming 999L does not exist
        nonExistingItem.setQuantity(1);

        Card cardWithNonExistingProduct = new Card();
        cardWithNonExistingProduct.setId(1);
        cardWithNonExistingProduct.setGuestCustomerUUID(UUID.randomUUID());
        cardWithNonExistingProduct.setPurchasedItemList(Arrays.asList(nonExistingItem));

        RuntimeException thrownException = null;
        try {
            service.saveCard(cardWithNonExistingProduct);
        } catch (RuntimeException e) {
            thrownException = e;
        }

        assertThat(thrownException).isNotNull();
        assertEquals(thrownException.getMessage(), "Product 999 is not available.");

        // Verify the product was not saved
        verify(productRepository, never()).save(any(Product.class));

        // Verify the card was not saved
        verify(cardRepository, never()).save(any(Card.class));
    }

}
