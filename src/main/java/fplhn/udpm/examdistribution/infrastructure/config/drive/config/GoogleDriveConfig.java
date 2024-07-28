package fplhn.udpm.examdistribution.infrastructure.config.drive.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
public class GoogleDriveConfig {

    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    @Value("${google.credentials.file.path}")
    private String CREDENTIALS_FILE_PATH;

    @Value("${google.tokens.directory.path}")
    private String TOKENS_DIRECTORY_PATH;

    @Value("${google.host}")
    private String GOOGLE_HOST;

    @Value("${google.port}")
    private Integer GOOGLE_PORT;

    @Bean
    public Drive getDrive() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName("Exam Distribution")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        try (InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH)) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            if (Objects.equals(GOOGLE_HOST, "localhost")) {
                LocalServerReceiver receiver = new LocalServerReceiver
                        .Builder()
                        .setHost(GOOGLE_HOST)
                        .setPort(GOOGLE_PORT)
                        .build();
                return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            } else {
                // USE FOR PRODUCTION
                LocalServerReceiver receiver = new LocalServerReceiver
                        .Builder()
                        .setHost(GOOGLE_HOST)
                        .build();
                return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            }
        } catch (Exception e) {
            log.error("Error loading credentials", e);
            throw new RuntimeException(e);
        }
    }

}
