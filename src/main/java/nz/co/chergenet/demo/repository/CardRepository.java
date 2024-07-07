package nz.co.chergenet.demo.repository;

import nz.co.chergenet.demo.model.Card;
import nz.co.chergenet.demo.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CardRepository extends CrudRepository<Card, Long> {
}
