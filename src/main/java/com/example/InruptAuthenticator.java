package com.example;

import com.inrupt.client.accessgrant.AccessGrant;
import com.inrupt.client.accessgrant.AccessGrantSession;
import com.inrupt.client.auth.Session;
import com.inrupt.client.openid.OpenIdSession;

import java.net.URI;

public class InruptAuthenticator {
    private final String issuerUrl;
    private final String clientId;
    private final String clientSecret;
    private final String authMethod;

    public InruptAuthenticator(String clientId, String clientSecret) {
        this("https://login.inrupt.com/", clientId, clientSecret);
    }

    public InruptAuthenticator(String issuerUrl, String clientId, String clientSecret) {
        this.issuerUrl = issuerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authMethod = "client_secret_basic";
    }

    public Session getSession() {
        return OpenIdSession.ofClientCredentials(URI.create(issuerUrl), clientId, clientSecret, authMethod);
    }

    public Session getAccessGrantSession(String grant) {
        var accessGrant = AccessGrant.ofAccessGrant(grant);
        return AccessGrantSession.ofAccessGrant(getSession(), accessGrant);
    }

}
