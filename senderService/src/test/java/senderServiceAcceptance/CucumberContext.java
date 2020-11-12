package senderServiceAcceptance;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import io.cucumber.java.DefaultParameterTransformer;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import senderService.SenderServiceApplication;

import java.lang.reflect.Type;

@CucumberContextConfiguration()
@TestPropertySource(locations = "classpath:test.properties")
@ContextConfiguration(classes = {ScenarioContext.class, TestConfiguration.class})
@SpringBootTest(classes = SenderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CucumberContext {
    @DefaultParameterTransformer
    @DefaultDataTableEntryTransformer
    @DefaultDataTableCellTransformer
    public Object defaultTransformer(Object fromValue, Type toValueType) {
        var objectMapper = new ObjectMapper();

        JavaType javaType = objectMapper.constructType(toValueType);
        return objectMapper
                .convertValue(fromValue, javaType);
    }
}
