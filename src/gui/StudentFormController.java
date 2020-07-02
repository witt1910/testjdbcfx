package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.School;
import model.entities.Student;
import model.exceptions.ValidationException;
import model.services.SchoolService;
import model.services.StudentService;

public class StudentFormController implements Initializable {

	private Student entity;

	private StudentService service;

	private SchoolService schoolService;

	private List<DataChangeListener> dataChangeListener = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtDemand;

	@FXML
	private TextField txtGrade;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorDemand;

	@FXML
	private Label labelErrorGrade;

	@FXML
	private ComboBox<School> comboBoxSchool;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<School> obsList;

	public void setStudent(Student entity) {
		this.entity = entity;
	}

	public void setServices(StudentService service, SchoolService schoolService) {
		this.service = service;
		this.schoolService = schoolService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUptade(entity);
			notifyDataChangelisteners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangelisteners() {
		for (DataChangeListener listener : dataChangeListener) {
			listener.onDataChanged();
		}
	}

	private Student getFormData() {
		Student obj = new Student();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field cant be empty");
		}
		obj.setName(txtName.getText());

		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field cant be empty");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		if (txtDemand.getText() == null || txtDemand.getText().trim().equals("")) {
			exception.addError("demand", "Field cant be empty");
		}
		obj.setDemand(txtDemand.getText());

		if (txtGrade.getText() == null || txtGrade.getText().trim().equals("")) {
			exception.addError("grade", "Field cant be empty");
		}
		obj.setGrade(Utils.tryParseToInt(txtGrade.getText()));

		obj.setSchool(comboBoxSchool.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 50);
		Constraints.setTextFieldMaxLength(txtDemand, 100);
		Constraints.setTextFieldInteger(txtGrade);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");

		initializeComboBoxSchool();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtDemand.setText(entity.getDemand());
		txtGrade.setText(String.valueOf(entity.getGrade()));
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getSchool() == null) {
			comboBoxSchool.getSelectionModel().selectFirst();
		}
		comboBoxSchool.setValue(entity.getSchool());

	}

	public void loadAssociatedObjects() {
		if (schoolService == null) {
			throw new IllegalStateException("SchoolService was null");
		}
		List<School> list = schoolService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxSchool.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		labelErrorDemand.setText((fields.contains("demand") ? errors.get("demand") : ""));
		labelErrorGrade.setText((fields.contains("grade") ? errors.get("grade") : ""));
	}

	private void initializeComboBoxSchool() {
		Callback<ListView<School>, ListCell<School>> factory = lv -> new ListCell<School>() {
			@Override
			protected void updateItem(School item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxSchool.setCellFactory(factory);
		comboBoxSchool.setButtonCell(factory.call(null));
	}
}
