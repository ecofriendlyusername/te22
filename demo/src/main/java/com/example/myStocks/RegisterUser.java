package com.example.myStocks;
import java.util.List;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

public class RegisterUser {
   private LdapTemplate ldapTemplate;

   public List<String> getPersonNamesByLastName(String lastName) {

	      LdapQuery query = query()
	         .base("dc=261consulting,dc=com")
	         .attributes("cn", "sn")
	         .where("objectclass").is("person")
	         .and("sn").is(lastName);

	      return ldapTemplate.search(query,
	         new AttributesMapper<String>() {
	            public String mapFromAttributes(Attributes attrs)
	               throws NamingException {

	               return (String) attrs.get("cn").get();
	            }
	         });
	   }
}

