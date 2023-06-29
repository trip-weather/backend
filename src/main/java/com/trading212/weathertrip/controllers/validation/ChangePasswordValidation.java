package com.trading212.weathertrip.controllers.validation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ChangePasswordValidation {
    @NotBlank
    @Length(min = 8, message = "Supplied old password length is below 8 characters")
    private String oldPassword;

    @NotBlank
    @Length(min = 8, message = "Supplied new password length is below 8 characters")
    private String newPassword;
}
