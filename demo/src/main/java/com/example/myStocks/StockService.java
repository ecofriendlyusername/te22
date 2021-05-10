package com.example.myStocks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.IEXCloudTokenBuilder;
import pl.zankowski.iextrading4j.client.IEXTradingApiVersion;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

@Service
@Component
public class StockService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OwnedStockRepository osRepository;
	
	final IEXCloudClient cloudClient = IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_BETA_SANDBOX,
	          new IEXCloudTokenBuilder()
	                  .withPublishableToken("Tpk_18dfe6cebb4f41ffb219b9680f9acaf2")
	                  .withSecretToken("Tsk_3eedff6f5c284e1a8b9bc16c54dd1af3")
	                  .build());
	
	public List<OwnedStock> getOwnedStocksByEmail(String userEmail) {
		List<OwnedStock> userStockList = userRepository.findByEmail(userEmail).getOwnedStocks();
		return userStockList;
	}
	
	public List<OwnedStock> getOwnedStocksByName(String userName) {
		List<OwnedStock> userStockList = userRepository.findByName(userName).getOwnedStocks();
		return userStockList;
	}
	
	public boolean limitOrderBuyService(String email, String ticker, BigDecimal price, int amount) {
		//Thread? ScheduledTask????
		User u = userRepository.findByEmail(email);
		BigDecimal cashdraw = price.multiply(new BigDecimal(amount));
		if (cashdraw.compareTo(u.getCash()) == 1)
			return false;
		else {
			final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
		  	        .withSymbol(ticker)
		  	        .build());
			String myDate = LocalDate.now(ZoneId.of("America/New_York")) + " 18:00:00";
			LocalDateTime localDateTime = LocalDateTime.parse(myDate,
			    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );
			long end = localDateTime
			    .atZone(ZoneId.of("America/New_York"))
			    .toInstant().toEpochMilli();
			BigDecimal pricenow;
			while(System.currentTimeMillis() < end) {
				pricenow = quote.getIexRealtimePrice();
				System.out.println("pricenow: " + pricenow);
				if (pricenow.compareTo(price) <= 0) {
					System.out.println("price: " + price + ", pricenow: " + pricenow);
					u.setCash(u.getCash().subtract(cashdraw));
					if (osRepository.existsByUser_idAndTicker(u.getId(), ticker)) {
						OwnedStock os = osRepository.findByUser_idAndTicker(u.getId(), ticker);
						os.addStock(pricenow, amount);
					} else {
						u.addOwnedStock(ticker, pricenow, amount);
					}
					userRepository.save(u);
					return true; // ???
				}
			}
			return false;
		}
    } 
	
	public boolean marketOrderBuyService(String email, String ticker, int amount) {
		User u = userRepository.findByEmail(email);
		final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
	  	        .withSymbol(ticker)
	  	        .build());
		BigDecimal pricenow = quote.getIexRealtimePrice();
		BigDecimal cashdraw = pricenow.multiply(new BigDecimal(amount));
		if (cashdraw.compareTo(u.getCash()) == 1)
			return false;
		else {
			u.setCash(u.getCash().subtract(cashdraw));
			if (osRepository.existsByUser_idAndTicker(u.getId(), ticker)) {
				OwnedStock os = osRepository.findByUser_idAndTicker(u.getId(), ticker);
				os.addStock(pricenow, amount);
			} else {
				u.addOwnedStock(ticker, pricenow, amount);
			}
		}
		userRepository.save(u);
		return true;
	}
	
    public boolean limitOrderSellService(String email, String ticker, BigDecimal price, int amount) {
    	User u = userRepository.findByEmail(email);
		if (osRepository.existsByUser_idAndTicker(u.getId(), ticker))
			return false;
		else {
			OwnedStock os = osRepository.findByUser_idAndTicker(u.getId(), ticker);
					if (os.getStock_balance() < amount) 
						return false;
					else {
						final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
					  	        .withSymbol(ticker)
					  	        .build());
						String myDate = LocalDate.now(ZoneId.of("EDT")) + " 18:00:00";
						LocalDateTime localDateTime = LocalDateTime.parse(myDate,
						    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );
						long end = localDateTime
						    .atZone(ZoneId.of("EDT"))
						    .toInstant().toEpochMilli();
						BigDecimal pricenow;
						while(System.currentTimeMillis() < end) {
							pricenow = quote.getIexRealtimePrice();
							if (pricenow.compareTo(price) >= 1) {
								if (os.getStock_balance() == amount) {
									u.getOwnedStocks().remove(os);
									osRepository.delete(os);
								} else {
									os.sellStock(amount);
								}
								userRepository.save(u);
								return true;
							}
						}
						return false;
			}
		}
    }
    
    public boolean marketOrderSellService(String email, String ticker, int amount) {
    	User u = userRepository.findByEmail(email);
		final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
	  	        .withSymbol(ticker)
	  	        .build());
		BigDecimal pricenow = quote.getIexRealtimePrice();
		BigDecimal cashin = pricenow.multiply(new BigDecimal(amount));
		
		u.setCash(u.getCash().add(cashin));
		if (osRepository.existsByUser_idAndTicker(u.getId(), ticker)) {
			OwnedStock os = osRepository.findByUser_idAndTicker(u.getId(), ticker);
			if (os.getStock_balance() < amount)
				return false;
			else if (os.getStock_balance() == amount) {
				u.getOwnedStocks().remove(os);
    			osRepository.delete(os);
			} else {
				os.sellStock(amount);
			}
		} else {
			return false;
		}
		userRepository.save(u);
		return true;
    }
}
