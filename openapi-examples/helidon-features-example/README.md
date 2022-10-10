[Back to openapi-ext](https://github.com/microprofile-extensions/openapi-ext/blob/main/README.md)

# Helidon Features Application

This is a basic helidon application to demonstrate several features of the OpenApi extensions with Helidon.

## Requirement

To build the example, you need JDK 11 and Maven 3.8.4.

## Presentation

Based on Helidon MP quickstart application, this example uses Openapi UI extension features. It exposes the greeting
application at `/myapp/greet`. The user interface is accessible at `/myopenapi/openapi-ui`. From 
`microprofile-config.properties`, the OpenApi document endpoint is modified by `openapi.web-context` property for Helidon
and `openapi.ui.yamlUrl` for OpenApi, so they point at each other. 

## Running the example

Using maven, you can start this application this way:

```
    mvn -Prun-example clean install
```

You can then go to http://localhost:8080/myopenapi/openapi-ui/ 

Press `ctrl+C` to stop the application.
