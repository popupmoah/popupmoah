package com.sgyj.popupmoah.presentation.user.service;

import com.nimbusds.oauth2.sdk.GrantType;
import com.sgyj.popupmoah.infra.config.exceptions.UserException;
import com.sgyj.popupmoah.infra.config.properties.NaverProviderProperties;
import com.sgyj.popupmoah.infra.config.properties.NaverRegistrationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverTokenService implements TokenService {

    private final RestClient restClient = RestClient.create();
    private final NaverRegistrationProperties properties;
    private final NaverProviderProperties naverProviderProperties;

    @Override
    public String getAccessToken(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("grant_type", GrantType.AUTHORIZATION_CODE.toString());
        params.add("code", code);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(params, httpHeaders);
        ResponseEntity<Map> response = restClient.post().uri(naverProviderProperties.getTokenUri()).body(multiValueMapHttpEntity).retrieve().toEntity(Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("{} {}", response.getStatusCode().toString(), response.getBody());
            throw new UserException("네이버 사용자 토큰 가져오는데 실패하였습니다.");
        }

        assert response.getBody() != null;
        return response.getBody().get("access_token").toString();
    }

}
