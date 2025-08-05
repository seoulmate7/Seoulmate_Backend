package com.nexus.seoulmate.member.dto;

import java.util.Map;

public class GoogleResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
    }

    @Override
    public String getProvider(){
        return "google";
    }

    @Override
    public String getProviderId(){
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail(){
        return attribute.get("email").toString();
    }

    @Override
    public String getName(){
        return attribute.get("name").toString();
    }

    @Override
    public String getGivenName(){
        return attribute.get("given_name").toString();
    }

    @Override
    public String getFamilyName(){
        Object familyName = attribute.get("family_name");
        return familyName != null ? familyName.toString() : "";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attribute;
    }
}
