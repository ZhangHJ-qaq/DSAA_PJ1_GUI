package sample.pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * 欢迎界面的面板
 */
public class WelcomePane extends VBox implements MyPane {
    private Button compressButton;
    private Button decompressButton;
    private Stage stage;
    private Scene scene;

    public WelcomePane() {
        compressButton = new Button("压缩");
        decompressButton = new Button("解压");

        compressButton.setFont(Font.font("courier", 50));
        decompressButton.setFont(Font.font("courier", 50));


        this.setAlignment(Pos.CENTER);

        this.getChildren().addAll(compressButton, decompressButton);

        this.setPadding(new Insets(10, 10, 10, 10));

        this.registerEventHandler();


        this.stage = new Stage();


        this.scene = new Scene(this);


        stage.setResizable(false);
        stage.setTitle("你想做什么？");

        stage.setScene(scene);
    }

    public void registerEventHandler() {
        compressButton.setOnAction(e -> {
            CompressPane compressPane = new CompressPane();
            compressPane.pop();

        });

        decompressButton.setOnAction(event -> {
            DecompressPane decompressPane = new DecompressPane();
            decompressPane.pop();
        });

    }

    public void pop() {


        stage.show();


    }
}
