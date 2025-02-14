package kr.intube.apps.mockapi.common.google.drive;

import aidt.gla.common.tools.biz.Checker;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Component
public class DriveHelper {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    @Value("${google.drive.app-name:AIDT-Mockup REST API}")
    private String applicationName;

    @Value("${google.drive.credentials.file-path:api-project-108305436670-ef69509e81f2.json}")
    private String credentialsFilePath;

    @Value("${google.drive.credentials.account:mockrowdata@api-project-108305436670.iam.gserviceaccount.com}")
    private String apiAccountMail;

    @Value("${google.drive.mime.spreadsheet:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet}")
    private String spreadsheetMime;

    @Value("${google.drive.page-size:10}")
    private int defaultPageSize;

    private static NetHttpTransport _httpTransport = null;

    // 구글 드라이브 API 인증 키 파일
    private InputStream getKeyFile() throws IOException {
        return ResourceUtils
            .getURL("classpath:" + credentialsFilePath)
            .openStream();
    }

    private GoogleCredential getCredentials() throws IOException {
        return GoogleCredential.fromStream(getKeyFile())
            .createScoped(SCOPES)
            .createDelegated(apiAccountMail);
    }

    private NetHttpTransport getNetHttpTransport() {
        if (_httpTransport == null) {
            try {
                _httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            } catch (GeneralSecurityException | IOException e) {
                return new NetHttpTransport();
            }
        }

        return _httpTransport;
    }

    public Drive getDrive() throws IOException {
        return new Drive.Builder(getNetHttpTransport(), JSON_FACTORY, getCredentials())
            .setApplicationName(applicationName)
            .build();
    }

    public FileList getFileList(int pageSize) throws IOException {
        return getDrive().files().list()
            .setPageSize(pageSize)
            .setSpaces("drive")
            .setFields("nextPageToken, files(id, name)")
            .execute();
    }

    public FileList getFileList() throws IOException {
        return getFileList(defaultPageSize);
    }

    public File getFile(String fileId) throws IOException {
        return getDrive().files().get(fileId).execute();
    }

    public String getFileId(String fileName) throws IOException {
        for (File f : getFileList(100).getFiles()) {
            if (Checker.eq(fileName, f.getName())) return f.getId();
        }

        return "";
    }

    public boolean existFileName(String fileName) {
        try {
            return Checker.isEmpty(getFileId(fileName));
        } catch (IOException e) {
            return false;
        }
    }

    public InputStream getFileStreamByName(String fileName) throws IOException {
        String fileId = getFileId(fileName);

        return Checker.isNull(fileId) ? null : getFileStreamById(getFileId(fileName));
    }

    public InputStream getFileStreamById(String fileId) throws IOException {
        return getDrive().files()
                .export(fileId, spreadsheetMime)
                .executeMediaAsInputStream();
    }
}
