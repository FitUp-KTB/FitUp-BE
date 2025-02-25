package site.FitUp.main.exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ExceptionResponse {
    private final boolean isSuccess=false;
    private String code;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime responseAt = OffsetDateTime.now();
}
