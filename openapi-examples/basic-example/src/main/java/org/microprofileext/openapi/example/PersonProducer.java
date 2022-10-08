package org.microprofileext.openapi.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Creating some test data
 *
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */

@ApplicationScoped
public class PersonProducer {

  @Produces
  public Map<Integer, Person> produceAllPeople() {
    return getJsonData().stream()
                        .map(e -> toPerson((JsonObject) e))
                        .collect(Collectors.toMap(Person::getId, p -> p));
  }

  private Person toPerson(JsonObject jsonObject) {
    Person p = new Person();
    p.setId(jsonObject.getInt("id"));
    p.setFirstName(jsonObject.getString("first_name"));
    p.setLastName(jsonObject.getString("last_name"));
    p.setEmail(jsonObject.getString("email"));
    p.setGender(Gender.valueOf(jsonObject.getString("gender")));
    p.setIPAddress(jsonObject.getString("ip_address"));
    return p;
  }

  private JsonArray getJsonData() {
    try (InputStream is = readSampleData()) {
      JsonReader reader = Json.createReader(is);
      return reader.readArray();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private InputStream readSampleData() {
    ClassLoader classLoader = getClass().getClassLoader();
    return classLoader.getResourceAsStream("examples/data.json");
  }
}
