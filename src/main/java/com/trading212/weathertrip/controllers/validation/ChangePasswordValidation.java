package com.trading212.weathertrip.controllers.validation;

import com.trading212.weathertrip.domain.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ChangePasswordValidation {
    @NotBlank
    @Length(min = Constants.PASSWORD_MIN_LENGTH,
            max = Constants.PASSWORD_MAX_LENGTH,
            message = "Supplied old password length is below 5 characters")
    private String oldPassword;

    @NotBlank
    @Length(min = Constants.PASSWORD_MIN_LENGTH,
            max = Constants.PASSWORD_MAX_LENGTH,
            message = "Supplied new password length is below 5 characters")
    private String newPassword;
}
