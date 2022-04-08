package org.microprofileext.openapi.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Person POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class Person {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String IPAddress;   
}
