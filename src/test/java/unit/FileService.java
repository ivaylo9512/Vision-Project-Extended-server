package unit;

import com.vision.project.exceptions.FileFormatException;
import com.vision.project.models.File;
import com.vision.project.models.UserModel;
import com.vision.project.repositories.base.FileRepository;
import com.vision.project.services.FileServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileService {
    @Mock
    private FileRepository fileRepository;

    @Spy
    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeAll
    private static void setup() throws IOException {
        new java.io.File("./uploads/test.txt").createNewFile();
        new java.io.File("./uploads/test1.txt").createNewFile();
        new java.io.File("./uploads/test3.txt").createNewFile();
    }

    @AfterAll
    private static void reset() throws IOException {
        new java.io.File("./uploads/test.txt").delete();
        new java.io.File("./uploads/test1.txt").delete();
        new java.io.File("./uploads/test2.txt").delete();
        new java.io.File("./uploads/test3.txt").delete();
    }

    @Test
    public void generate() {
        MockMultipartFile file = new MockMultipartFile(
                "image132",
                "image132.png",
                "image/png",
                "image132".getBytes());

        File savedFile = fileService.generate(file, "savedName", "image");

        assertEquals(savedFile.getName(), "savedName.png");
        assertEquals(savedFile.getType(), "image/png");
    }

    @Test
    public void create_WhenTypeDoesNotMatch_FileFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "text132",
                "text132.txt",
                "text/plain",
                "text132".getBytes());

        FileFormatException thrown = assertThrows(FileFormatException.class,
                () -> fileService.create(file, "savedName", "image", new UserModel()));

        assertEquals(thrown.getMessage(), "File should be of type image");
    }

    @Test
    public void create() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "image132",
                "image132.png",
                "image/png",
                "image132".getBytes());

        File generatedFile = new File("savedName.png", 22.0, "image/png");

        doNothing().when(fileService).save(generatedFile, file);
        when(fileService.generate(file, "savedName.png", "image")).thenReturn(generatedFile);
        when(fileRepository.save(generatedFile)).thenReturn(generatedFile);

        File savedFile = fileService.create(file, "savedName.png", "image", new UserModel());

        assertEquals(savedFile.getName(), "savedName.png");
        assertEquals(savedFile.getType(), "image/png");
    }

    @Test
    public void createAndSave() throws Exception{
        FileInputStream input = new FileInputStream("./uploads/test.txt");
        MultipartFile multipartFile = new MockMultipartFile("test", "test.txt", "text/plain",
                IOUtils.toByteArray(input));

        fileService.create(multipartFile, "test2", "text", new UserModel());

        assertTrue(new java.io.File("./uploads/test2.txt").exists());
    }

    @Test
    public void find(){
        Resource resource = fileService.getAsResource("test.txt");

        assertEquals(resource.getFilename(), "test.txt");
    }
}
