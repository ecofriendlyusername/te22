package com.example.myStocks;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.IEXCloudTokenBuilder;
import pl.zankowski.iextrading4j.client.IEXTradingApiVersion;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

@SpringBootApplication
@RestController
public class MyStocksApplication extends WebSecurityConfigurerAdapter{
	@GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
	 
	public static void main(String[] args) {
	final IEXCloudClient cloudClient = IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_BETA_SANDBOX,
	              new IEXCloudTokenBuilder()
	                      .withPublishableToken("Tpk_18dfe6cebb4f41ffb219b9680f9acaf2")
	                      .withSecretToken("Tsk_3eedff6f5c284e1a8b9bc16c54dd1af3")
	                      .build());
	
	/* Runnable helloRunnable = new Runnable() {
	    public void run() {
	    	final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
	    	        .withSymbol("AAPL")
	    	        .build());
	    	System.out.println(quote);
	    }
	};

	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	executor.scheduleAtFixedRate(helloRunnable, 0, 1, TimeUnit.SECONDS);
	
	*/
	SpringApplication.run(MyStocksApplication.class, args);
	}
}