package store.juin.api.domain.request;

import lombok.Data;

@Data
public class EmailRequest {
    private String toEmail;
    private String title;
    private String content;
}