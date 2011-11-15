//package org.nbfx.core;
//
//import javafx.application.Application;
//import javafx.embed.swing.JFXPanel;
//import javafx.stage.Stage;
//import org.netbeans.core.startup.RunLevel;
//import org.openide.util.lookup.ServiceProvider;
//
///**
// * @author Administrator
// */
//@ServiceProvider(service = RunLevel.class)
//public class JavaFXRunLevel extends Application implements RunLevel {
//
//    @Override
//    public void run() {
//        new Thread() {
//
//            @Override
//            public void run() {
//                new JFXPanel();
//                System.out.println("initiated");
//            }
//        }.start();
//    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        System.out.println("start(Stage stage)");
//    }
//}
