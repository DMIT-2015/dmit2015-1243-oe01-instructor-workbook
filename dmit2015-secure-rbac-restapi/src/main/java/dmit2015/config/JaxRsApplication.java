package dmit2015.config;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 * This class enables Jakarta RESTful Web Services and identifies the application path that serves
 * as the base URI for all resource URIs provided by Path.
 */
@ApplicationPath("restapi")
@LoginConfig(authMethod = "MP-JWT",realmName = "dmit2015-realm")
@DeclareRoles({"Marketing","Sales","Administration"})
public class JaxRsApplication extends Application {

}