package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.Comparator;

public class MainController {

    @FXML private ListView<TodoItem> todoList;
    @FXML private TextField titleField;
    @FXML private TextField dateField;
    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button sortBtn;

    private boolean sortByDate = false;

    @FXML
    public void initialize() {

        // ListView のセル表示をカスタム
        todoList.setCellFactory(list -> new ListCell<TodoItem>() {

            CheckBox checkBox = new CheckBox();
            Label titleLabel = new Label();
            Label dateLabel = new Label();

            {
                // チェックしたら削除
                checkBox.setOnAction(e -> {
                    TodoItem item = getItem();
                    if (item != null) {
                        todoList.getItems().remove(item);
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

                    HBox h = new HBox(10, checkBox, titleLabel, dateLabel);
                    setGraphic(h);
                }
            }
        });

        // 追加ボタン
        addBtn.setOnAction(e -> addTask());

        // 編集ボタン
        editBtn.setOnAction(e -> editTask());

        // 並び替え
        sortBtn.setOnAction(e -> toggleSort());
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

    // タスク編集
    private void editTask() {
        TodoItem item = todoList.getSelectionModel().getSelectedItem();
        if (item == null) return;

        TextInputDialog dialog = new TextInputDialog(item.getTitle());
        dialog.setHeaderText("タイトルを編集");
        dialog.setContentText("新しいタイトルを入力：");
        String newTitle = dialog.showAndWait().orElse(null);
        if (newTitle == null) return;

        TextInputDialog dialog2 = new TextInputDialog(item.getDate());
        dialog2.setHeaderText("日付を編集");
        dialog2.setContentText("新しい日付を入力：");
        String newDate = dialog2.showAndWait().orElse(null);
        if (newDate == null) return;

        item.setTitle(newTitle);
        item.setDate(newDate);
        todoList.refresh();
    }

    // 並び替え切り替え
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
