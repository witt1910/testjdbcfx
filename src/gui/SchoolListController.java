package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.School;
import model.services.SchoolService;

public class SchoolListController implements Initializable{

	private SchoolService service;
	
	@FXML
	private TableView<School> tableViewSchool;
	
	@FXML
	private TableColumn<School, Integer> tableColumnId;
	
	@FXML
	private TableColumn<School, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	private ObservableList<School> obsList;
	
	@FXML
	public void btNewAction() {
		System.out.println("btNewAction");
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
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<School> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSchool.setItems(obsList);
	}
	
}
