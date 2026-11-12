package com.github.alessiobianchi.erp.config;

import com.github.alessiobianchi.erp.dto.ErrorMessage;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
public class OpenApiConfig {

    public static final String BAD_REQUEST = "BadRequest";
    public static final String NOT_FOUND = "NotFound";
    public static final String CONFLICT = "Conflict";

    private static final String SESSION_SCHEME = "sessionCookie";

    @Bean
    public OpenAPI erpOpenApi() {
        Components components = new Components()
                .addSecuritySchemes(SESSION_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("JSESSIONID"))
                .addResponses(BAD_REQUEST, errorResponse("Invalid request body or parameters"))
                .addResponses(NOT_FOUND, errorResponse("Resource not found"))
                .addResponses(CONFLICT, errorResponse("Resource is still referenced by other records"));
        ModelConverters.getInstance().read(ErrorMessage.class).forEach(components::addSchemas);

        return new OpenAPI()
                .info(new Info()
                        .title("Northwind ERP API")
                        .version("v1")
                        .description("REST API of the Northwind ERP, consumed by the Angular frontend.\n\n" +
                                "**Authentication:** session based. Log in with `POST /api/auth/login` (form fields `username`, `password`); " +
                                "the `JSESSIONID` cookie is sent automatically afterwards. Write requests also need the `X-XSRF-TOKEN` header " +
                                "(value of the `XSRF-TOKEN` cookie); Swagger UI adds it for you.\n\n" +
                                "**Conventions:**\n\n" +
                                "- On update and delete, the ID comes from the path; an ID in the body is ignored\n" +
                                "- Lists are sorted newest first (ID descending)\n" +
                                "- Errors return `{ status, message, timestamp }`\n" +
                                "- Every endpoint except login returns 401 when not logged in"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SESSION_SCHEME));
    }

    // A GET with params = {...} shares its path with the plain list GET; OpenAPI allows one operation
    // per path + method, so springdoc merges them and the params must not look mandatory.
    @Bean
    public OperationCustomizer optionalParamsOnMergedGets() {
        return (operation, handlerMethod) -> {
            GetMapping get = handlerMethod.getMethodAnnotation(GetMapping.class);
            if (get != null && get.params().length > 0 && operation.getParameters() != null) {
                operation.getParameters().forEach(parameter -> parameter.setRequired(false));
            }
            return operation;
        };
    }

    private static ApiResponse errorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json", new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ErrorMessage"))));
    }
}
