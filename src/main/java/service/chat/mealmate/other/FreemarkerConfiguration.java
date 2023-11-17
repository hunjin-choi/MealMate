package service.chat.mealmate.other;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;

/**
 * Overrides the default spring-boot configuration to allow adding shared variables to the freemarker context
 */
@Configuration
public class FreemarkerConfiguration{//} extends FreeMarkerConfigurer {

//    @Value("${spring.freemarker.domain}")
//    private String domain;
//
//    public FreemarkerConfiguration() {
//        super();
//    }
//
//    @Override
//    public void setFreemarkerVariables(Map<String, Object> variables) {
//        variables.put("domain", domain);
//        super.setFreemarkerVariables(variables);
//    }
}