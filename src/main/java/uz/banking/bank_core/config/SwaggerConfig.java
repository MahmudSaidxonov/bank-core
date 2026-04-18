package uz.banking.bank_core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Java Bank Core API")
                        .version("1.0")
                        .description("A RESTful API designed to handle core banking operations, including user authentication, account management, and secure fund transfers between accounts."));
    }
}
