package fplhn.udpm.examdistribution.infrastructure.config.drive.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
@Slf4j
public class GoogleDriveConfig {

    @Value("${google.drive.email}")
    private String SERVICE_ACCOUNT_EMAIL;

    @Value("${google.drive.key}")
    private String SERVICE_ACCOUNT_KEY;

    @Bean
    public Drive getDrive() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Drive.Builder(
                    HTTP_TRANSPORT,
                    JacksonFactory.getDefaultInstance(),
                    this.googleCredential()
            ).setApplicationName("Exam Distribution").build();
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
