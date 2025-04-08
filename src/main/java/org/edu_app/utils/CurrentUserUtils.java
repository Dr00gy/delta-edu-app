package org.edu_app.utils;

import lombok.AllArgsConstructor;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.entity.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CurrentUserUtils {

    private InitDBManager dbManager;

    public UserDTO get() {
        String authenticatedEmail = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // Fetch user details via initial DB manager
        return dbManager.getUserDetails(authenticatedEmail);
    }
}
