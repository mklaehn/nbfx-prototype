package org.nbfx.browser;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitMenuButtonBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import org.nbfx.util.NBFxImageUtilities;

/**
 * @author Sven
 */
public class WebBrowser {

    private final WebView web = new WebView();
    private final TextField location;
    private final BorderPane pane = new BorderPane();

    public WebBrowser(final String homeUrl) {
        if (null != homeUrl) {
            homeProperty.set(homeUrl);
        }

        web.getEngine().loadContent("<html><body><h1>No data</h1></body></html>");
        final SplitMenuButton backButton = SplitMenuButtonBuilder.create().
                graphic(new ImageView(NBFxImageUtilities.getImage("org/nbfx/browser/resources/back.png"))).
                build();
        final SplitMenuButton forwardButton = SplitMenuButtonBuilder.create().
                graphic(new ImageView(NBFxImageUtilities.getImage("org/nbfx/browser/resources/forward.png"))).
                build();
        final Button homeButton = ButtonBuilder.create().
                graphic(new ImageView(NBFxImageUtilities.getImage("org/nbfx/browser/resources/home.png"))).
                onAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                location.setText(getHome());
                web.getEngine().load(getHome());
                web.requestFocus();
            }
        }).build();

        web.getEngine().getHistory().currentIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                backButton.getItems().clear();
                forwardButton.getItems().clear();
                for (WebHistory.Entry entry : web.getEngine().getHistory().getEntries()) {
                    if (web.getEngine().getHistory().getEntries().indexOf(entry) < t1.intValue()) {
                        backButton.getItems().add(new WebHistoryEntryMenuItem(entry, web.getEngine().getHistory().getEntries().indexOf(entry)));
                    }
                    if (web.getEngine().getHistory().getEntries().indexOf(entry) > t1.intValue()) {
                        forwardButton.getItems().add(new WebHistoryEntryMenuItem(entry, web.getEngine().getHistory().getEntries().indexOf(entry)));
                    }
                }
            }
        });



        location = new TextField();
        location.disableProperty().bind(web.getEngine().getLoadWorker().runningProperty());
        location.setMinWidth(200);
        location.setMaxWidth(200);
        location.setPrefWidth(200);
        location.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                String text = location.getText();
                if (text.startsWith("http://") || text.startsWith("https://")) {
                    web.getEngine().load(text);
                } else {
                    web.getEngine().load("http://" + text);
                }
                web.requestFocus();
            }
        });
        final ToolBar tb = new ToolBar(backButton, forwardButton, homeButton, location);
        final GridPane gp = new GridPane();
        gp.add(tb, 0, 0);
//        gp.add(forwardButton,1,0);
//        gp.add(location,1,0);
        GridPane.setHgrow(tb, Priority.ALWAYS);
        pane.setTop(gp);
        pane.setCenter(web);
        web.setOnZoom(new EventHandler<ZoomEvent>() {
            @Override
            public void handle(ZoomEvent t) {
                web.impl_setScale(web.impl_getScale() * t.getZoomFactor());
            }
        });
        web.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.isMetaDown() && t.getCharacter().equals("0")) {
                    web.impl_setScale(1);
                } else if (t.isMetaDown() && t.getCharacter().equals("+")) {
                    web.impl_setScale(web.impl_getScale() * 1.1);
                }
                if (t.isMetaDown() && t.getCharacter().equals("-")) {
                    web.impl_setScale(web.impl_getScale() * 0.9);
                }
            }
        });
    }
    private final ReadOnlyStringWrapper homeProperty = new ReadOnlyStringWrapper("http://netbeans.org");

    public ReadOnlyStringProperty homeProperty() {
        return homeProperty.getReadOnlyProperty();
    }

    public String getHome() {
        return homeProperty.get();
    }

    public Parent getNode() {
        setLocation(homeProperty.get());
        return pane;
    }

    public void setLocation(final String newLocation) {
        location.setText(newLocation);
        web.getEngine().load(newLocation);
        web.requestFocus();
    }

    private class WebHistoryEntryMenuItem extends MenuItem {

        private final Entry entry;
        private final int index;

        public WebHistoryEntryMenuItem(final WebHistory.Entry entry, final int index) {
            super();
            this.entry = entry;
            this.index = index;
            this.textProperty().set(entry.getTitle());
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    location.setText(WebHistoryEntryMenuItem.this.entry.getUrl());
                    web.getEngine().getHistory().go(WebHistoryEntryMenuItem.this.index - 1);
                    web.requestFocus();
                }
            });
        }
    }
}
