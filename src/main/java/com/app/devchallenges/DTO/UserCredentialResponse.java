package com.app.devchallenges.DTO;

import com.app.devchallenges.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialResponse {
    private Long userId;
    private String username;
    private String email;
    private Role role;
}
