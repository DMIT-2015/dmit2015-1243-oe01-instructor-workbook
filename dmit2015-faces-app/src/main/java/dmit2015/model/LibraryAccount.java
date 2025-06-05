package dmit2015.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LibraryAccount {

    @NotBlank(message = "First name is required.")
    @Size(message = "First name must contain 2 or more characters.")
    private String firstName;

    @Size(message = "Last name must contain 2 or more characters.")
    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Email name is required.")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[0-9]).{8,}$", message = "Password must contain 8 or characters and 1 digit")
    private String password;

}
