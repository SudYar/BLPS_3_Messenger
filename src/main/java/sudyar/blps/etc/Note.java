package sudyar.blps.etc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    private String to;
    private String from;
    private String message;
}
