package navy.toonavy4u;

import entities.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import repositories.ReaderRepository;

import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ReaderRepository readerRepository;

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());

        String userInfoEndpointUri = client
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());

            HttpEntity<String> entity = new HttpEntity<>("", headers);
            ResponseEntity<Map> response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();

            if (userAttributes != null && userAttributes.get("email") != null) {
                String email = userAttributes.get("email").toString();
                List<Reader> readers = readerRepository.findByIdtoken(email);
                Reader reader;

                if (readers.isEmpty()) {                // if reader is not in database
                    reader = new Reader();
                    reader.setIdtoken(email);
                    reader.setPublished(0);

                    readerRepository.save(reader);
                } else {
                    reader = readers.get(0);
                }

                model.addAttribute("reader", reader);
            }
        }

        model.addAttribute("post", new Post());
        return "index";
    }
}
