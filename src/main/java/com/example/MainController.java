package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.nio.file.Files;
import java.util.Comparator;

public class MainController {

    @FXML
    private ListView<TodoItem> activeList;
    @FXML
    private ListView<TodoItem> completedList;
    @FXML
    private TextField titleField;
    @FXML
    private TextField dateField;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button sortBtn;
    @FXML
    private Button deleteBtn;
    @FXML 
    private Button restoreBtn;


    private boolean sortByDate = false;
    private final File saveFile = new File("todo-data.json");

    // 起動時
    @FXML
    public void initialize() {
        setupActiveList();
        setupCompletedList();
        loadTasks();

        addBtn.setOnAction(e -> addTask());
        editBtn.setOnAction(e -> editTask());
        sortBtn.setOnAction(e -> sortTasks());
        deleteBtn.setOnAction(e -> deleteTask());
        restoreBtn.setOnAction(e -> restoreTask());


    }

    // ===== 未完了リスト =====
private void setupActiveList() {
    activeList.setCellFactory(list -> new ListCell<>() {

        CheckBox check = new CheckBox();
        Label label = new Label();
        HBox box = new HBox(10, check, label);

        {
            check.setOnAction(e -> {
                TodoItem item = getItem();
                if (item != null) {
                    item.setDone(true);
                    activeList.getItems().remove(item);
                    completedList.getItems().add(item);
                    saveTasks();
                }
            });
        }

        @Override
        protected void updateItem(TodoItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                check.setSelected(false); // ← 重要
            } else {
                label.setText(item.toString());
                check.setSelected(false); // ← 再利用対策
                setGraphic(box);
            }
        }
    });
}


    // ===== 完了済みリスト =====
    private void setupCompletedList() {
        completedList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(TodoItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("✔ " + item.toString());
                }
            }
        });
    }

    // ===== 追加 =====
    private void addTask() {
        if (titleField.getText().isEmpty() || dateField.getText().isEmpty())
            return;

        activeList.getItems().add(
                new TodoItem(titleField.getText(), dateField.getText()));

        titleField.clear();
        dateField.clear();
        saveTasks();
    }

    // ===== 編集 =====
    private void editTask() {
        TodoItem item = activeList.getSelectionModel().getSelectedItem();
        if (item == null)
            return;

        TextInputDialog t1 = new TextInputDialog(item.getTitle());
        t1.setHeaderText("タイトル編集");
        item.setTitle(t1.showAndWait().orElse(item.getTitle()));

        TextInputDialog t2 = new TextInputDialog(item.getDate());
        t2.setHeaderText("日付編集");
        item.setDate(t2.showAndWait().orElse(item.getDate()));

        activeList.refresh();
        saveTasks();
    }
    //削除
    private void deleteTask() {

        // 未完了タブが選択されている場合
        TodoItem activeItem = activeList.getSelectionModel().getSelectedItem();

        if (activeItem != null) {
            activeList.getItems().remove(activeItem);
            saveTasks();
            return;
        }

        // 完了済みタブが選択されている場合
        TodoItem completedItem = completedList.getSelectionModel().getSelectedItem();

        if (completedItem != null) {
            completedList.getItems().remove(completedItem);
            saveTasks();
            return;
        }

        
    }
    //復元
    private void restoreTask() {

    TodoItem item =
            completedList.getSelectionModel().getSelectedItem();

    if (item == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("復元できません");
        alert.setContentText("完了済みタスクを選択してください");
        alert.show();
        return;
    }

    // 状態を未完了に戻す
    item.setDone(false);

    // リスト移動
    completedList.getItems().remove(item);
    activeList.getItems().add(item);

    saveTasks();
}

    

    // ===== 並び替え =====
    private void sortTasks() {
        if (sortByDate) {
            activeList.getItems().sort(
                    Comparator.comparingLong(TodoItem::getCreatedTime));
            sortBtn.setText("日付順");
        } else {
            activeList.getItems().sort(
                    Comparator.comparing(TodoItem::getDate));
            sortBtn.setText("追加順");
        }
        sortByDate = !sortByDate;
    }

    // ===== 保存 =====
    private void saveTasks() {
        try (PrintWriter pw = new PrintWriter(saveFile, "UTF-8")) {
            for (TodoItem t : activeList.getItems()) {
                pw.println(t.getTitle() + "," + t.getDate() + "," + t.isDone());
            }
            for (TodoItem t : completedList.getItems()) {
                pw.println(t.getTitle() + "," + t.getDate() + "," + t.isDone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== 復元 =====
    private void loadTasks() {
        if (!saveFile.exists())
            return;

        try {
            for (String line : Files.readAllLines(saveFile.toPath())) {
                String[] p = line.split(",");
                TodoItem item = new TodoItem(p[0], p[1]);
                item.setDone(Boolean.parseBoolean(p[2]));

                if (item.isDone()) {
                    completedList.getItems().add(item);
                } else {
                    activeList.getItems().add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
