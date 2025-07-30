package custom.spring.saml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;

@SpringBootApplication(
    exclude = {
        org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration.class
    }
)

public class CustomSpringSamlMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomSpringSamlMainApplication.class, args);
    }
}
