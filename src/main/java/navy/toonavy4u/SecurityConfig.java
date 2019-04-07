package navy.toonavy4u;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/fonts/**", "/images/**", "/js/**", "/View.html", "/Front.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login();
    }
}