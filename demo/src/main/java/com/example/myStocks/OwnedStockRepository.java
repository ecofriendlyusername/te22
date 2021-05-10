package com.example.myStocks;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface OwnedStockRepository extends CrudRepository<OwnedStock, Integer> {
	
	OwnedStock findByUser_idAndTicker(int user_id, String ticker);

	boolean existsByUser_idAndTicker(int user_id, String ticker);
	
	OwnedStock findByTicker(String ticker);
	@Modifying
	@Query("update OwnedStock o set o.avgbprice = ?1 where o.id = ?2")
	int setFixedAvgbPriceFor(BigDecimal avgbprice, Integer id);
	
	@Modifying
	@Query("update OwnedStock o set o.stockbalance = ?1 where o.id = ?2")
	int setFixedStockBalanceFor(int stockbalance, Integer id);
}