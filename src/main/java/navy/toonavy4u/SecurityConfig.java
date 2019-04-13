package navy.toonavy4u;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/fonts/**", "/images/**", "/js/**", "/ViewComic", "/ViewSeries", "/Toolbar.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(authorizationRequestRepository())
                .and()
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient())
                .and()
                    .defaultSuccessUrl("/", true)
                .and()
                    .logout().logoutSuccessUrl("/").permitAll()
                .and()
                    .csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository());
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        return accessTokenResponseClient;
    }
}