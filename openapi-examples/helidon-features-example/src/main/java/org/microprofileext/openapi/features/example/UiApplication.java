package org.microprofileext.openapi.features.example;

import org.microprofileext.openapi.swaggerui.OpenApiUiService;
import org.microprofileext.openapi.swaggerui.StaticResourcesService;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
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
