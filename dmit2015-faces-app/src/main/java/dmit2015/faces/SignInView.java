package dmit2015.faces;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import java.io.Serializable;

@Named("currentSignInView")
@ViewScoped // create this object for one HTTP request and keep in memory if the next is for the same page
// class must implement Serializable
public class SignInView implements Serializable {

    // Declare read/write properties (field + getter + setter) for each form field
    @NotBlank(message = "Username is required.")
    @Getter
    @Setter
    private String username;

    @NotBlank(message = "Password is required.")
    @Getter
    @Setter
    private String password;

    public String onLogin() {
        try {
            if (username.equalsIgnoreCase("user2015") && password.equals("Password2015")) {
                return "/helloWorld?faces-redirect=true";
            } else {
                Messages.addGlobalInfo("Incorrect username or password");
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
        var details = new StringBuilder();
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