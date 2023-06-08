package sudyar.blps.etc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sudyar.blps.entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserWithRole {
    private String login;
    private String password;
    private Role role;
}
