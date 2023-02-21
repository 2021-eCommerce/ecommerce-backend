package store.juin.api.service.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.juin.api.domain.request.EmailRequest;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final String fromEmail = "jduck1024@naver.com";

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    public String send(EmailRequest request) {
        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(request.getToEmail()))
                .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(request.getContent())))
                        .withSubject(new Content().withCharset("UTF-8").withData(request.getTitle())))
                .withSource(fromEmail);

        final SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(sendEmailRequest);
        return sendEmailResult.getSdkResponseMetadata().getRequestId();
    }

}
