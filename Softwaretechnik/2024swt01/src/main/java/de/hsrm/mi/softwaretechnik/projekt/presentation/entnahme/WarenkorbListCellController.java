package de.hsrm.mi.softwaretechnik.projekt.presentation.entnahme;

import de.hsrm.mi.softwaretechnik.projekt.business.entities.Zutatenpaket;
import de.hsrm.mi.softwaretechnik.projekt.presentation.ViewBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class WarenkorbListCellController extends ListCell<Zutatenpaket> {
    @FXML
    private Pane listCell;

    @FXML
    private HBox listCellRow;

    @FXML
    private ImageView zutatenBild;

    @FXML
    private Label zutatenName;

    public WarenkorbListCellController() {
        this.listCell = new Pane();
        this.listCell.getStyleClass().add("listCell");
        this.listCellRow = new HBox();
        this.listCellRow.getStyleClass().add("listCellRow");
        this.zutatenBild = new ImageView();
        this.zutatenName = new Label();
        this.listCell.getChildren().add(listCellRow);

        this.listCellRow.getChildren().addAll(this.zutatenBild, this.zutatenName);
    }

    /**
     * Method called for each row (cell) to display the given model
     *
     * @param item  The new item for the cell.
     * @param empty whether or not this cell represents data from the list. If it
     *              is empty, then it does not represent any domain data, but is a cell
     *              being used to render an "empty" row.
     */
    @Override
    protected void updateItem(Zutatenpaket item, boolean empty) {
        super.updateItem(item, empty);
        this.setText(null);
        this.setGraphic(null);

        if (!empty) {

            try {
                Image zutatImageView = new Image(new FileInputStream("src/main/resources/images/zutat/" + item.getZutat().getName() + ".png"));
                zutatenBild.setImage(zutatImageView);
                zutatenBild.setFitWidth(50);
                zutatenBild.setFitHeight(50);
                zutatenBild.setPreserveRatio(true);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            this.zutatenName.setText(item.getZutat().getName());
            this.setGraphic(this.listCell);
        }
    }
}
