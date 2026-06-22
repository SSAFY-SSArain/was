package org.ssafy.ssarain.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateDto(
		@Schema(requiredMode = RequiredMode.REQUIRED, example = "pwd")
		@NotBlank
		String oldPassword, 
		
		@Schema(requiredMode = RequiredMode.REQUIRED, example = "pwd")
		@NotBlank
		String newPassword) {

}
