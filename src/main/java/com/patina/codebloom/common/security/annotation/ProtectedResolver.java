package com.patina.codebloom.common.security.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ProtectedResolver implements HandlerMethodArgumentResolver {

    private final Protector protector;

    public ProtectedResolver(final Protector protector) {
        this.protector = protector;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Protected.class)
                        && parameter.getParameterType().equals(AuthenticationObject.class);
    }

    @Override
    public Object resolveArgument(
                    final MethodParameter parameter,
                    final ModelAndViewContainer mavContainer,
                    final NativeWebRequest webRequest,
                    final WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return protector.validateSession(request);
    }
}
