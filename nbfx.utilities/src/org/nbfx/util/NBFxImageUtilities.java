/**
 * This file is part of the NBFx.
 *
 * NBFx is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 2 of the License only.
 *
 * NBFx is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NBFx. If not, see <http://www.gnu.org/licenses/>.
 *
 * The NBFx project designates this particular file as subject to the
 * "Classpath" exception as provided by the NBFx Project in the GPL Version 2
 * section of the License file that accompanied this code.
 */
package org.nbfx.util;

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

/**
 * @author martin
 */
public class NBFxImageUtilities {

    private NBFxImageUtilities() {
    }

    public static Image getImage(final Object object) {
        if (object instanceof String) {
            return getImage(ImageUtilities.loadImage((String) object));
        } else if (object instanceof Image) {
            return (Image) object;
        } else if (object instanceof ImageIcon) {
            return getImage(((ImageIcon) object).getImage());
        } else if (object instanceof java.awt.Image) {
            return getImage((java.awt.Image) object);
        } else if (object instanceof Icon) {
            return getImage((Icon) object);
        } else {
            throw new IllegalArgumentException("Illegal parameter. cannot get Image from it");
        }
    }

    private static Image getImage(final java.awt.Image image) {
        final RenderedImage ri = toRenderedImage(image);

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ImageIO.write(ri, "png", baos);
            baos.flush();

            return new Image(new ByteArrayInputStream(baos.toByteArray()));
        } catch (final IOException ioe) {
            return null;
        }
    }

    private static Image getImage(final Icon icon) {
        return getImage(icon2Image(icon));
    }

    private static java.awt.Image icon2Image(final Icon icon) {
        if (icon instanceof ImageIcon) {
            return ImageIcon.class.cast(icon).getImage();
        } else {
            final BufferedImage bufferedImage = createBufferedImage(icon.getIconWidth(), icon.getIconHeight());
            final Graphics g = bufferedImage.createGraphics();

            icon.paintIcon(new JLabel(), g, 0, 0);
            g.dispose();
            bufferedImage.flush();

            return bufferedImage;
        }
    }

    private static RenderedImage toRenderedImage(final java.awt.Image image) {
        if (image instanceof RenderedImage) {
            return RenderedImage.class.cast(image);
        } else {
            final BufferedImage bufferedImage = createBufferedImage(image.getWidth(null), image.getHeight(null));
            final Graphics g = bufferedImage.createGraphics();

            g.drawImage(image, 0, 0, null);
            g.dispose();
            image.flush();

            return bufferedImage;
        }
    }

    private static BufferedImage createBufferedImage(final int width, final int height) {
        if (Utilities.isMac()) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        }

        final ColorModel model = colorModel(Transparency.TRANSLUCENT);
        final BufferedImage buffImage = new BufferedImage(
                model,
                model.createCompatibleWritableRaster(width, height),
                model.isAlphaPremultiplied(),
                null);

        return buffImage;
    }

    private static ColorModel colorModel(int transparency) {
        try {
            return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getColorModel(transparency);
        } catch (final HeadlessException he) {
            return ColorModel.getRGBdefault();
        }
    }
}
