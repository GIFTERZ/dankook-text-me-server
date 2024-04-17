package gifterz.textme.domain.oauth.config;

import gifterz.textme.domain.oauth.infra.dku.controller.DkuApi;
import gifterz.textme.domain.oauth.infra.kakao.controller.KakaoApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {

    @Value("${oauth.dku.api.base-url}")
    private String dkuBaseUrl;

    @Bean
    KakaoApi kakaoApi() {
        WebClient client = WebClient.create();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(client))
                .build();

        return httpServiceProxyFactory.createClient(KakaoApi.class);
    }

    @Bean
    DkuApi dkuApi() {
        WebClient client = WebClient.builder()
                .baseUrl(dkuBaseUrl).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(client))
                .build();

        return httpServiceProxyFactory.createClient(DkuApi.class);
    }
}
