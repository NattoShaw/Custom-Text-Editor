import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Editor {

    Path file;
    List<String> text;
    Stage stage;

    public Editor(Stage stage) {
        this.stage = stage;
    }

    public Path getFile() {
        return file;
    }

    public List<String> getText() {
        return text;
    }


    public void save(Path file, List<String> text) {
        try {
            Files.write(file, text, StandardOpenOption.TRUNCATE_EXISTING);
            stage.setTitle(file.getFileName().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAs(Path file, List<String> text) {
        try {
            Files.write(file, text, StandardOpenOption.CREATE);

            this.file = file;
            this.text = text;

            stage.setTitle(file.getFileName().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open(Path file) {
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.ISO_8859_1);
            this.file = file;
            this.text = lines;

            stage.setTitle(file.getFileName().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
