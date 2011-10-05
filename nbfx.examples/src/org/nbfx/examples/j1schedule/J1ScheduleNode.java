/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.nbfx.examples.j1schedule;

import java.awt.Image;
import org.nbfx.examples.j1schedule.J1ScheduleReader.J1Session;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Administrator
 */
public class J1ScheduleNode extends AbstractNode {

    public J1ScheduleNode() {
        super(new J1SessionChildren());
        this.setDisplayName("J1 Schedule");
        this.setIconBaseWithExtension("org/nbfx/examples/j1schedule/javaone.jpg");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.loadImage("org/nbfx/examples/j1schedule/javaone_opened.jpg");
    }

    
    
    private static class J1SessionChildren extends Children.Keys<J1Session> {

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

            public J1SessionNode(J1Session t) {
                super(Children.LEAF);
                this.setDisplayName(t.getTitle() + " @ " + t.getLocation());
                this.setIconBaseWithExtension("org/nbfx/examples/j1schedule/duke.gif");
            }
        }
    }
}
