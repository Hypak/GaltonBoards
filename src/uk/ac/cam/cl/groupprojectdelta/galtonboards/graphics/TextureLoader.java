package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {

    private byte[] data;
    private int width;
    private int height;
    ByteBuffer buf;

    private static Map<String, ByteBuffer> bufCache;
    private static Map<String, Integer> widthCache;

    public TextureLoader(String path) {
        try {
            bufCache = new HashMap<>();
            widthCache = new HashMap<>();

            loadTexture(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadTexture(String path) throws IOException {

        if (bufCache.containsKey(path)) {

            buf = bufCache.get(path);
            width = widthCache.get(path);
            height = (data.length / width) / 4;

        } else {
            File imageFile = new File(path);
            BufferedImage image = ImageIO.read(imageFile);
            Color c;

            width = image.getWidth();
            height = image.getHeight();

            data = new byte[width * height * 4];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    c = new Color(image.getRGB(x, y), true);
                    data[4 * (x + y * width)] = (byte) c.getRed();
                    data[4 * (x + y * width) + 1] = (byte) c.getGreen();
                    data[4 * (x + y * width) + 2] = (byte) c.getBlue();
                    data[4 * (x + y * width) + 3] = (byte) c.getAlpha();
                }
            }

            generateBuffer();

            bufCache.put(path, buf);
            widthCache.put(path, width);
        }
    }

    private void generateBuffer() {
        buf = BufferUtils.createByteBuffer(width*height*4);

        for (int i = width * height; i > 0 ; i-- ) {
            buf.put(data[4*i-4]);
            buf.put(data[4*i-3]);
            buf.put(data[4*i-2]);
            buf.put(data[4*i-1]);
        }

        buf.flip();
    }

    public ByteBuffer buffer() {
        return buf;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}