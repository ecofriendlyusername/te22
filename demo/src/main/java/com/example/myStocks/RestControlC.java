package com.example.myStocks;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestControlC {
	@PostMapping(path="/testhere")
	  public String test() {
	    RegisterUser ru = new RegisterUser();
	    String s = ru.getPersonNamesByLastName("Alex").get(0);
	    return s;
	  }
}
