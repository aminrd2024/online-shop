package nz.co.chergenet.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.chergenet.demo.service.StoreService;
import nz.co.chergenet.demo.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import nz.co.chergenet.demo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void whenSaveProduct_thenProductShouldBeSaved() throws Exception {
        Product product = new Product(1L, "test_item", 100.80, 10);

        when(service.saveProduct(product)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/shop/v1/admin/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.name", is("test_item")))
                .andExpect(jsonPath("$.price", is(100.80)))
                .andExpect(jsonPath("$.quantity", is(10)));
    }

    @Test
    public void whenUpdateProduct_thenProductShouldBeUpdated() throws Exception {
        Product product = new Product(1L, "test_item", 100.80, 10);

        doNothing().when(service).updateProduct(any(Product.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/shop/v1/admin/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated."));
    }

    @Test
    public void whenDeleteProduct_thenProductShouldBeDeleted() throws Exception {
        long productId = 1L;

        doNothing().when(service).deleteProduct(productId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/shop/v1/admin/product/1", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("product deleted"));
    }
}

