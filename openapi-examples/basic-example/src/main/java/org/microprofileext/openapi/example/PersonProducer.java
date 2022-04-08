package org.microprofileext.openapi.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * Creating some test data
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@ApplicationScoped
public class PersonProducer {
    
    @Produces
    public Map<Integer,Person> produceAllPeople(){
        Map<Integer,Person> m = new HashMap<>();
        
        JsonArray jsonPeople = getJsonData();
        
        for(JsonValue jsonPerson : jsonPeople){
            Person p = toPerson((JsonObject)jsonPerson);
            m.put(p.getId(), p);
        }
        
        return m;
    }
    
    private Person toPerson(JsonObject jsonObject){
        Person p = new Person();
        p.setId(jsonObject.getInt("id"));
        p.setFirstName(jsonObject.getString("first_name"));
        p.setLastName(jsonObject.getString("last_name"));
        p.setEmail(jsonObject.getString("email"));
        p.setGender(Gender.valueOf(jsonObject.getString("gender")));
        p.setIPAddress(jsonObject.getString("ip_address"));
        return p;
    }
    
    private JsonArray getJsonData(){
        try(InputStream is = readSampleData()){
            JsonReader reader = Json.createReader(is);
            return reader.readArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private InputStream readSampleData(){
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream("examples/data.json");
    }
    
}
