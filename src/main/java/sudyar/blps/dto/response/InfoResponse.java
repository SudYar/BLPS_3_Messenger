package sudyar.blps.dto.response;

public class InfoResponse {

    private final String info;
    private final Integer code;

    public InfoResponse(String info, Integer code) {
        this.info = info;
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public Integer getCode() {
        return code;
    }
}
