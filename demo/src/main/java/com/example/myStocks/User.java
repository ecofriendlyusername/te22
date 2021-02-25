package com.example.myStocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name = "User") // This tells Hibernate to make a table out of this class
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name;
	
	private String email;
	
	private BigDecimal cash;
	
	// @OneToMany(mappedBy="user")
	// @JoinColumn(name = "user_id")
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	private List<OwnedStock> ostocks = new ArrayList<>();;
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	// public void setTickers(String ticker, BigDecimal price, int amount) {
	public void addOwnedStock(String ticker, BigDecimal price, int amount) {
		// this.tickers = tickers;
		if (ostocks == null) {
			ostocks = new ArrayList<OwnedStock>();
			OwnedStock os = new OwnedStock();
			os.addStock(price, amount);
			os.setTicker(ticker); 
			ostocks.add(os);
		} else {
			if (containsName(ostocks, ticker)) {
				int indx = getIndexByProperty(this.ostocks, ticker);
				ostocks.get(indx).addStock(price, amount);
			} else {
				OwnedStock os = new OwnedStock();
				os.addStock(price, amount);
				os.setTicker(ticker);
				ostocks.add(os);
			}
		}
	}
	
	public void addOstock(OwnedStock stock) { 
		if (ostocks == null) {
			ostocks = new ArrayList<>();
		} 
		stock.setUser(this);
		ostocks.add(stock);
	}
	
	public List<OwnedStock> getOwnedStocks() {
		return ostocks;
	}
	
	public boolean containsName(final List<OwnedStock> list, final String name){
	    return list.stream().filter(o -> o.getTicker().equals(name)).findFirst().isPresent();
	}
	
	public int getIndexByProperty(final List<OwnedStock> list, String yourString) {
        for (int i = 0; i < list.size(); i++) {
            if (list !=null && list.get(i).getTicker().equals(yourString)) {
                return i;
            }
        }
        return -1;// not there is list
    }

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

}