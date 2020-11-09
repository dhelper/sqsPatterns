package receiverService;

import common.sqs.config.SqsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SqsConfig.class)
public class SpringConfiguration {

}
