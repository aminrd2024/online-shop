package nz.co.chergenet.demo.repository;

import nz.co.chergenet.demo.model.Card;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Long> {
}
