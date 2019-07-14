import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Core extends Application {
    public int width = 1200;
    public int height = 500;

    Scene rootScene;

    BorderPane rootPane;
    MenuBar menuBar;
    TextArea textArea;

    Clipboard systemClipboard;

    Stage window;

    int pos;
    ArrayList<Dialog> currentDialogs;
    Dialog<Pair<String, String>> replaceDialog;
    Dialog<String> findDialog;

    public Editor currentEditor;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        systemClipboard = Clipboard.getSystemClipboard();

        currentDialogs = new ArrayList<>();
        replaceDialog = new Dialog<>();
        findDialog = new Dialog<>();

        rootPane = new BorderPane();

        menuBar = new MenuBar();

        menuBar.setPrefWidth(width);

        Menu menu1 = new Menu("File");

        MenuItem newOption = new MenuItem("New \t\tCtrl+N");

        newOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newMethod();
            }
        });

        MenuItem open = new MenuItem("Open... \t\tCtrl+O");

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                open();
            }
        });

        MenuItem save = new MenuItem("Save \t\tCtrl+S");

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                save();
            }
        });

        MenuItem saveAs = new MenuItem("Save As...");

        saveAs.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Document (*.txt)", "*.txt");
                FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

                fileChooser.getExtensionFilters().addAll(txtFilter, allFilter);

                File file = fileChooser.showSaveDialog(primaryStage);

                if (file != null) {
                    currentEditor = new Editor(primaryStage);
                    currentEditor.saveAs(file.toPath(), Arrays.asList(textArea.getText().split("\n")));
                }
            }
        });

        MenuItem close = new MenuItem("Close");

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        menu1.getItems().addAll(newOption, open, save, saveAs, close);

        Menu menu2 = new Menu("Help");

        MenuItem about = new MenuItem("About");

        about.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
                aboutAlert.setHeaderText(null);
                aboutAlert.setTitle("About");
                aboutAlert.setContentText("This version 1.0 of Custom Editor");
                aboutAlert.show();
            }
        });

        menu2.getItems().add(about);

        Menu menu3 = new Menu("Edit");

        MenuItem undo = new MenuItem("Undo \t\tCtrl+Z");

        undo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.undo();
            }
        });

        MenuItem redo = new MenuItem("Redo \t\tCtrl+Y");

        redo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.redo();
            }
        });

        MenuItem cut = new MenuItem("Cut \t\t\tCtrl+X");

        cut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.cut();
            }
        });

        MenuItem copy = new MenuItem("Copy \t\tCtrl+C");

        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.copy();
            }
        });

        MenuItem paste = new MenuItem("Paste \t\tCtrl+V");

        paste.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.paste();
            }
        });

        MenuItem delete = new MenuItem("Delete \t\tDel");

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.replaceSelection("");
            }
        });

        MenuItem find = new MenuItem("Find... \t\tCtrl+F");

        find.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                findWord();
            }
        });

        MenuItem replace = new MenuItem("Replace... \t\tCtrl+R");

        replace.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                replace();
            }
        });

        MenuItem selectAll = new MenuItem("Select All \t\tCtrl+A");

        selectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.selectAll();
            }
        });

        menu3.getItems().addAll(undo, redo, cut, copy, paste, delete, new SeparatorMenuItem(), find, replace, new SeparatorMenuItem(), selectAll);

        menu3.setOnShowing(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                paste.setDisable(!systemClipboard.hasString());

                undo.setDisable(!textArea.isUndoable());

                redo.setDisable(!textArea.isRedoable());


                if (textArea.getSelectedText().equals("")) {
                    cut.setDisable(true);
                    copy.setDisable(true);
                    delete.setDisable(true);
                } else {
                    cut.setDisable(false);
                    copy.setDisable(false);
                    delete.setDisable(false);
                }
            }
        });

        menuBar.getMenus().addAll(menu1, menu3, menu2);

        textArea = new TextArea();

        textArea.setFont(new Font(25));

        rootPane.setTop(menuBar);
        rootPane.setCenter(textArea);

        rootScene = new Scene(rootPane, width, height);

        KeyCombination findCombo = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        KeyCombination replaceCombo = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        KeyCombination newCombo = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        KeyCombination openCombo = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCombination saveCombo = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

        rootScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (findCombo.match(event)) {
                    findWord();
                } else if (replaceCombo.match(event)) {
                    replace();
                } else if (newCombo.match(event)) {
                    newMethod();
                } else if (openCombo.match(event)) {
                    open();
                } else if (saveCombo.match(event)) {
                    save();
                }

            }
        });

        primaryStage.setTitle("Custom Editor");

        primaryStage.setScene(rootScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void newMethod() {
        textArea.clear();
        currentEditor = null;
        window.setTitle("Custom Editor");
    }

    public void open() {
        FileChooser fc = new FileChooser();

        File file = fc.showOpenDialog(window);

        if (file != null) {
            currentEditor = new Editor(window);

            currentEditor.open(file.toPath());

            if (!currentEditor.getText().isEmpty()) {
                textArea.clear();
                currentEditor.getText().forEach(line -> textArea.appendText(line + "\n"));
            }
        }
    }

    public void save() {
        if (currentEditor == null) {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Document (*.txt)", "*.txt");
            FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");

            fileChooser.getExtensionFilters().addAll(txtFilter, allFilter);

            File file = fileChooser.showSaveDialog(window);

            if (file != null) {
                currentEditor = new Editor(window);
                currentEditor.saveAs(file.toPath(), Arrays.asList(textArea.getText().split("\n")));
            }
        } else {
            currentEditor.save(currentEditor.getFile(), Arrays.asList(textArea.getText().split("\n")));
        }
    }

    public void findWord() {
        if (!currentDialogs.contains(findDialog)) {
            currentDialogs.add(findDialog);

            if (findDialog.getDialogPane().getButtonTypes().isEmpty()) {
                findDialog.setTitle("Find");
                findDialog.setHeaderText(null);

                ButtonType findNext = new ButtonType("Find Next", ButtonBar.ButtonData.OK_DONE);
                findDialog.getDialogPane().getButtonTypes().addAll(findNext, ButtonType.CANCEL);

                GridPane grid = new GridPane();

                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 10, 10, 10));

                TextField word = new TextField();
                word.setStyle("-fx-pref-width: 250");
                word.setPromptText("Find a word");

                grid.add(word, 0, 0);

                Button findNextBTN = (Button) findDialog.getDialogPane().lookupButton(findNext);
                findNextBTN.setDisable(true);

                word.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        findNextBTN.setDisable(newValue.trim().isEmpty());
                    }
                });

                findDialog.getDialogPane().setContent(grid);

                findDialog.initModality(Modality.WINDOW_MODAL);

                findNextBTN.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        while ((pos = textArea.getText().toUpperCase().indexOf(word.getText().toUpperCase(), pos)) >= 0) {
                            textArea.selectRange(pos, (pos + word.getText().length()));
                            pos += word.getText().length();
                            break;
                        }
                        event.consume();
                    }
                });

                findDialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent event) {
                        currentDialogs.remove(findDialog);
                    }
                });
            }
            findDialog.showAndWait();
        }
    }

    public void replace() {
        if (!currentDialogs.contains(replaceDialog)) {
            currentDialogs.add(replaceDialog);

            if (replaceDialog.getDialogPane().getButtonTypes().isEmpty()) {
                replaceDialog.setTitle("Replace");
                replaceDialog.setHeaderText(null);

                ButtonType replaceNext = new ButtonType("Replace Next", ButtonBar.ButtonData.OK_DONE);
                ButtonType replaceAll = new ButtonType("Replace All", ButtonBar.ButtonData.OK_DONE);
                replaceDialog.getDialogPane().getButtonTypes().addAll(replaceNext, replaceAll, ButtonType.CANCEL);

                GridPane grid = new GridPane();

                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 10, 10, 10));

                TextField word = new TextField();
                word.setStyle("-fx-pref-width: 250");
                word.setPromptText("Enter a word");

                TextField replaceWord = new TextField();
                replaceWord.setStyle("-fx-pref-width: 250");
                replaceWord.setPromptText("Enter a replacement");

                grid.add(word, 0, 0);
                grid.add(replaceWord, 0, 1);


                Button replaceAllBTN = (Button) replaceDialog.getDialogPane().lookupButton(replaceAll);
                replaceAllBTN.setDisable(true);

                Button replaceNextBTN = (Button) replaceDialog.getDialogPane().lookupButton(replaceNext);
                replaceNextBTN.setDisable(true);

                word.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        replaceNextBTN.setDisable(newValue.trim().isEmpty());
                        replaceAllBTN.setDisable(newValue.trim().isEmpty());
                    }
                });

                replaceDialog.getDialogPane().setContent(grid);

                replaceDialog.initModality(Modality.WINDOW_MODAL);

                replaceNextBTN.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        while ((pos = textArea.getText().toUpperCase().indexOf(word.getText().toUpperCase(), pos)) >= 0) {
                            textArea.selectRange(pos, (pos + word.getText().length()));
                            textArea.replaceSelection(replaceWord.getText());
                            pos += word.getText().length();
                            break;
                        }
                        event.consume();
                    }
                });

                replaceAllBTN.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        while ((pos = textArea.getText().toUpperCase().indexOf(word.getText().toUpperCase(), pos)) >= 0) {
                            textArea.selectRange(pos, (pos + word.getText().length()));
                            textArea.replaceSelection(replaceWord.getText());
                            pos += word.getText().length();
                        }
                        event.consume();
                    }
                });

                replaceDialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent event) {
                        currentDialogs.remove(replaceDialog);
                    }
                });
            }
            replaceDialog.showAndWait();
        }
    }

}
