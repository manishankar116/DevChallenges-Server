package com.app.devchallenges.Security;

import com.app.devchallenges.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionCredentialRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}
