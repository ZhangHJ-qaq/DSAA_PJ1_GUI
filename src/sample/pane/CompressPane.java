package sample.pane;

import core.Compress;
import core.CompressResult;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Optional;


public class CompressPane extends VBox implements MyPane {
    private Stage stage;
    private Scene scene;
    private Text originalFileInfo;
    private Text outputInfo;
    private Button fileChooserButton;
    private Button dirChooserButton;
    private Button chooseOutputButton;
    private Button startButton;

    private File srcFile;
    private File desFile;

    public CompressPane() {
        this.stage = new Stage();
        this.scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle("压缩");

        //初始化节点
        this.originalFileInfo = new Text("你还没有选择要压缩的文件或目录");
        this.outputInfo = new Text("你还没有选择输出的路径");
        this.fileChooserButton = new Button("选择文件");
        this.dirChooserButton = new Button("选择目录");
        this.chooseOutputButton = new Button("选择要输出的路径");
        this.startButton = new Button("开始压缩");

        //设置字体
        this.originalFileInfo.setFont(Font.font("courier", 15));
        this.outputInfo.setFont(Font.font("courier", 15));
        this.chooseOutputButton.setFont(Font.font("courier", 25));
        this.dirChooserButton.setFont(Font.font("courier", 25));
        this.fileChooserButton.setFont(Font.font("courier", 25));
        this.startButton.setFont(Font.font("courier", 25));

        //


        this.getChildren().addAll(
                originalFileInfo,
                fileChooserButton,
                dirChooserButton,
                outputInfo,
                chooseOutputButton,
                startButton

        );

        this.registerEventHandler();

        this.setMinWidth(800);
        this.setAlignment(Pos.CENTER);


    }

    @Override
    public void pop() {
        this.stage.show();

    }

    @Override
    public void registerEventHandler() {
        //注册选择文件按钮的事件
        this.fileChooserButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            srcFile = fileChooser.showOpenDialog(this.stage);
            if (srcFile != null) {
                this.originalFileInfo.setText(srcFile.getPath());
            }
        });

        //注册选择目录按钮的事件
        this.dirChooserButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            srcFile = directoryChooser.showDialog(this.stage);
            if (srcFile != null) {
                this.originalFileInfo.setText(srcFile.getPath());
            }
        });

        //注册选择输出目录的按钮
        this.chooseOutputButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            desFile = directoryChooser.showDialog(this.stage);
            if (desFile != null) {
                this.outputInfo.setText(desFile.getPath());
            }
        });

        //注册开始压缩按钮的事件
        this.startButton.setOnAction(event -> {

            //先检查用户是否都填写了输入和输出
            if (this.srcFile == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Opps......");
                alert.setContentText("你还没有选择要压缩的文件或目录");
                alert.show();
                return;
            }

            if (this.desFile == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Opps......");
                alert.setContentText("你还没有选择输出的目录");
                alert.show();
                return;
            }

            String name = srcFile.getName();

            //desFile是一个目录，这里得到真正的输出文件
            String outPutPath = desFile.getPath() + "/" + name + "_compressed.haojie";
            File realDesFile = new File(outPutPath);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("确定要开始压缩吗");
            alert.setContentText("压缩的文件将输出到" + realDesFile.getPath());

            Optional<ButtonType> result = alert.showAndWait();


            //如果用户确定压缩
            if (result.get() == ButtonType.OK) {
                try {
                    CompressResult compressResult = Compress.compress(srcFile.getPath(), realDesFile.getPath());
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("压缩成功");
                    alert1.setHeaderText("压缩成功");
                    alert1.setContentText(
                            "压缩后的文件已经存储到" + realDesFile.getPath() + "\n"
                                    + "压缩前文件大小为" + compressResult.originalFileSize / 1024.0 / 1024.0 + "MB\n"
                                    + "压缩后文件大小为" + compressResult.compressedFileSize / 1024.0 / 1024.0 + "MB\n"
                                    + "压缩时间是" + compressResult.timeConsumed + "s\n"
                                    + "压缩速度是" + compressResult.speed / 1024.0 / 1024.0 + "MB/s\n"
                                    + "压缩比是" + (compressResult.compressedFileSize) / (double)compressResult.originalFileSize
                    );
                    alert1.show();

                } catch (Exception e) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(e.getMessage());
                    alert1.show();

                } finally {
                    this.stage.close();
                    clear();
                }

            }


        });


    }

    private void clear() {
        this.stage = null;
        this.scene = null;
        this.originalFileInfo = null;
        this.outputInfo = null;
        this.fileChooserButton = null;
        this.dirChooserButton = null;
        this.chooseOutputButton = null;
        this.startButton = null;

        this.srcFile = null;
        this.desFile = null;
        System.gc();
    }


}
