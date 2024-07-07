package nz.co.chergenet.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nz.co.chergenet.demo.mapper.PurchasedItemListConverter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "card")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Convert(converter = PurchasedItemListConverter.class)
    @Column(name = "purchased_item_list", columnDefinition = "TEXT")
    private List<PurchasedItemDto> purchasedItemList;

    @Column(name = "guest-customer-uuid")
    private UUID guestCustomerUUID;
}
