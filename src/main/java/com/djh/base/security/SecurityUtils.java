package com.djh.base.security;

import com.djh.base.security.custom.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {
    public static Optional<UserPrincipal> requester() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication && null != authentication.getDetails()) {
            Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return object instanceof UserPrincipal ? Optional.of((UserPrincipal)object) : Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    private SecurityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
