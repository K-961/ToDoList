package com.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

public class MainController {

    @FXML private ListView<TodoItem> todoList;
    @FXML private TextField titleField;
    @FXML private TextField dateField;
    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button sortBtn;

    private boolean sortByDate = false;
    private final String FILE_NAME = "todo-data.json";

    @FXML
    public void initialize() {

        loadData(); // ← 起動時に読み込み!

        // ListView セル
        todoList.setCellFactory(list -> new ListCell<TodoItem>() {

            CheckBox checkBox = new CheckBox();
            Label titleLabel = new Label();
            Label dateLabel = new Label();

            {
                // 完了 (削除)
                checkBox.setOnAction(e -> {
                    TodoItem item = getItem();
                    if (item != null) {
                        todoList.getItems().remove(item);
                        saveData();  // ← 削除したら保存
                    }
                });
            }

            @Override
            protected void updateItem(TodoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(item.getTitle());
                    dateLabel.setText(item.getDate());
                    titleLabel.setMinWidth(200);
                    dateLabel.setMinWidth(100);
                    setGraphic(new HBox(10, checkBox, titleLabel, dateLabel));
                }
            }
        });

        addBtn.setOnAction(e -> {
            addTask();
            saveData(); // ← 追加後に保存
        });

        editBtn.setOnAction(e -> {
            editTask();
            saveData(); // ← 編集後に保存
        });

        sortBtn.setOnAction(e -> toggleSort());
    }

    // ------------------------------
    // JSON 保存処理
    // ------------------------------
    private void saveData() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            Gson gson = new Gson();
            gson.toJson(todoList.getItems(), writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------
    // JSON 読み込み処理
    // ------------------------------
    private void loadData() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TodoItem>>() {}.getType();
            List<TodoItem> list = gson.fromJson(reader, listType);
            todoList.getItems().addAll(list);
        } catch (Exception e) {
            // 最初の起動時はファイルがない → 無視してOK
        }
    }

    // タスク追加
    private void addTask() {
        String title = titleField.getText();
        String date = dateField.getText();

        if (title.isEmpty() || date.isEmpty()) return;

        todoList.getItems().add(new TodoItem(title, date));

        titleField.clear();
        dateField.clear();
    }

    // 編集
    private void editTask() {
        TodoItem item = todoList.getSelectionModel().getSelectedItem();
        if (item == null) return;

        TextInputDialog dialog = new TextInputDialog(item.getTitle());
        dialog.setHeaderText("タイトルを編集");
        String newTitle = dialog.showAndWait().orElse(null);
        if (newTitle == null) return;

        TextInputDialog dialog2 = new TextInputDialog(item.getDate());
        dialog2.setHeaderText("日付を編集");
        String newDate = dialog2.showAndWait().orElse(null);
        if (newDate == null) return;

        item.setTitle(newTitle);
        item.setDate(newDate);
        todoList.refresh();
    }

    private void toggleSort() {
        sortByDate = !sortByDate;

        if (sortByDate) {
            todoList.getItems().sort(Comparator.comparing(TodoItem::getDate));
            sortBtn.setText("追加順");
        } else {
            todoList.getItems().sort(Comparator.comparing(TodoItem::getCreatedTime));
            sortBtn.setText("日付順");
        }
    }
}
