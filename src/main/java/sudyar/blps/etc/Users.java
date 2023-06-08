package sudyar.blps.etc;

import lombok.Data;
import sudyar.blps.entity.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class Users {
    private List<User> user;

    public Users(){
        user = new ArrayList<>();
    }
}
