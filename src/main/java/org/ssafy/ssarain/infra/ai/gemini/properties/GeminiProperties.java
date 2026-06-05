package org.ssafy.ssarain.infra.ai.gemini.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.gemini")
@Getter
@Setter
public class GeminiProperties {

    @NotBlank
    private String apiKey;

    private String model = "gemini-3.5-flash";
}
