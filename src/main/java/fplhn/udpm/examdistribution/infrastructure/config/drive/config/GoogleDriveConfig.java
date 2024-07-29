package fplhn.udpm.examdistribution.infrastructure.config.drive.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class GoogleDriveConfig {

    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    @Value("${google.drive.credentials.file.path}")
    private String CREDENTIALS_FILE_PATH;

    @Value("${google.drive.tokens.directory.path}")
    private String TOKENS_DIRECTORY_PATH;

    @Value("${google.drive.host}")
    private String GOOGLE_HOST;

    @Value("${google.drive.port}")
    private Integer GOOGLE_PORT;

    @Value("${google.drive.email}")
    private String SERVICE_ACCOUNT_EMAIL;

    @Value("${google.drive.key}")
    private String SERVICE_ACCOUNT_KEY;

//    @Bean
//    public Drive getDrive() {
//        try {
//            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                    .setApplicationName("Exam Distribution")
//                    .build();
//        } catch (GeneralSecurityException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        try (InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH)) {
//            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
//                    .setAccessType("offline")
//                    .build();
//
//            if (Objects.equals(GOOGLE_HOST, "localhost")) {
//                LocalServerReceiver receiver = new LocalServerReceiver
//                        .Builder()
//                        .setHost(GOOGLE_HOST)
//                        .setPort(GOOGLE_PORT)
//                        .build();
//                return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//            } else {
//                // USE FOR PRODUCTION
//                LocalServerReceiver receiver = new LocalServerReceiver
//                        .Builder()
//                        .setHost(GOOGLE_HOST)
//                        .build();
//                return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//            }
//        } catch (Exception e) {
//            log.error("Error loading credentials", e);
//            throw new RuntimeException(e);
//        }
//    }

    @Bean
    public Drive getDrive() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Drive.Builder(
                    HTTP_TRANSPORT,
                    JacksonFactory.getDefaultInstance(),
                    this.googleCredential()
            ).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private GoogleCredential googleCredential() throws GeneralSecurityException, IOException {
        Collection<String> elenco = new ArrayList<>();
        elenco.add("https://www.googleapis.com/auth/drive");
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        return new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountScopes(elenco)
                .setServiceAccountPrivateKeyFromP12File(new File(SERVICE_ACCOUNT_KEY))
                .build();
    }

}
