package ru.maklas.genetics.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Consumer;

public class ImageAssets extends Asset{

    public TextureRegion empty;
    public TextureRegion cell;
    public TextureRegion laserStart, laserMiddle, laserEnd, laserEffect;
    public TextureRegion background;
    public BitmapFont font;
    public TextureRegion whiteBox10px;
    public TextureRegion whiteBox10pxHalfAlpha;

    @Override
    protected void loadImpl() {
        empty = new TextureRegion(new Texture("default.png"));
        cell = new TextureRegion(new Texture("cell.png"));
        background = new TextureRegion(new Texture("background.png"));

        laserStart = new TextureRegion(new Texture("laser/laser_start.png"));
        laserMiddle = new TextureRegion(new Texture("laser/laser_middle.png"));
        laserEnd = new TextureRegion(new Texture("laser/laser_end.png"));
        laserEffect = new TextureRegion(new Texture("laser/laser_effect.png"));

        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        whiteBox10px = createRectangleImage(10, 10, Color.WHITE);
        whiteBox10pxHalfAlpha = createRectangleImage(10, 10, new Color(1, 1, 1, 0.5f));
    }

    @Override
    protected void disposeImpl() {
        empty.getTexture().dispose();
        whiteBox10px.getTexture().dispose();
    }


    public static TextureRegion createCircleImage(int radius, Color color){
        int size = radius * 2 + 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillCircle(radius + 1, radius + 1, radius);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createCircleImageNoFill(int radius, Color color){
        int size = radius * 2 + 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.drawCircle(radius + 1, radius + 1, radius);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createImage(int width, int height, Color color, Consumer<Pixmap> pixmapConsumer){
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmapConsumer.accept(pixmap);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createRectangleImage(int width, int height, Color color){
        Pixmap pixmap = new Pixmap(width , height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        return new TextureRegion(new Texture(pixmap));
    }


    public static TextureRegion createRectangleImage(int width, int height, Color color, Color borderColor){
        Pixmap pixmap = new Pixmap(width + 2, height + 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(1, 1, width, height);
        pixmap.setColor(borderColor);
        pixmap.drawRectangle(1, 1, width,  height);
        return new TextureRegion(new Texture(pixmap));
    }
}
