import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** A reusable chat bubble containing a speaker badge and message. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box view", e);
        }
        dialog.setText(text);
    }

    /** Creates a right-aligned message from the user. */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.avatar.setText("YOU");
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /** Creates a left-aligned message from Otaku. */
    public static DialogBox getOtakuDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.avatar.setText("OTA");
        box.getStyleClass().add("otaku-dialog");
        ObservableList<Node> reversed = FXCollections.observableArrayList(box.getChildren());
        Collections.reverse(reversed);
        box.getChildren().setAll(reversed);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }
}
