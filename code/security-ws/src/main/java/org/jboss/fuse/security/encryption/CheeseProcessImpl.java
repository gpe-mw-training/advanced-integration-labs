package org.jboss.fuse.security.encryption;

import org.apache.camel.Body;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebService(targetNamespace = "http://encryption.security.fuse.jboss.org", name = "CheeseProcess")
public class CheeseProcessImpl implements CheeseProcess {

    private Map<String, Country> countries = new HashMap<String,Country>();
    private Map<String,Set<Country>> map = new HashMap<String,Set<Country>>();

    public CheeseProcessImpl() {
        initList();
    }

    private void initList() {
        // Countries
        Country c1 = new Country();
        c1.setId("Belgium");
        c1.setSource("cow");
        c1.setCheese("Orval");
        c1.setRating("8/10");

        Country c2 = new Country();
        c2.setId("France");
        c2.setSource("cow");
        c2.setCheese("Camembert");
        c2.setRating("7/10");

        Country c3 = new Country();
        c3.setId("France");
        c3.setSource("goat");
        c3.setCheese("Rocamadour");
        c3.setRating("6/10");

        Country c4 = new Country();
        c4.setId("Italy");
        c4.setSource("cow");
        c4.setCheese("Parmezan");
        c4.setRating("9/10");

        Set<Country> set1 = new HashSet<Country>();
        Set<Country> set2 = new HashSet<Country>();
        Set<Country> set3 = new HashSet<Country>();
        Set<Country> set4 = new HashSet<Country>();

        set1.add(c1);
        set2.add(c2);
        set3.add(c3);
        set4.add(c4);

        map.put("s1", set1);
        map.put("s2", set2);
        map.put("s3", set3);
        map.put("s4", set4);
    }

    public Country processCheese(String cheese) {
        Country found = search(map, cheese);
        return found;
    }

    private static Country search(Map<String, Set<Country>> map, String cheese) {
        for(Set<Country> s: map.values()){
            for(Country country:s){
                if(country.cheese.equalsIgnoreCase(cheese)){
                    return country;
                }
            }
        }
        return null;
    }

    public String populateSoapResponse(@Body String msg) {
        final String PREFIX = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" + "<soap:Body>\n";
        final String SUFFIX = "\n</soap:Body>\n" + "</soap:Envelope>";
        return PREFIX + msg + SUFFIX;
    }

}
