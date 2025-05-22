package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

@Named("currentSignUpView")
@RequestScoped  // create this object for one HTTP request and destroy after the HTTP response has been sent
public class SignUpView {

    // Declare read/write properties (field + getter + setter) for each form field
    @NotBlank(message = "Username is required.")
    @Getter
    @Setter
    private String username;

    @NotBlank(message = "Password is required.")
    @Getter
    @Setter
    private String password;

    @NotBlank(message = "Confirmed Password is required.")
    @Getter
    @Setter
    private String confirmedPassword;

    public String onSignUp() {
        try {
            if (password.equals(confirmedPassword)) {
                return "/signIn.xhtml?faces-redirect=true";
            } else {
                Messages.addGlobalInfo("Passwords do not match.");
            }
        } catch (Exception ex) {
            handleException(ex);
        }
        return null;
    }

    /**
     * This method is used to handle exceptions and display root cause to user.
     *
     * @param ex The Exception to handle.
     */
    protected void handleException(Exception ex) {
        StringBuilder details = new StringBuilder();
        Throwable causes = ex;
        while (causes.getCause() != null) {
            details.append(ex.getMessage());
            details.append("    Caused by:");
            details.append(causes.getCause().getMessage());
            causes = causes.getCause();
        }
        Messages.create(ex.getMessage()).detail(details.toString()).error().add("errors");
    }

}