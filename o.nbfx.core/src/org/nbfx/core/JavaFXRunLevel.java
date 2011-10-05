package org.nbfx.core;

import javafx.application.Application;
import javafx.stage.Stage;
import org.netbeans.core.startup.RunLevel;
import org.netbeans.core.startup.Splash;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Administrator
 */
@ServiceProvider(service = RunLevel.class)
public class JavaFXRunLevel extends Application implements RunLevel {

    @Override
    public void run() {
        System.out.println("Launch Java FX");
        long ms = System.currentTimeMillis();

        Application.launch(JavaFXRunLevel.class, new String[0]); // This is the main start up for JavaFX 2.0

        System.out.println("Launched Java FX in " + (System.currentTimeMillis() - ms) + "ms");
    }

    @Override
    public void start(Stage stage) {
        Splash.getInstance().setRunning(true);
        Splash.getInstance().setRunning(false);
    }
}
