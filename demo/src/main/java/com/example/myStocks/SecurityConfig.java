package com.example.myStocks;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;



@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired 
    private UserRepository userRepository; 
    
    @Override
    public void configure(HttpSecurity http) throws Exception {

    	http
        .authorizeRequests(a -> a
            .antMatchers("/", "/error", "/webjars/**").permitAll()
            .anyRequest().authenticated()
        )
        .logout(l -> l
                .logoutSuccessUrl("/").permitAll()
            )
        .csrf().disable()
        .exceptionHandling(e -> e
            .authenticationEntryPoint((AuthenticationEntryPoint) new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(this.oidcUserService())
                )
            );
    }
    //
    /*csrf(c -> c
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )*/
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
    	final OidcUserService delegate = new OidcUserService();
    	
    	return (userRequest) -> {
    		OidcUser oidcUser = delegate.loadUser(userRequest);
    		if (!userRepository.existsByEmail(oidcUser.getAttribute("email"))) {  
    	   		 User n = new User();
    	   	        n.setName(oidcUser.getAttribute("name"));
    	   	        n.setEmail(oidcUser.getAttribute("email"));
    	   	        n.setCash(new BigDecimal(10000));
    	   	        userRepository.save(n);  
    			  }
    	       return oidcUser;
    	};
   }
}