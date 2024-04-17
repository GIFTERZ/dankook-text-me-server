package gifterz.textme.domain.oauth.infra.dku.controller;

import gifterz.textme.domain.oauth.infra.dku.dto.DkuMemberResponse;
import gifterz.textme.domain.oauth.infra.dku.entity.DkuTokenResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface DkuApi {
    @PostExchange(url = "${oauth.dku.api.fetch_token}")
    DkuTokenResponse fetchToken(@RequestParam MultiValueMap<String, String> params);

    @GetExchange(url = "${oauth.dku.api.fetch_user}")
    DkuMemberResponse fetchMemberInfo(@RequestHeader(name = "Authorization") String authorization,
                                      @RequestParam String scope);
}
