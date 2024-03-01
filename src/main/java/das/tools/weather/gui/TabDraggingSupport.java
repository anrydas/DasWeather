package das.tools.weather.gui;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.concurrent.atomic.AtomicLong;

public class TabDraggingSupport {
    private Tab draggingTab;
    private static final AtomicLong idGenerator = new AtomicLong();
    private final String draggingID = "TabDraggingSupport-" + idGenerator.incrementAndGet() ;
    public void addDragging(TabPane pane) {
        pane.getTabs().forEach(this::addDragHandlers);
        pane.getTabs().addListener((Change<? extends Tab> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(this::addDragHandlers);
                }
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(this::removeDragHandlers);
                }
            }
        });
        pane.setOnDragOver(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    draggingTab != null &&
                    draggingTab.getTabPane() != pane) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        pane.setOnDragDropped(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    draggingTab != null &&
                    draggingTab.getTabPane() != pane) {

                draggingTab.getTabPane().getTabs().remove(draggingTab);
                pane.getTabs().add(draggingTab);
                draggingTab.getTabPane().getSelectionModel().select(draggingTab);
            }
        });
    }

    private void addDragHandlers(Tab tab) {
        if (tab.getText() != null && ! tab.getText().isEmpty()) {
            Label label = new Label(tab.getText(), tab.getGraphic());
            tab.setText(null);
            tab.setGraphic(label);
        }
        Node graphic = tab.getGraphic();
        graphic.setOnDragDetected(e -> {
            Dragboard dragboard = graphic.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(draggingID);
            dragboard.setContent(content);
            dragboard.setDragView(graphic.snapshot(null, null));
            draggingTab = tab ;
        });
        graphic.setOnDragOver(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    draggingTab != null &&
                    draggingTab.getGraphic() != graphic) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        graphic.setOnDragDropped(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    draggingTab != null &&
                    draggingTab.getGraphic() != graphic) {

                int index = tab.getTabPane().getTabs().indexOf(tab) ;
                draggingTab.getTabPane().getTabs().remove(draggingTab);
                tab.getTabPane().getTabs().add(index, draggingTab);
                draggingTab.getTabPane().getSelectionModel().select(draggingTab);
            }
        });
        graphic.setOnDragDone(e -> draggingTab = null);
    }

    private void removeDragHandlers(Tab tab) {
        tab.getGraphic().setOnDragDetected(null);
        tab.getGraphic().setOnDragOver(null);
        tab.getGraphic().setOnDragDropped(null);
        tab.getGraphic().setOnDragDone(null);
    }
}
