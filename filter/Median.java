/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package filter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.swing.ImageIcon;

/**
 *
 * @author forsca
 */
public class Median {    
    public BufferedImage medianfilter(BufferedImage sumber) {
        BufferedImage prosesGambar;
        BufferedImage loadIng = sumber;
        
        int ukuranX = loadIng.getWidth();
        int ukuranY = loadIng.getHeight();
        prosesGambar = new BufferedImage(ukuranX, ukuranY,
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = prosesGambar.getGraphics();
        g.drawImage(loadIng, 0, 0, null);
        WritableRaster raster = prosesGambar.getRaster();
        for (int x = 1; x < (ukuranX - 1); x++) {
            for (int y = 1; y < (ukuranY - 1); y++) {
                int rgb11 = loadIng.getRGB((x - 1), (y - 1));
                int[] p = new int[9];
                p[0] = (rgb11 >> 8) & 0xff;
                int rgb12 = loadIng.getRGB(x, (y - 1));
                p[1] = (rgb12 >> 8) & 0xff;
                int rgb13 = loadIng.getRGB((x + 1), (y - 1));
                p[2] = (rgb13 >> 8) & 0xff;
                int rgb21 = loadIng.getRGB((x - 1), (y));
                p[3] = (rgb21 >> 8) & 0xff;
                int rgb22 = loadIng.getRGB(x, y);
                p[4] = (rgb22 >> 8) & 0xff;
                int rgb23 = loadIng.getRGB((x + 1), y);
                p[5] = (rgb23 >> 8) & 0xff;
                int rgb31 = loadIng.getRGB((x - 1), (y + 1));
                p[6] = (rgb31 >> 8) & 0xff;
                int rgb32 = loadIng.getRGB(x, (y + 1));
                p[7] = (rgb32 >> 8) & 0xff;
                int rgb33 = loadIng.getRGB((x + 1), (y + 1));
                p[8] = (rgb33 >> 8) & 0xff;

                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (p[j] > p[j + 1]) {
                            int temp = p[j];
                            p[j] = p[j + 1];
                            p[j + 1] = temp;
                        }
                    }
                }
                raster.setSample(x, y, 0, p[6]);
            }
        }
        return prosesGambar;
    }
}
