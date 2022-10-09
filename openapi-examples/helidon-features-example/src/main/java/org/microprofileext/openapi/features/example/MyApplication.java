package org.microprofileext.openapi.features.example;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

@ApplicationScoped
@ApplicationPath("/myapp")
public class MyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(GreetResource.class);
    }

}
