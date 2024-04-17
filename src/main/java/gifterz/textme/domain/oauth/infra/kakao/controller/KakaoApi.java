package gifterz.textme.domain.oauth.infra.kakao.controller;

import gifterz.textme.domain.oauth.infra.kakao.entity.KakaoToken;
import gifterz.textme.domain.oauth.infra.kakao.dto.KakaoMemberResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;


import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public interface KakaoApi {

    @PostExchange(url = "${oauth.kakao.api.fetch_token}", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoToken fetchToken(@RequestBody MultiValueMap<String, String> params);

    @GetExchange(url = "${oauth.kakao.api.fetch_user}")
    KakaoMemberResponse fetchMemberInfo(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestHeader(name = "Content-type") String contentType);
}
