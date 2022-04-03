package learn.jfx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class Runner extends Application {

    private Map<String, Long> sizes;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private PieChart pieChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Analyser");

        Button dirBtn = new Button("Choose directory...");
        dirBtn.setOnAction(actionEvent -> {
            File file = new DirectoryChooser().showDialog(stage);
            sizes = new Analyzer().dirrSize(file.toPath());
            buildChart(file.toPath(), stage);
        });

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(dirBtn);
        stage.setScene(new Scene(stackPane, 300,250));
        stage.show();
    }

    private void buildChart(Path path, Stage stage) {
        pieChart = new PieChart(pieChartData);

        refillChart(path);

        Button back = new Button(path.toString());
        back.setOnAction(actionEvent -> refillChart(path.getParent()));

        TableView<String> tableView = new TableView<>();
        TableColumn<String, String> pathColumn = new TableColumn<>("Path");
        tableView.getColumns().add(pathColumn);
        pieChart.getData().forEach(data -> tableView.getItems().add(data.getName()));
        VBox box = new VBox(tableView);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(back);
        borderPane.setCenter(pieChart);
        borderPane.setLeft(box);

        stage.setScene(new Scene(borderPane, 900, 600));
        stage.show();
    }

    private void refillChart(Path path) {
        pieChartData.clear();
        pieChartData.addAll(
                sizes
                        .entrySet()
                        .parallelStream()
                        .filter(entry -> {
                            Path parent = Path.of(entry.getKey()).getParent();
                            return parent != null;
                        })
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .toList()
        );
        pieChart.getData().forEach(data -> {
            data.getNode().addEventHandler(
                    MouseEvent.MOUSE_CLICKED,
                    event -> refillChart(Path.of(data.getName()))
            );
        });
    }
}
