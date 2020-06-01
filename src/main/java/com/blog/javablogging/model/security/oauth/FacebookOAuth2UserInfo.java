package com.blog.javablogging.model.security.oauth;

import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {

    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String)attributes.get("id");
    }

    @Override
    public String getName() {
        return (String)attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        if (attributes.containsKey("picture")){
            Map<String , Object> pictureObject = (Map<String, Object>) attributes.get("picture");
            if (pictureObject.containsKey("data")){
                Map<String, Object> dataObj = (Map<String, Object>)pictureObject.get("data");
                if (dataObj.containsKey("url")){
                    return (String)dataObj.get("url");
                }
            }
        }
        return null;
    }
}
