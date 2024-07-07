package nz.co.chergenet.demo.contoller;

import nz.co.chergenet.demo.model.Card;
import nz.co.chergenet.demo.model.Product;
import nz.co.chergenet.demo.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "shop/v1/online")
@Validated
public class ShopController {

    private final StoreService service;

    public ShopController(StoreService service) {
        this.service = service;
    }

    @GetMapping(path = "products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProduct());
    }

    @PostMapping(path = "cards")
    public ResponseEntity<Card> addCard(@RequestBody Card card, @RequestHeader UUID guest_customer_uuid) {
        card.setGuestCustomerUUID(guest_customer_uuid);
        Card savedCard = service.saveCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }

}
