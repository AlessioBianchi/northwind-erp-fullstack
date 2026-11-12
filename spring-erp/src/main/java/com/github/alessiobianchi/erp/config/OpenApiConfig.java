package com.github.alessiobianchi.erp.config;

import com.github.alessiobianchi.erp.dto.ErrorMessage;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BAD_REQUEST = "BadRequest";
    public static final String NOT_FOUND = "NotFound";
    public static final String CONFLICT = "Conflict";

    private static final String SESSION_SCHEME = "sessionCookie";
    private static final String AUTH_TAG = "Authentication";

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

    // Login/logout are handled by Spring Security filters, not controllers, so springdoc can't scan them.
    // Its built-in login endpoint documents a JSON body, but formLogin only reads form fields.
    @Bean
    public OpenApiCustomizer authEndpoints() {
        return openApi -> {
            Schema<?> credentials = new ObjectSchema()
                    .addProperty("username", new StringSchema())
                    .addProperty("password", new StringSchema().format("password"))
                    .required(List.of("username", "password"));
            Schema<?> loginResult = new ObjectSchema()
                    .addProperty("message", new StringSchema())
                    .addProperty("username", new StringSchema())
                    .addProperty("isManager", new BooleanSchema());

            Operation login = new Operation()
                    .addTagsItem(AUTH_TAG)
                    .summary("Log in")
                    .description("Starts a session: the response sets the `JSESSIONID` cookie used by every other endpoint. "
                            + "`isManager` is true for employees who report to no one.")
                    .security(List.of())
                    .requestBody(new RequestBody().required(true).content(new Content()
                            .addMediaType("application/x-www-form-urlencoded", new MediaType().schema(credentials))))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("Logged in")
                                    .content(new Content().addMediaType("application/json", new MediaType().schema(loginResult))))
                            .addApiResponse("401", new ApiResponse().description("Invalid username or password")));

            Operation logout = new Operation()
                    .addTagsItem(AUTH_TAG)
                    .summary("Log out")
                    .description("Invalidates the session and deletes the `JSESSIONID` cookie.")
                    .security(List.of())
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("Logged out")));

            openApi.path("/api/auth/login", new PathItem().post(login))
                    .path("/api/auth/logout", new PathItem().post(logout))
                    .addTagsItem(new Tag().name(AUTH_TAG).description("Session login and logout."));
        };
    }

    private static ApiResponse errorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json", new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ErrorMessage"))));
    }
}
