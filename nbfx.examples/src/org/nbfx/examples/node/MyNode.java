package org.nbfx.examples.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.nbfx.examples.j1schedule.J1ScheduleNode;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

public class MyNode extends AbstractNode {

    public MyNode() {
        super(new MyChildren());
        setDisplayName("<html>RootNode <font color=\"red\">Hallo</font>");
    }

    enum SubEntry {

        ONE, TWO, THREE, FOUR, FIVE, J1SCHEDULE
    }

    static class MyChildren extends Children.Keys<SubEntry> {

        public MyChildren() {
            super(true);
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(SubEntry.values());
        }

        @Override
        final protected Node[] createNodes(final SubEntry key) {
            if (key == SubEntry.J1SCHEDULE) {
                return new Node[]{new J1ScheduleNode()};
            }
            return new Node[]{new MySubNode(key.name())};
        }
    }

    static class MySubNode extends AbstractNode {

        public MySubNode(String name) {
            super(new HexChildren());
            setDisplayName(name);
        }

        @Override
        protected Sheet createSheet() {
            final Sheet sheet = super.createSheet();
            final Sheet.Set sheetSet = new Sheet.Set();

            sheetSet.setDisplayName("Moins");
            sheetSet.setName("Moins");

            sheetSet.put(new PropertySupport.ReadOnly<String>("name", String.class, "Name", "Name") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return MySubNode.this.getDisplayName();
                }
            });

            sheet.put(sheetSet);

            return sheet;
        }
    }

    static class HexChildren extends Children.Keys<Integer> {

        public HexChildren() {
            super(true);
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        }

        @Override
        protected Node[] createNodes(Integer t) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }

            return new Node[]{new HexNode(t)};
        }
    }

    static class HexNode extends AbstractNode {

        private static final RequestProcessor RP = new RequestProcessor(MyNode.class.getSimpleName(), 2);

        public HexNode(final Integer nr) {
            super(Children.LEAF);
            setDisplayName(Integer.toHexString(nr));

            RP.post(new Runnable() {

                @Override
                public void run() {
                    if (SwingUtilities.isEventDispatchThread()) {
                        setDisplayName(getDisplayName() + "Now");
                        fireIconChange();
                        fireOpenedIconChange();
                    } else {
                        NBFxThreadUtilities.SWING.runLater(this);
                    }
                }
            }, nr * 1000);
            System.out.println(Arrays.toString(getActions(true)));
            System.out.println(Arrays.toString(getActions(false)));
        }

        @Override
        public Image getIcon(final int type) {
            final Image image = new BufferedImage(80, 16, BufferedImage.TYPE_INT_RGB);
            final Graphics graphics = image.getGraphics();

            graphics.setColor(Color.CYAN);
            graphics.drawString(getDisplayName(), 1, 10);

            image.flush();

            return image;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return super.getIcon(type);
        }
    }
}
