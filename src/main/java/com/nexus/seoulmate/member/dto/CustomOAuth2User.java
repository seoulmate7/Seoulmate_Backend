package com.nexus.seoulmate.member.dto;

import com.nexus.seoulmate.member.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final Role role;

    public CustomOAuth2User(OAuth2Response oAuth2Response, Role role){
        this.oAuth2Response = oAuth2Response;
        this.role = role;
    }

    public OAuth2Response getOAuth2Response() {
        return oAuth2Response;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
        // return oAuth2Response.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(() -> role.name()); // 람다로 간결하게 구현

        return collection;
    }

    @Override
    public String getName() {
        return oAuth2Response.getName();
    }
}
