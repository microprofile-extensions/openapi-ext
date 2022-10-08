package org.microprofileext.openapi.features.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.microprofileext.openapi.swaggerui.OpenApiUiService;
import org.microprofileext.openapi.swaggerui.StaticResourcesService;

import java.util.Set;

@ApplicationScoped
@ApplicationPath("/myopenapi")
public class UiApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(OpenApiUiService.class,
                StaticResourcesService.class);
    }

}
