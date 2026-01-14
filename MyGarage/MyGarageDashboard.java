package MyGarage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The main dashboard of the application.
 * This class serves as the central hub where the user manages their vehicles.
 * It features a sidebar for navigation and a dynamic central area containing 
 * Overview, Expenses, Dream Spec, and Track Days tabs.
 */
public class MyGarageDashboard {

    private User currentUser;
    private BorderPane mainLayout;
    private VBox vehicleListContainer;
    private final String[] CURRENCIES = {"TL", "USD", "EUR"};
    
    // Window dragging coordinates
    private double xOffset = 0;
    private double yOffset = 0;

    public MyGarageDashboard(User user) {
        this.currentUser = user;
    }

    /**
     * Initializes and displays the Dashboard window.
     * @param stage The primary stage of the application.
     */
    public void show(Stage stage) {
        mainLayout = new BorderPane();
        
        // Enable window dragging for undecorated stage
        mainLayout.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        mainLayout.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // --- SIDEBAR (Left Navigation) ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30));
        sidebar.setPrefWidth(320);
        sidebar.setStyle("-fx-background-color: #161616;"); 

        Label lblTitle = new Label("MY GARAGE 🏁");
        lblTitle.setFont(Font.font("Impact", 32));
        lblTitle.setTextFill(Color.WHITE);

        vehicleListContainer = new VBox(15);
        
        Button btnAddVehicle = new Button("+ Add New Machine");
        btnAddVehicle.setMaxWidth(Double.MAX_VALUE);
        btnAddVehicle.getStyleClass().add("accent-button");
        btnAddVehicle.setOnAction(e -> showAddVehicleDialog());

        Button btnLogout = new Button("Logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("outline-button");
        btnLogout.setOnAction(e -> {
            SessionManager.clearSession(); 
            new WelcomeScreen().show(stage); 
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(lblTitle, new Separator(), vehicleListContainer, spacer, btnAddVehicle, btnLogout);

        // --- WINDOW CONTROLS (Top Bar) ---
        HBox windowControls = new HBox(10);
        windowControls.setAlignment(Pos.TOP_RIGHT);
        windowControls.setPadding(new Insets(15, 20, 0, 0));

        Button btnClose = new Button("X");
        btnClose.setStyle("-fx-background-color: #E63946; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 30; -fx-min-height: 30;");
        btnClose.setOnAction(e -> System.exit(0));

        Button btnMinimize = new Button("_");
        btnMinimize.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50; -fx-min-width: 30; -fx-min-height: 30;");
        btnMinimize.setOnAction(e -> stage.setIconified(true)); 

        windowControls.getChildren().addAll(btnMinimize, btnClose);

        // --- CENTER AREA (Welcome State) ---
        VBox centerArea = new VBox(20);
        centerArea.setPadding(new Insets(20, 50, 50, 50));
        centerArea.setAlignment(Pos.CENTER);

        VBox topContainer = new VBox(10);
        topContainer.getChildren().add(windowControls);

        Label lblWelcome = new Label("Welcome back, " + currentUser.getUsername());
        lblWelcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        lblWelcome.setTextFill(Color.web("#555"));
        
        Label lblHint = new Label("Select a ride to start managing.");
        lblHint.setTextFill(Color.web("#444"));
        lblHint.setFont(Font.font(18));

        centerArea.getChildren().addAll(lblWelcome, lblHint);
        
        // Wrap center area in ScrollPane for responsiveness
        ScrollPane welcomeScroll = new ScrollPane(centerArea);
        welcomeScroll.setFitToWidth(true);
        welcomeScroll.setFitToHeight(true);
        welcomeScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        
        BorderPane contentPane = new BorderPane();
        contentPane.setTop(topContainer);
        contentPane.setCenter(welcomeScroll);

        mainLayout.setCenter(contentPane);
        mainLayout.setLeft(sidebar);

        refreshSidebar();

        Scene scene = new Scene(mainLayout, 1280, 850);
        scene.setFill(Color.TRANSPARENT); 
        if (stage.getStyle() != StageStyle.TRANSPARENT) {
            try { scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); } 
            catch (Exception ex) {}
        }

        stage.setTitle("MyGarage - Manager Console");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Refreshes the sidebar vehicle list based on the user's garage.
     */
    private void refreshSidebar() {
        vehicleListContainer.getChildren().clear();
        if (currentUser.getGarage().isEmpty()) {
            Label emptyLbl = new Label("Garage is empty.");
            emptyLbl.setTextFill(Color.GRAY);
            vehicleListContainer.getChildren().add(emptyLbl);
        } else {
            for (Vehicle v : currentUser.getGarage()) {
                Button btnVehicle = new Button(v.getYear() + " " + v.getModel());
                btnVehicle.setMaxWidth(Double.MAX_VALUE);
                btnVehicle.setAlignment(Pos.CENTER_LEFT);
                btnVehicle.getStyleClass().add("button");
                btnVehicle.setStyle("-fx-background-color: #252525; -fx-font-size: 15px; -fx-padding: 15 25;");
                btnVehicle.setOnAction(e -> loadVehicleDetails(v));
                vehicleListContainer.getChildren().add(btnVehicle);
            }
        }
    }

    /**
     * Loads the details of the selected vehicle into the center pane.
     * Sets up Tabs: Overview, Expenses, Dream Spec, Track Days.
     */
    private void loadVehicleDetails(Vehicle v) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab tabOverview = new Tab("Specs & Overview");
        tabOverview.setContent(createOverviewContent(v));

        Tab tabExpenses = new Tab("Expenses");
        tabExpenses.setContent(createExpensesContent(v)); 

        Tab tabDream = new Tab("Dream Spec");
        tabDream.setContent(createDreamSpecContent(v)); 
        
        Tab tabTrack = new Tab("Track Days");
        tabTrack.setContent(createTrackContent(v));

        tabPane.getTabs().addAll(tabOverview, tabExpenses, tabDream, tabTrack);

        // HEADER SECTION
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(0, 50, 50, 50));

        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        String ownerTitle = currentUser.getUsername().toLowerCase() + "'s " + v.getBrand().toLowerCase() + " " + v.getModel().toLowerCase();
        Label lblHeader = new Label(ownerTitle);
        lblHeader.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 60)); 
        lblHeader.setTextFill(Color.WHITE);
        
        Label lblSub = new Label(v.getYear() + " | " + v.getGeneration() + " | " + v.getColor());
        lblSub.setFont(Font.font("Segoe UI", 20));
        lblSub.setTextFill(Color.web("#E63946")); 

        headerBox.getChildren().addAll(lblHeader, lblSub);
        mainContainer.getChildren().addAll(headerBox, tabPane);
        
        // SCROLL PANE CONFIGURATION
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        BorderPane contentPane = (BorderPane) mainLayout.getCenter();
        contentPane.setCenter(scrollPane);
    }

    // --- 1. OVERVIEW TAB CONTENT ---
    private VBox createOverviewContent(Vehicle v) {
        VBox box = new VBox(25);
        box.setPadding(new Insets(25, 0, 0, 0));
        
        FlowPane stats = new FlowPane();
        stats.setHgap(20); stats.setVgap(20);
        
        stats.getChildren().add(createStatCard("Power / Torque", v.getPower() + " HP / " + v.getTorque() + " Nm"));
        
        VBox kmCard = new VBox(15);
        kmCard.getStyleClass().add("rounded-box");
        kmCard.setPrefWidth(280);
        
        Label lblKmTitle = new Label("Odometer");
        lblKmTitle.setTextFill(Color.GRAY);
        
        HBox kmInputBox = new HBox(10);
        TextField txtKm = new TextField(String.valueOf(v.getKilometer()));
        txtKm.setPrefWidth(120);
        
        Button btnUpdateKm = new Button("SAVE");
        btnUpdateKm.getStyleClass().add("accent-button");
        btnUpdateKm.setPadding(new Insets(10, 20, 10, 20));
        
        btnUpdateKm.setOnAction(e -> {
            try {
                int newKm = Integer.parseInt(txtKm.getText());
                v.setKilometer(newKm);
                saveChanges();
            } catch (NumberFormatException ex) { txtKm.setText(String.valueOf(v.getKilometer())); }
        });
        
        kmInputBox.getChildren().addAll(txtKm, btnUpdateKm);
        kmCard.getChildren().addAll(lblKmTitle, kmInputBox);
        stats.getChildren().add(kmCard);
        
        // Calculate Total Expenses per Currency
        Map<String, Double> expenseTotals = new HashMap<>();
        for(Expense e : v.getExpenses()) expenseTotals.put(e.getCurrency(), expenseTotals.getOrDefault(e.getCurrency(), 0.0) + e.getAmount());
        stats.getChildren().add(createMultiCurrencyStatCard("Total Expenses", expenseTotals));

        // Calculate Dream Spec Cost
        Map<String, Double> dreamTotals = new HashMap<>();
        for(DreamItem d : v.getDreamList()) dreamTotals.put(d.getCurrency(), dreamTotals.getOrDefault(d.getCurrency(), 0.0) + d.getEstimatedCost());
        
        VBox dreamCard = createMultiCurrencyStatCard("Dream Spec Cost", dreamTotals);
        ((Label)dreamCard.getChildren().get(0)).setTextFill(Color.web("#4CAF50")); 
        stats.getChildren().add(dreamCard);
        
        box.getChildren().add(stats);
        return box;
    }

    // --- 2. EXPENSES TAB CONTENT ---
    private VBox createExpensesContent(Vehicle v) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(25, 0, 0, 0));

        HBox topSection = new HBox(30);
        topSection.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(topSection, Priority.ALWAYS);

        TableView<Expense> table = new TableView<>();
        table.setPlaceholder(new Label("No expenses recorded."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Expense, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Expense, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        TableColumn<Expense, String> colAmount = new TableColumn<>("Amount");
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #E63946; -fx-font-weight: bold;");
        colAmount.setCellValueFactory(cellData -> {
            Expense exp = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> String.format("%.2f %s", exp.getAmount(), exp.getCurrency()));
        });

        TableColumn<Expense, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(colDate, colCat, colAmount, colDesc);
        ObservableList<Expense> expenseData = FXCollections.observableArrayList(v.getExpenses());
        table.setItems(expenseData);

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Breakdown");
        pieChart.setLabelsVisible(false);
        pieChart.setPrefSize(250, 250);
        updatePieChart(pieChart, v);

        topSection.getChildren().addAll(table, pieChart);
        HBox.setHgrow(table, Priority.ALWAYS);

        VBox formContainer = new VBox(20);
        formContainer.getStyleClass().add("rounded-box");

        Label lblFormTitle = new Label("Add New Expense");
        lblFormTitle.setTextFill(Color.WHITE);
        lblFormTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);

        ComboBox<Expense.ExpenseCategory> catBox = new ComboBox<>();
        catBox.getItems().addAll(Expense.ExpenseCategory.values());
        catBox.setValue(Expense.ExpenseCategory.FUEL);
        catBox.setMaxWidth(Double.MAX_VALUE);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        
        HBox amountBox = new HBox(10);
        TextField txtAmount = new TextField(); txtAmount.setPromptText("Amount");
        ComboBox<String> currencyBox = new ComboBox<>();
        currencyBox.getItems().addAll(CURRENCIES);
        currencyBox.setValue("TL");
        currencyBox.setPrefWidth(100);
        amountBox.getChildren().addAll(txtAmount, currencyBox);
        HBox.setHgrow(txtAmount, Priority.ALWAYS);

        TextField txtDesc = new TextField(); txtDesc.setPromptText("Description (e.g. Shell V-Power)");

        grid.add(catBox, 0, 0); grid.add(datePicker, 1, 0);
        grid.add(amountBox, 0, 1); grid.add(txtDesc, 1, 1);
        
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Button btnAdd = new Button("Add Expense");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.getStyleClass().add("accent-button");
        
        btnAdd.setOnAction(e -> {
            try {
                if(txtAmount.getText().isEmpty()) return;
                String amountStr = txtAmount.getText().replace(",", ".");
                double amount = Double.parseDouble(amountStr);
                Expense newExp = new Expense(catBox.getValue(), amount, currencyBox.getValue(), txtDesc.getText(), datePicker.getValue());
                v.addExpense(newExp);
                saveChanges();
                expenseData.add(newExp);
                updatePieChart(pieChart, v);
                txtAmount.clear(); txtDesc.clear();
            } catch (NumberFormatException ex) {}
        });

        formContainer.getChildren().addAll(lblFormTitle, grid, btnAdd);
        root.getChildren().addAll(topSection, formContainer);
        return root;
    }

    // --- 3. DREAM SPEC TAB CONTENT ---
    private VBox createDreamSpecContent(Vehicle v) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(25, 0, 0, 0));

        VBox progressBox = new VBox(10);
        Label lblProgress = new Label();
        lblProgress.setTextFill(Color.WHITE);
        lblProgress.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(10);

        Runnable updateProgressAction = () -> {
            long completedCount = v.getDreamList().stream().filter(DreamItem::isDone).count();
            int totalCount = v.getDreamList().size();
            double progress = totalCount == 0 ? 0 : (double) completedCount / totalCount;
            progressBar.setProgress(progress);
            lblProgress.setText("Project Completion: " + (int)(progress * 100) + "% (" + completedCount + "/" + totalCount + ")");
        };
        updateProgressAction.run();
        progressBox.getChildren().addAll(lblProgress, progressBar);

        TableView<DreamItem> table = new TableView<>();
        table.setPlaceholder(new Label("Start dreaming... Add your first mod!"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DreamItem, Boolean> colStatus = new TableColumn<>("Done");
        colStatus.setMinWidth(60); colStatus.setMaxWidth(60);
        colStatus.setCellValueFactory(new PropertyValueFactory<>("done"));
        
        colStatus.setCellFactory(column -> new TableCell<DreamItem, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            {
                setAlignment(Pos.CENTER);
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    DreamItem item = getTableRow().getItem();
                    if (item != null && item.isDone() != newVal) {
                        item.setDone(newVal);
                        saveChanges();
                        updateProgressAction.run();
                    }
                });
            }
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else { checkBox.setSelected(item); setGraphic(checkBox); }
            }
        });

        TableColumn<DreamItem, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCat.setMinWidth(120); colCat.setMaxWidth(150);
        
        TableColumn<DreamItem, String> colDesc = new TableColumn<>("Part / Brand");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        TableColumn<DreamItem, String> colCost = new TableColumn<>("Est. Cost");
        colCost.setMinWidth(120); colCost.setMaxWidth(150);
        colCost.setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #4CAF50; -fx-font-weight: bold;"); 
        colCost.setCellValueFactory(cellData -> {
            DreamItem item = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> String.format("%.2f %s", item.getEstimatedCost(), item.getCurrency()));
        });

        TableColumn<DreamItem, String> colDate = new TableColumn<>("Plan Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("plannedDate"));
        colDate.setMinWidth(100); colDate.setMaxWidth(120);

        table.getColumns().addAll(colStatus, colCat, colDesc, colCost, colDate);
        ObservableList<DreamItem> dreamData = FXCollections.observableArrayList(v.getDreamList());
        table.setItems(dreamData);

        VBox formContainer = new VBox(20);
        formContainer.getStyleClass().add("rounded-box");

        Label lblFormTitle = new Label("Add to Wishlist");
        lblFormTitle.setTextFill(Color.WHITE);
        lblFormTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        
        ComboBox<DreamItem.DreamCategory> catBox = new ComboBox<>();
        catBox.getItems().addAll(DreamItem.DreamCategory.values());
        catBox.setValue(DreamItem.DreamCategory.WHEELS);
        catBox.setMaxWidth(Double.MAX_VALUE);

        TextField txtDesc = new TextField(); txtDesc.setPromptText("Part Detail (e.g. BBS RI-A)");

        HBox costBox = new HBox(10);
        TextField txtCost = new TextField(); txtCost.setPromptText("Est. Price");
        ComboBox<String> currencyBox = new ComboBox<>();
        currencyBox.getItems().addAll(CURRENCIES);
        currencyBox.setValue("TL");
        currencyBox.setPrefWidth(100);
        costBox.getChildren().addAll(txtCost, currencyBox);
        HBox.setHgrow(txtCost, Priority.ALWAYS);

        DatePicker datePicker = new DatePicker(java.time.LocalDate.now().plusMonths(1));
        datePicker.setMaxWidth(Double.MAX_VALUE);

        grid.add(catBox, 0, 0); grid.add(txtDesc, 1, 0);
        grid.add(costBox, 0, 1); grid.add(datePicker, 1, 1);
        
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Button btnAdd = new Button("Add to Dreams ✨");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.getStyleClass().add("success-button");

        btnAdd.setOnAction(e -> {
            try {
                if(txtDesc.getText().isEmpty()) return;
                double cost = txtCost.getText().isEmpty() ? 0 : Double.parseDouble(txtCost.getText());
                DreamItem newItem = new DreamItem(catBox.getValue(), txtDesc.getText(), cost, currencyBox.getValue(), datePicker.getValue());
                v.addDreamItem(newItem);
                saveChanges();
                dreamData.add(newItem);
                updateProgressAction.run(); 
                txtDesc.clear(); txtCost.clear();
            } catch (NumberFormatException ex) {}
        });

        formContainer.getChildren().addAll(lblFormTitle, grid, btnAdd);
        root.getChildren().addAll(progressBox, table, formContainer);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    // --- 4. TRACK DAYS TAB CONTENT ---
    private VBox createTrackContent(Vehicle v) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(25, 0, 0, 0));

        HBox statsBox = new HBox(20);
        VBox statCard = createStatCard("Total Track Days", v.getTrackLog().size() + " Sessions");
        statsBox.getChildren().add(statCard);

        TableView<TrackSession> table = new TableView<>();
        table.setPlaceholder(new Label("No track days recorded. Get out there! 🏎️"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TrackSession, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<TrackSession, String> colTrack = new TableColumn<>("Track Name");
        colTrack.setCellValueFactory(new PropertyValueFactory<>("trackName"));
        
        TableColumn<TrackSession, String> colTime = new TableColumn<>("Best Lap");
        colTime.setCellValueFactory(new PropertyValueFactory<>("lapTime"));
        colTime.setStyle("-fx-text-fill: #E63946; -fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");
        
        TableColumn<TrackSession, String> colCond = new TableColumn<>("Conditions");
        colCond.setCellValueFactory(new PropertyValueFactory<>("conditions"));

        TableColumn<TrackSession, String> colTires = new TableColumn<>("Tires");
        colTires.setCellValueFactory(new PropertyValueFactory<>("tires"));

        table.getColumns().addAll(colDate, colTrack, colTime, colCond, colTires);
        ObservableList<TrackSession> trackData = FXCollections.observableArrayList(v.getTrackLog());
        table.setItems(trackData);

        VBox formContainer = new VBox(20);
        formContainer.getStyleClass().add("rounded-box");

        Label lblFormTitle = new Label("Add Track Session");
        lblFormTitle.setTextFill(Color.WHITE);
        lblFormTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);

        TextField txtTrack = new TextField(); txtTrack.setPromptText("Track Name (e.g. Istanbul Park)");
        TextField txtTime = new TextField(); txtTime.setPromptText("Best Lap (e.g. 2:15.450)");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        
        ComboBox<String> condBox = new ComboBox<>();
        condBox.getItems().addAll("Dry ☀️", "Wet 🌧️", "Damp ☁️", "Night 🌙");
        condBox.setValue("Dry ☀️");
        condBox.setMaxWidth(Double.MAX_VALUE);

        TextField txtTires = new TextField(); txtTires.setPromptText("Tires (e.g. Michelin Cup 2)");

        grid.add(txtTrack, 0, 0); grid.add(txtTime, 1, 0);
        grid.add(datePicker, 0, 1); grid.add(condBox, 1, 1);
        grid.add(txtTires, 0, 2, 2, 1); 
        
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Button btnAdd = new Button("Record Session 🏁");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.getStyleClass().add("accent-button");

        btnAdd.setOnAction(e -> {
            if(txtTrack.getText().isEmpty() || txtTime.getText().isEmpty()) return;
            
            TrackSession newSession = new TrackSession(
                txtTrack.getText(), 
                txtTime.getText(), 
                datePicker.getValue(), 
                condBox.getValue(), 
                txtTires.getText()
            );
            
            v.addTrackSession(newSession);
            saveChanges();
            trackData.add(newSession);
            
            ((Label)statCard.getChildren().get(1)).setText((v.getTrackLog().size()) + " Sessions");

            txtTrack.clear(); txtTime.clear(); txtTires.clear();
        });

        formContainer.getChildren().addAll(lblFormTitle, grid, btnAdd);
        root.getChildren().addAll(statsBox, table, formContainer);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    /**
     * Shows a popup dialog to add a new vehicle to the garage.
     */
    private void showAddVehicleDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Machine");

        VBox root = new VBox(25);
        root.setPadding(new Insets(35));
        root.setStyle("-fx-background-color: #121212;"); 

        Label title = new Label("Add to Garage");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Impact", 28));

        TextField txtBrand = new TextField(); txtBrand.setPromptText("Brand (e.g. BMW)");
        TextField txtModel = new TextField(); txtModel.setPromptText("Model (e.g. 320i)");
        TextField txtGen = new TextField(); txtGen.setPromptText("Gen (e.g. F30)");
        TextField txtYear = new TextField(); txtYear.setPromptText("Year (e.g. 2016)");
        TextField txtColor = new TextField(); txtColor.setPromptText("Color (e.g. Estoril Blue)");
        TextField txtKm = new TextField(); txtKm.setPromptText("KM (e.g. 120000)");
        TextField txtHp = new TextField(); txtHp.setPromptText("Power (HP)");
        TextField txtNm = new TextField(); txtNm.setPromptText("Torque (Nm)");

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        grid.add(txtBrand, 0, 0);  grid.add(txtModel, 1, 0);
        grid.add(txtGen, 0, 1);    grid.add(txtYear, 1, 1);
        grid.add(txtColor, 0, 2);  grid.add(txtKm, 1, 2);
        grid.add(txtHp, 0, 3);     grid.add(txtNm, 1, 3);

        Button btnSave = new Button("PARK IT! 🅿️");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.getStyleClass().add("accent-button");
        
        btnSave.setOnAction(e -> {
            try {
                if (txtBrand.getText().isEmpty() || txtModel.getText().isEmpty()) return;
                int km = txtKm.getText().isEmpty() ? 0 : Integer.parseInt(txtKm.getText());
                int hp = txtHp.getText().isEmpty() ? 0 : Integer.parseInt(txtHp.getText());
                int nm = txtNm.getText().isEmpty() ? 0 : Integer.parseInt(txtNm.getText());

                // FIX: Removed VehicleType enum usage here
                Vehicle newVehicle = new Vehicle(
                    txtBrand.getText(), 
                    txtModel.getText(), 
                    txtGen.getText(), 
                    txtYear.getText(), 
                    txtColor.getText(), 
                    km, hp, nm
                );
                
                currentUser.addVehicleToGarage(newVehicle);
                saveChanges(); 
                refreshSidebar(); 
                dialog.close();
            } catch (NumberFormatException ex) {}
        });

        root.getChildren().addAll(title, grid, new Region(), btnSave);
        Scene scene = new Scene(root, 450, 550);
        try {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch (Exception ex) {}
        
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Saves changes to the local database.
     */
    private void saveChanges() {
        ArrayList<User> allUsers = DataBaseManager.loadUsers();
        boolean found = false;
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUsername().equals(currentUser.getUsername())) {
                allUsers.set(i, currentUser);
                found = true;
                break;
            }
        }
        if (!found) allUsers.add(currentUser);
        DataBaseManager.saveUsers(allUsers);
    }
    
    private void updatePieChart(PieChart chart, Vehicle v) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Expense.ExpenseCategory cat : Expense.ExpenseCategory.values()) {
            double total = 0;
            for (Expense e : v.getExpenses()) if (e.getCategory() == cat) total += e.getAmount();
            if (total > 0) pieData.add(new PieChart.Data(cat.toString(), total));
        }
        chart.setData(pieData);
    }
    
    private VBox createMultiCurrencyStatCard(String title, Map<String, Double> totals) {
        VBox card = new VBox(10);
        card.getStyleClass().add("rounded-box"); 
        card.setPrefWidth(280);
        
        Label lblTitle = new Label(title);
        lblTitle.setTextFill(Color.GRAY);
        card.getChildren().add(lblTitle);
        
        if (totals.isEmpty()) {
            Label lblValue = new Label("0.00");
            lblValue.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            lblValue.setTextFill(Color.WHITE);
            card.getChildren().add(lblValue);
        } else {
            for (Map.Entry<String, Double> entry : totals.entrySet()) {
                Label lblValue = new Label(String.format("%.2f %s", entry.getValue(), entry.getKey()));
                lblValue.setFont(Font.font("Arial", FontWeight.BOLD, 22));
                lblValue.setTextFill(Color.WHITE);
                card.getChildren().add(lblValue);
            }
        }
        return card;
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(10);
        card.getStyleClass().add("rounded-box");
        card.setPrefWidth(280);
        Label lblTitle = new Label(title);
        lblTitle.setTextFill(Color.GRAY);
        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblValue.setTextFill(Color.WHITE);
        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }
}