package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.School;
import model.services.SchoolService;

public class SchoolListController implements Initializable, DataChangeListener {

	private SchoolService service;

	@FXML
	private TableView<School> tableViewSchool;

	@FXML
	private TableColumn<School, Integer> tableColumnId;

	@FXML
	private TableColumn<School, String> tableColumnName;

	@FXML
	private TableColumn<School, School> tableColumnEDIT;

	@FXML
	private TableColumn<School, School> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<School> obsList;

	@FXML
	public void btNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		School obj = new School();
		createDialogForm(obj, "/gui/SchoolForm.fxml", parentStage);
	}

	public void setSchoolService(SchoolService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSchool.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<School> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSchool.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(School obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			SchoolFormController controller = loader.getController();
			controller.setSchool(obj);
			controller.setSchoolService(new SchoolService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter School data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loagind view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<School, School>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(School obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/SchoolForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<School, School>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(School obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(School obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are your sure to delete?");
	
		if (result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Error removing objects", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
