package com.patina.codebloom.common.security.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ProtectedAspect {
    private final Protector protector;
    private final HttpServletRequest request;

    public ProtectedAspect(
                    final Protector protector,
                    final HttpServletRequest request) {
        this.protector = protector;
        this.request = request;
    }

    @Around("@annotation(protectedAnno)")
    public Object handleProtected(final ProceedingJoinPoint joinPoint, final Protected protectedAnno) throws Throwable {
        AuthenticationObject auth = protectedAnno.admin()
                        ? protector.validateAdminSession(request)
                        : protector.validateSession(request);

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof AuthenticationObject) {
                args[i] = auth;
            }
        }

        try {
            return joinPoint.proceed(args);
        } catch (ResponseStatusException ex) {
            throw ex; // propagate intentional auth errors
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", ex);
        }
    }
}
