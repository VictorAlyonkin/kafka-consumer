package org.petrarka.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Validator {

    /**
     * Валидация объекта по json схеме
     *
     * @param object               проверяемый объект
     * @param pathToFileJsonSchema путь до файла json схемы
     */
    public static <T> void validateJsonSchema(T object, String pathToFileJsonSchema) {
        log.debug("Запуск валидации по json схеме");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        try (InputStream schemaStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathToFileJsonSchema)) {
            JsonNode json = objectMapper.valueToTree(object);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            Set<ValidationMessage> validationResult = schema.validate(json);

            if (validationResult.isEmpty()) {
                log.debug("Валидации по json схеме не выявила нарушений");
                return;
            }

            String errorMessage = validationResult.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining("; "));

            log.error("Валидации по json схеме выявила следующие нарушения: {}", errorMessage);
            throw new ValidateException(errorMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
