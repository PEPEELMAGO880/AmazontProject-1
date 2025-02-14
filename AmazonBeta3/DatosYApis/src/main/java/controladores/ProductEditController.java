package controladores;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import accesoDatos.CategoryAd;
import accesoDatos.ProductAd;
import data.Category;
import data.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import serv.CategoryService;

public class ProductEditController {

	@FXML
	private TextField searchBar;

	@FXML
	private Button editButton;

	@FXML
	private TextField nameField;

	@FXML
	private TextField priceField;

	@FXML
	private ComboBox<Category> categoryChoiceBox;

	@FXML
	private TextArea descriptionArea;

	@FXML
	private TextArea imagesArea;

	@FXML
	private Label errorLabel;

	private int idProductEditing;

	@FXML
	public void initialize() {

			categoryChoiceBox.getItems().setAll(new CategoryAd().obtenerTodos());

			categoryChoiceBox.setConverter(new StringConverter<>() {
				@Override
				public String toString(Category category) {
					return category != null ? category.name() : "";
				}

				@Override
				public Category fromString(String string) {
					return categoryChoiceBox.getItems().stream().filter(category -> category.name().equals(string))
							.findFirst().orElse(null);
				}
			});

		searchBar.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				getProduct();
			}
		});
	}

	@FXML
	public void getProduct() {

		try {
			var search = searchBar.getText().trim();
			searchBar.setText("");

			int productId = Integer.parseInt(search);

            var product = new ProductAd().obtenerPorId(productId);

			if (product != null) {

				idProductEditing = productId;

				errorLabel.setText("Editando producto con id: " + idProductEditing);

				imagesArea.setText(Arrays.stream(product.images()).collect(Collectors.joining("\n")));
				nameField.setText(product.title());
				priceField.setText(product.price() + "");
				categoryChoiceBox.setValue(product.category());
				descriptionArea.setText(product.description());

				editButton.setDisable(false);

			} else {
				errorLabel.setText("Producto no encontrado");
			}
		} catch (NumberFormatException e) {
			errorLabel.setText("Por favor, ingrese un número válido :(");
		} catch (Exception e) {
			errorLabel.setText("No se encontró el producto :(");
		}

	}

	@FXML
	private void handleEditProduct() {
	    clearErrors();

	    try {
	        String name = nameField.getText().trim();
	        double price = Double.parseDouble(priceField.getText().trim());
	        Category category = categoryChoiceBox.getValue();
	        String description = descriptionArea.getText().trim();
	        String images = imagesArea.getText().trim();

	        if (name.isEmpty() || category == null || description.isEmpty() || images.isEmpty()) {
	            errorLabel.setText("Todos los campos son obligatorios.");
	            return;
	        }

	        Product product = new Product(idProductEditing, name, price, description, category, images.split("\n"));
	        boolean updated = new ProductAd().actualizar(product);
	        
	        if (updated) {
	        	 errorLabel.setText("Producto actualizado correctamente.");
	        	 clearFields();
	            System.out.println("Producto actualizado: " + product);
	            
	        } else {
	            errorLabel.setText("Error al actualizar el producto.");
	        }

	    } catch (NumberFormatException e) {
	        errorLabel.setText("Por favor, ingrese un precio válido.");
	    } catch (Exception e) {
	        errorLabel.setText("Error al actualizar el producto.");
	    }
	}


	private void clearFields() {
		nameField.clear();
		priceField.clear();
		categoryChoiceBox.getSelectionModel().clearSelection();
		descriptionArea.clear();
		imagesArea.clear();
		clearErrors();
		editButton.setDisable(true);

	}

	private void clearErrors() {
		errorLabel.setText("");
	}
}