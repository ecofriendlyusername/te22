package com.example.myStocks;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.zankowski.iextrading4j.api.marketdata.PriceType;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.api.stocks.v1.BatchStocks;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.IEXCloudTokenBuilder;
import pl.zankowski.iextrading4j.client.IEXTradingApiVersion;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.v1.BatchStocksRequestBuilder;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
// @RestConller // very different from just @Controller!
@Controller // This means that this class is a Controller
@RequestMapping(path="/home") // This means URL's start with /home (after Application path)
public class MainController {
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private StockService stockService;
  
  final IEXCloudClient cloudClient = IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_BETA_SANDBOX,
          new IEXCloudTokenBuilder()
                  .withPublishableToken("Tpk_18dfe6cebb4f41ffb219b9680f9acaf2")
                  .withSecretToken("Tsk_3eedff6f5c284e1a8b9bc16c54dd1af3")
                  .build());
 
  @GetMapping(path="/getUserStocks")
  public ResponseEntity<?> userStocks() {
	  OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  List<OwnedStock> slist = stockService.getOwnedStocksByEmail(oidcUser.getEmail());
	  List<MyStocks> stockList = new ArrayList<MyStocks>();
	  for (int i = 0; i < slist.size(); i++) {
		  stockList.add(new MyStocks(slist.get(i)));
	  }
	  // return ResponseEntity.ok(stringList);
	  return ResponseEntity.ok(stockList);
  }
  
  @GetMapping(path="/getStockPrice")
  public ResponseEntity<?> getStockPrice(@RequestParam String squote) {
	  final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
  	        .withSymbol(squote)
  	        .build());
	return ResponseEntity.ok(quote.getIexRealtimePrice());
  }
  
  public ResponseEntity<?> getStocksPrices(@RequestParam List<String> slist) {
	  List<BigDecimal> prices = new ArrayList<BigDecimal>();
	  for (int i = 0; i < slist.size(); i++) {
		  prices.add(cloudClient.executeRequest(new QuoteRequestBuilder()
		  	        .withSymbol(slist.get(i))
		  	        .build()).getIexRealtimePrice());
	  }
	return ResponseEntity.ok(prices);
  }

  @CrossOrigin(origins = "http://localhost:8080")
  @PostMapping(path="/limitOrderBuy") // Map ONLY POST Requests
  public ResponseEntity<?> limitOrderBuy (@RequestParam String ticker, @RequestParam BigDecimal price, @RequestParam int amount) {
    // @ResponseBody means the returned String is the response, not a view name
    // @RequestParam means it is a parameter from the GET or POST request
	 OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	 String email = oidcUser.getEmail();
	 if (!userRepository.existsByEmail(email)) {
		 return ResponseEntity.ok("User does not exist");
	 } else { 
		 if (stockService.limitOrderBuyService(email, ticker, price, amount)) 
			 return ResponseEntity.ok("Not Enough Cash!");
		 else
			 return ResponseEntity.ok("Transaction Excuted");
	 } 
	 // 돈 없으면 여기서 자르기
  } 
  
  @PostMapping(path="/marketOrderBuy") // Map ONLY POST Requests
  public ResponseEntity<?> marketOrderBuy (@RequestParam String ticker, @RequestParam int amount) {
    // @ResponseBody means the returned String is the response, not a view name
    // @RequestParam means it is a parameter from the GET or POST request
	 OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	 String email = oidcUser.getEmail();
	 if (!userRepository.existsByEmail(email)) {
		 return ResponseEntity.ok("User does not exist");
	 } else { 
		 if (stockService.marketOrderBuyService(email, ticker, amount))
			 return ResponseEntity.ok("Transaction Excuted");
		 else
			 return ResponseEntity.ok("Not Enough Cash!");
	 }
  }
  
  @PostMapping(path="/limitOrderSell") // Map ONLY POST Requests
  public ResponseEntity<?> sellStock (@RequestParam String ticker, @RequestParam BigDecimal price, @RequestParam int amount) {
    // @ResponseBody means the returned String is the response, not a view name
    // @RequestParam means it is a parameter from the GET or POST request
	  OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  String email = oidcUser.getEmail();
	  if (!userRepository.existsByEmail(email)) {
			 return ResponseEntity.ok("User does not exist");
		 } else { 
			 if (stockService.limitOrderSellService(email, ticker, price, amount))
				 return ResponseEntity.ok("Order requested");
			 else 
				 return ResponseEntity.ok("Not Enough Shares!");
		 }
  }
  
  @PostMapping(path="/marketOrderSell")
  public ResponseEntity<?> marketOrderSell(@RequestParam String ticker, @RequestParam int amount) {
	  OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  String email = oidcUser.getEmail();
	  if (!userRepository.existsByEmail(email)) {
			 return ResponseEntity.ok("User does not exist");
		 } else { 
			 if (stockService.marketOrderSellService(email, ticker, amount))
				 return ResponseEntity.ok("Transaction Excuted");
			 else
				 return ResponseEntity.ok("Not Enough Shares!");
		 }
  }
  
  @PostMapping(path="/addInterested")
  public ResponseEntity<?> addInterested(@RequestParam String ticker) {
	  OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  String email = oidcUser.getEmail();
	  if (!userRepository.existsByEmail(email)) {
		  return ResponseEntity.ok("User does not exist");
		  } else { 
			 if (stockService.addInterestedService(email, ticker))
				 return ResponseEntity.ok("stock added");
			 else
				 return ResponseEntity.ok("!");
		 }
  }
  
  @GetMapping(path="/getInterested")
  public ResponseEntity<?> getInterested() {
	  OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  String email = oidcUser.getEmail();
	  System.out.println("I was called");
	  if (!userRepository.existsByEmail(email)) {
		  return ResponseEntity.ok("User does not exist");
		  } else { 
			 return ResponseEntity.ok(stockService.getInterestedService(email));
		 }
  }
  
  @PostMapping(path="/deleteInterested")
  public ResponseEntity<?> deleteInterested(@RequestParam String ticker) {
	  OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  String email = oidcUser.getEmail();
	return ResponseEntity.ok(stockService.deleteInterestedService(email, ticker));
  }
  
  @GetMapping(path="/getUserRanking")
  public ResponseEntity<?> getUserRanking() {
	  List<UserPortfolio> portfolios = stockService.getUserRankingService();
	  Collections.sort(portfolios, new SortbyReturn());
	  return ResponseEntity.ok(portfolios);
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<User> getAllUsers() {
    // This returns a JSON or XML with the users
    return userRepository.findAll();
  }
  
}