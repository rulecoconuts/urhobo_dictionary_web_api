package com.fejiro.exploration.dictionary.dictionary_web_api.database;

import com.fejiro.exploration.dictionary.dictionary_web_api.security.AuthorizedUserHolder;
import com.fejiro.exploration.dictionary.dictionary_web_api.security.SimpleAuthorizedUserHolder;
import com.fejiro.exploration.dictionary.dictionary_web_api.service.user.AppUserDomainObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SimpleUserAuditablePopulator implements UserAuditablePopulator {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void populateForCreation(UserAuditable model) {
        var id = getId();
        model.setCreatedBy(id);
        model.setUpdatedBy(id);
    }

    AuthorizedUserHolder authorizedUserHolder() {
        try {
            return Optional.ofNullable(applicationContext.getBean(AuthorizedUserHolder.class))
                           .orElse(new SimpleAuthorizedUserHolder());
        } catch (NoSuchBeanDefinitionException exception) {
            return null;
        }
    }

    Integer getId() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                       .map(SecurityContext::getAuthentication)
                       .map(Authentication::getPrincipal)
                       .map(principal -> (AppUserDomainObject) principal)
                       .map(AppUserDomainObject::getId)
                       .orElse(null);

    }

    @Override
    public void populateForUpdate(UserAuditable model) {
        var id = getId();
        model.setUpdatedBy(id);
    }
}
