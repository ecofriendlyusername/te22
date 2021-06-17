package com.example.myStocks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
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
		class LimitOrder {
            Timer timer = new Timer();
            TimerTask exitApp = new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            };

            public LimitOrder(Quote quote, User u, BigDecimal cashdraw) {
                Calendar testdate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
                testdate.set(Calendar.HOUR_OF_DAY, 18);
                testdate.set(Calendar.MINUTE, 0);
                testdate.set(Calendar.SECOND, 0);
                timer.schedule(exitApp, testdate.getTime());
                class priceCheck extends TimerTask {
                    public void run() {
                    	BigDecimal pricenow = quote.getIexRealtimePrice();
        				if (pricenow.compareTo(price) <= 0) {
        					u.setCash(u.getCash().subtract(cashdraw));
        					if (osRepository.existsByUser_idAndTicker(u.getId(), ticker)) {
        						OwnedStock os = osRepository.findByUser_idAndTicker(u.getId(), ticker);
        						os.addStock(pricenow, amount);
        					} else {
        						u.addOwnedStock(ticker, pricenow, amount);
        					}
        					userRepository.save(u);
        				}
        			
                    }
                }
                Timer timer2 = new Timer();
                timer2.schedule(new priceCheck(), 0, 500);
            }
        }
		User u = userRepository.findByEmail(email);
		BigDecimal cashdraw = price.multiply(new BigDecimal(amount));
		if (cashdraw.compareTo(u.getCash()) == 1) { return false; }
		else {
			final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
		  	        .withSymbol(ticker)
		  	        .build());
			new LimitOrder(quote, u, cashdraw);
			return true;
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
	
	public boolean marketOrderBuyServiceReservedTest(String email, String ticker, int amount) {
		// TEST 나중에 String return 함수로 바꾸기
		Timer timer = new Timer();
        Calendar testdate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        System.out.println();
        if (testdate.get(Calendar.DAY_OF_WEEK) == 7 || testdate.get(Calendar.DAY_OF_WEEK) == 1) {
            System.out.println("주말에는 예약주문을 받지 않습니다");
            return false;
        }
        if (testdate.get(Calendar.HOUR_OF_DAY) < 8) {
            // 아침에 주문
            System.out.println("Market Order reserved for 9:30AM today");
            testdate.set(Calendar.HOUR_OF_DAY, 9);
            testdate.set(Calendar.MINUTE, 30);
            testdate.set(Calendar.SECOND, 0);
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("여기서 시장가주문 함수 Call");
                            marketOrderBuyService(email, ticker, amount);
                        }
                    }, testdate.getTime()
            );
        } else if (testdate.get(Calendar.HOUR_OF_DAY) >= 18) {
            if (testdate.get(Calendar.DAY_OF_WEEK) == 6) {
                System.out.println("Market Order reserved for 9:30AM next week Monday");
                testdate.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH + 3);
            } else {
                System.out.println("Market Order reserved for 9:30AM tomorrow");
                testdate.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH + 1);
            }
            testdate.set(Calendar.HOUR_OF_DAY, 9);
            testdate.set(Calendar.MINUTE, 30);
            testdate.set(Calendar.SECOND, 0);
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("여기서 시장가주문 함수 Call");
                            marketOrderBuyService(email, ticker, amount);
                        }
                    }, testdate.getTime()
            );
        } else {
            System.out.println("예약 주문 가능 시간이 아닙니다");
        }
        return false;
	}
	
    public boolean limitOrderSellService(String email, String ticker, BigDecimal price, int amount) {
    	class LimitOrder {
            Timer timer = new Timer();
            TimerTask exitApp = new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            };

            public LimitOrder(User u, Quote quote, OwnedStock os) {
                Calendar testdate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
                testdate.set(Calendar.HOUR_OF_DAY, 18);
                testdate.set(Calendar.MINUTE, 0);
                testdate.set(Calendar.SECOND, 0);
                timer.schedule(exitApp, testdate.getTime());
                class priceCheck extends TimerTask {
                    public void run() {
                    	BigDecimal pricenow = quote.getIexRealtimePrice();
						if (pricenow.compareTo(price) >= 1) {
							if (os.getStock_balance() == amount) {
								u.getOwnedStocks().remove(os);
								osRepository.delete(os);
							} else {
								os.sellStock(amount);
							}
							userRepository.save(u);
						}
                    }
                }
                Timer timer2 = new Timer();
                timer2.schedule(new priceCheck(), 0, 500);
            }
        }
    	User u = userRepository.findByEmail(email);
    	if (osRepository.existsByUser_idAndTicker(u.getId(), ticker))
			return false;
		else {
			OwnedStock os = osRepository.findByUser_idAndTicker(u.getId(), ticker);
			if (os.getStock_balance() < amount) { return false; } 
			else {
				final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
			  	        .withSymbol(ticker)
			  	        .build());
				new LimitOrder(u, quote, os);
				return true;
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

    public boolean marketOrderSellServiceReservedTest(String email, String ticker, int amount) {
		// TEST 나중에 String return 함수로 바꾸기
    	Timer timer = new Timer();
        Calendar testdate = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        System.out.println();
        if (testdate.get(Calendar.DAY_OF_WEEK) == 7 || testdate.get(Calendar.DAY_OF_WEEK) == 1) {
            System.out.println("주말에는 예약주문을 받지 않습니다");
            return false;
        }
        if (testdate.get(Calendar.HOUR_OF_DAY) < 8) {
            // 아침에 주문
            System.out.println("Market Order reserved for 9:30AM today");
            testdate.set(Calendar.HOUR_OF_DAY, 9);
            testdate.set(Calendar.MINUTE, 30);
            testdate.set(Calendar.SECOND, 0);
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("여기서 시장가주문 함수 Call");
                            marketOrderSellService(email, ticker, amount);
                        }
                    }, testdate.getTime()
            );
        } else if (testdate.get(Calendar.HOUR_OF_DAY) >= 18) {
            if (testdate.get(Calendar.DAY_OF_WEEK) == 6) {
                System.out.println("Market Order reserved for 9:30AM next week Monday");
                testdate.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH + 3);
            } else {
                System.out.println("Market Order reserved for 9:30AM tomorrow");
                testdate.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH + 1);
            }
            testdate.set(Calendar.HOUR_OF_DAY, 9);
            testdate.set(Calendar.MINUTE, 30);
            testdate.set(Calendar.SECOND, 0);
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("여기서 시장가주문 함수 Call");
                            marketOrderSellService(email, ticker, amount);
                        }
                    }, testdate.getTime()
            );
        } else {
            System.out.println("예약 주문 가능 시간이 아닙니다");
        }
        return false;
	}
	
	public boolean addInterestedService(String email, String ticker) {
		// TODO Auto-generated method stub
		User u = userRepository.findByEmail(email);
		String[] tickers = u.getInterested().split(" ");
		boolean exists = false;
		for (int i = 0; i < tickers.length; i++) {
			if (tickers[i] == ticker) {
				exists = true;
				break;
			}
			
		}
		if (!exists) u.setInterested(u.getInterested() + ticker + " ");
		userRepository.save(u);
		return true;
	}
	
	//public List<TickerOnly> getInterestedService(String email) {
	public String[] getInterestedService(String email) {
		User u = userRepository.findByEmail(email);
		String tickers = u.getInterested();
		String[] s = tickers.split(" ");
		return s;
	}

	public Object deleteInterestedService(String email, String ticker) {
		// TODO Auto-generated method stub
		User u = userRepository.findByEmail(email);
		String[] tickers = u.getInterested().split(" ");
		boolean exists = false;
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < tickers.length; i++) {
			if (tickers[i].equals(ticker)) {
				continue;
			}
			s.append(tickers[i]);
			s.append(" ");
		}
		u.setInterested(s.toString());
		userRepository.save(u);
		return null;
	}

	// 시간차가 크게 중요하지 않으므로 우선 이렇게 
	// After hours?
	public List<UserPortfolio> getUserRankingService() {
		// TODO Auto-generated method stub
		List<UserPortfolio> portfolios = new ArrayList<UserPortfolio>();
		for (User u : userRepository.findAll()) {
			UserPortfolio up = new UserPortfolio(u.getName());
			BigDecimal profitSum = new BigDecimal(0);
			BigDecimal myMoney = new BigDecimal(0);
			for (OwnedStock o : userRepository.findByEmail(u.getEmail()).getOwnedStocks()) {
				final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
			  	        .withSymbol(o.getTicker())
			  	        .build());
				BigDecimal profit = quote.getLatestPrice().subtract(o.getAvg_bprice());
				profitSum = profit.multiply(new BigDecimal(o.getStock_balance()));
				myMoney = o.getAvg_bprice().multiply(new BigDecimal(o.getStock_balance()));
			}
			up.setRturn(profitSum.divide(myMoney, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
			portfolios.add(up);
		}
		return portfolios;
	}
}
