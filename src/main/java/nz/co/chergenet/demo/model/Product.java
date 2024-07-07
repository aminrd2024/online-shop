package nz.co.chergenet.demo.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;

    @NotBlank(message = "Product's name is missing")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull(message = "Product's price is missing")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product's price must be greater than zero")
    @Column(name = "price", nullable = false)
    private double price;

    @NotNull(message = "Product's quantity is missing")
    @Min(value = 0, message = "Product's quantity must be non-negative")
    @Column(name = "quantity", nullable = false)
    private int quantity;
}
