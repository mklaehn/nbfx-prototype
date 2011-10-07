package org.nbfx.examples.j1schedule;

import java.lang.reflect.InvocationTargetException;
import org.nbfx.examples.j1schedule.J1ScheduleReader.J1Session;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

public class J1ScheduleNode extends AbstractNode {

    public J1ScheduleNode() {
        super(new J1SessionChildren());
        this.setDisplayName("J1 Schedule");
        this.setIconBaseWithExtension("org/nbfx/examples/j1schedule/javaone.jpg");
    }

    private static class J1SessionChildren extends Children.Keys<J1Session> {

        public J1SessionChildren() {
            super(true);
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            setKeys(J1ScheduleReader.read().getSessions());
        }

        @Override
        protected Node[] createNodes(J1Session t) {
            return new Node[]{new J1SessionNode(t)};
        }

        private static class J1SessionNode extends AbstractNode {

            final J1Session session;

            public J1SessionNode(J1Session t) {
                super(Children.LEAF);
                session = t;
                this.setDisplayName(t.getTitle() + " @ " + t.getLocation());
                this.setIconBaseWithExtension("org/nbfx/examples/j1schedule/duke.gif");
            }

            @Override
            protected Sheet createSheet() {
                final Sheet sheet = super.createSheet();
                final Sheet.Set sheetSet = new Sheet.Set();

                sheetSet.setDisplayName("Session");
                sheetSet.setName("Session");

                sheetSet.put(new PropertySupport.ReadOnly<String>("Id", String.class, "Id", "Id") {

                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return session.getId();
                    }
                });
                sheetSet.put(new PropertySupport.ReadOnly<String>("Title", String.class, "Title", "Title") {

                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return session.getTitle();
                    }
                });
                sheetSet.put(new PropertySupport.ReadOnly<String>("Location", String.class, "Location", "Location") {

                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return session.getLocation();
                    }
                });

                sheet.put(sheetSet);

                return sheet;
            }
        }
    }
}
