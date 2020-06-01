package com.blog.javablogging.model.security.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getAuth2UserInfo(String registrationId, Map<String,Object> attributes){
        if (registrationId.equalsIgnoreCase(AuthProvider.google.toString())){
            return new GoogleOAuth2UserInfo(attributes);
        }
        else if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())){
            return new FacebookOAuth2UserInfo(attributes);
        }
        else {
            throw new OAuth2AuthenticationException(new OAuth2Error(registrationId + " is not supported"));
        }
    }
}
