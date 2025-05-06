package org.example.uchattincapstoneproject.viewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.uchattincapstoneproject.model.User;

import java.io.IOException;

public class RegistrationController {
    @FXML
    @FXML
    @FXML
    @FXML


    @FXML
    public void initialize() {

            }
        });


    }

    }


    private boolean validateFirstName() {
            return false;
        }
        return true;
    }

    private boolean validateLastName() {
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String email = emailTF.getText().trim();
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber() {
            return false;
        }
        return true;
    }


        String firstName = firstNameTF.getText().trim();
        String lastName = lastNameTF.getText().trim();
        String email = emailTF.getText().trim();
        String phoneNumber = phoneNumberTF.getText().trim();
        String gender = genderCB.getValue();
        String pronouns = pronounsCB.getValue();



        } else {
    }

            e.printStackTrace();
        }
    }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createAvatarScreen.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}