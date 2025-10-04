package com.example.tradingapp.service;

import com.example.tradingapp.config.AppConfig;
import com.example.tradingapp.model.AccessToken;
import com.example.tradingapp.repository.AccessTokenRepository;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class KiteConnectService {

    private final KiteConnect kiteConnect;
    private final AppConfig appConfig;
    private final AccessTokenRepository accessTokenRepository;

    public KiteConnectService(AppConfig appConfig, AccessTokenRepository accessTokenRepository) {
        this.appConfig = appConfig;
        this.accessTokenRepository = accessTokenRepository;
        this.kiteConnect = new KiteConnect(appConfig.getKey());
    }

    public String getLoginURL() {
        return kiteConnect.getLoginURL();
    }

    public void generateSession(String requestToken) throws KiteException, IOException {
        User user = kiteConnect.generateSession(requestToken, appConfig.getSecret());
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(user.accessToken);
        accessToken.setUserId(user.userId);
        accessToken.setCreatedAt(LocalDateTime.now());
        accessTokenRepository.save(accessToken);
        kiteConnect.setAccessToken(user.accessToken);
        kiteConnect.setPublicToken(user.publicToken);
    }
}