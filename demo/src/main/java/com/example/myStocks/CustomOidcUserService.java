package com.example.myStocks;

import java.math.BigDecimal; 

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository; 
    
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        try {
             return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

     private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
    	 if (!userRepository.existsByEmail(oidcUser.getAttribute("email"))) {  
    		 User n = new User();
    	        n.setName(oidcUser.getAttribute("name"));
    	        n.setEmail(oidcUser.getAttribute("email"));
    	        n.setCash(new BigDecimal(10000));
    	        userRepository.save(n);  
		  }
        return oidcUser;
    }
}