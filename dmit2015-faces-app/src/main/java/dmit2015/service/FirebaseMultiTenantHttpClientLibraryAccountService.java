package dmit2015.service;

import dmit2015.faces.FirebaseAuthSignInSession;
import dmit2015.model.LibraryAccount;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * This class implements the StudentService using the HttpClient library
 * to send Http Request to the Firebase Realtime Database REST API.
 * <p>
 * Verify that you have set up the following Rules in your Firebase Realtime Database
 * where the multi-tenant data is in a root path name "multi_tenant_data".
 * <p>
 * {
 * "rules": {
 * "multi_tenant_data": {
 * "LibraryAccount": {
 * "$uid": {
 * // Allow only authenticated content owners access to their data
 * ".read": "auth !== null && auth.uid === $uid",
 * ".write": "auth !== null && auth.uid === $uid"
 * }
 * }
 * }
 * }
 * }
 */

@Named("firebaseMultiTenantHttpClientLibraryAccountService")
@ApplicationScoped
public class FirebaseMultiTenantHttpClientLibraryAccountService implements LibraryAccountService {
    /**
     * The base URL to the Firebase Realtime Database that is defined in
     * `src/main/resources/META-INF/microprofile-config.properties` file.
     */
    @Inject
    @ConfigProperty(name = "firebase.rtdb.LibraryAccount.base.url")
    private String firebaseRtdbBaseUrl;

    /**
     * The URL to the Firebase Realtime Database to access all data.
     */
    private String _jsonAllDataPath;

    /**
     * HttpClient is native Java library for sending Http Request to a web server
     */
    private HttpClient httpClient;

    /**
     * Jsonb is used for converting Java objects to a JSON string or visa-versa
     */
    private Jsonb jsonb;

    @Inject
    private FirebaseAuthSignInSession _firebaseAuthSignInSession;

    private static final String MULTI_TENANT_PATH_PREFIX = "multi_tenant_data";

    @PostConstruct
    private void init() {
        httpClient = HttpClient.newHttpClient();
        jsonb = JsonbBuilder.create();
    }

    /**
     * Puhsing currentLibraryAccount data to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    @Override
    public LibraryAccount createLibraryAccount(LibraryAccount libraryAccount) {

        // Get the Firebase Authenticated userId and token.
        String firebaseUserId = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        // Set the path in the database for content-owner access only data
        _jsonAllDataPath = String.format("%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                LibraryAccount.class.getSimpleName(),
                firebaseUserId,
                firebaseToken);

        // Convert the Java object to a JSON string using JSONB
        String requestBodyJson = jsonb.toJson(libraryAccount);

        // Create a Http Request for sending a Http POST request to push new data
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(_jsonAllDataPath))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                .build();
        try {
            // Send the Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Request response is successful
            if (httpResponse.statusCode() == 200) {
                // Get the body of the Http Response
                var responseBodyJson = httpResponse.body();
                // Convert the JSON String to a JsonObject
                JsonObject responseJsonObject = jsonb.fromJson(responseBodyJson, JsonObject.class);
                // Set the unique key name for this object
                libraryAccount.setId(responseJsonObject.getString("name"));

            } else {
                String errorMessage = String.format("Create was not successful with status code: %s", httpResponse.statusCode());
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return libraryAccount;
    }

    @Override
    public Optional<LibraryAccount> getLibraryAccountById(String id) {
        // Build the url path to object to access
        String firebaseUserId = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        String _jsonSingleDataPath = String.format("%s/%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                LibraryAccount.class.getSimpleName(),
                firebaseUserId,
                id,
                firebaseToken);

        try {
            // Create an GET Http Request to fetch all data
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(_jsonSingleDataPath))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            // Send the GET Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Request was successful
            if (httpResponse.statusCode() == 200) {
                // Get the body of the Http Response
                var responseBodyJson = httpResponse.body();
                if (!responseBodyJson.equals("null")) {
                    // Convert the responseBodyJson to an LinkedHashMap<String, LibraryAccount>
                    LibraryAccount responseData = jsonb.fromJson(responseBodyJson, LibraryAccount.class);
                    responseData.setId(id);
                    return Optional.of(responseData);
                } else {
                    throw new RuntimeException(String.format("LibraryAccount with id of %s not found", id));
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<LibraryAccount> getAllLibraryAccounts() {
        // Get the Firebase Authenticated userId and token.
        String firebaseUserId = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        // Set the path in the database for content-owner access only data
        _jsonAllDataPath = String.format("%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                LibraryAccount.class.getSimpleName(),
                firebaseUserId,
                firebaseToken);

        // Create an GET Http Request to fetch all data
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(_jsonAllDataPath))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            // Send the GET Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Request was successful
            if (httpResponse.statusCode() == 200) {
                // Get the body of the Http Response
                var responseBodyJson = httpResponse.body();
                // Convert the responseBodyJson to an LinkedHashMap<String, LibraryAccount>
                LinkedHashMap<String, LibraryAccount> responseData = jsonb.fromJson(responseBodyJson, new LinkedHashMap<String, LibraryAccount>() {
                }.getClass().getGenericSuperclass());
                if (responseData != null) {
                    // Convert the LinkedHashMap<String, LibraryAccount> to List<LibraryAccount>
                    return responseData.entrySet()
                            .stream()
                            .map(item -> {
                                var currentLibraryAccount = new LibraryAccount();
                                currentLibraryAccount.setId(item.getKey());

                                currentLibraryAccount.setFirstName(item.getValue().getFirstName());
                                 currentLibraryAccount.setLastName(item.getValue().getLastName());
                                 currentLibraryAccount.setEmail(item.getValue().getEmail());
                                 currentLibraryAccount.setPassword(item.getValue().getPassword());
                                 // Convert birthDate from a String value to a LocalDate value
currentLibraryAccount.setDateOfBirth(LocalDate.parse(item.getValue().getDateOfBirth().toString()));

                                return currentLibraryAccount;
                            })
                            .toList();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return List.of();
    }

    /**
     * Writing currentLibraryAccount data to Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    @Override
    public LibraryAccount updateLibraryAccount(LibraryAccount libraryAccount) {
        // Build the url path to object to access
        String firebaseUserId = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        String _jsonSingleDataPath = String.format("%s/%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                LibraryAccount.class.getSimpleName(),
                firebaseUserId,
                libraryAccount.getId(),
                firebaseToken);

        // Convert the Java object to a JSON string using JSONB
        String requestBodyJson = jsonb.toJson(libraryAccount);

        // Create and Http Request to send an HTTP PUT request to write over existing data
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(_jsonSingleDataPath))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                .build();
        try {
            // Send the Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Response was successful
            if (httpResponse.statusCode() != 200) {
                String errorMessage = String.format("Update was not successful with status code: %s", httpResponse.statusCode());
                throw new RuntimeException(errorMessage);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return libraryAccount;
    }

    /**
     * Remove data from Firebase Realtime Database using the REST API
     *
     * @link <a href="https://firebase.google.com/docs/reference/rest/database">Firebase Realtime Database REST API</a>
     */
    @Override
    public void deleteLibraryAccountById(String id) {
        // Build the url path to object to access
        String firebaseUserId = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getLocalId();
        String firebaseToken = _firebaseAuthSignInSession.getFirebaseAuthSignInResponsePayload().getIdToken();
        String _jsonSingleDataPath = String.format("%s/%s/%s/%s/%s.json?auth=%s",
                firebaseRtdbBaseUrl,
                MULTI_TENANT_PATH_PREFIX,
                LibraryAccount.class.getSimpleName(),
                firebaseUserId,
                id,
                firebaseToken);

        // Create an DELETE Http Request
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(_jsonSingleDataPath))
                .DELETE()
                .build();
        try {
            // Send the DELETE Http Request
            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // Check if the Http Response was successful
            if (httpResponse.statusCode() != 200) {
                String errorMessage = String.format("Delete was not successful with status code: %s", httpResponse.statusCode());
                throw new RuntimeException(errorMessage);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}