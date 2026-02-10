package org.tunaylmz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    Texture charTexture;
    SpriteBatch batch;
    Viewport viewport;

    Vector2 touchPos;

    float width = 800, height = 500;
    private Sprite charSprite;

    Array<Sprite> enemySprites;
    Texture enemyTexture;


    @Override
    public void create() {
        setScreen(new FirstScreen());
        charTexture = new Texture("char.png"  );
        batch = new SpriteBatch();
        viewport = new FitViewport(width, height);
        charSprite = new Sprite(charTexture);
        charSprite.setSize(128, 128);
        touchPos = new Vector2();

        enemySprites = new Array<>();

        createEnemies();

    }
    public void createEnemies(){
        float dropWidth = 128;
        float dropHeight = 128;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        enemyTexture = new Texture("bucket.png");
        // create the drop sprite
        Sprite enemySprite = new Sprite(enemyTexture);
        enemySprite.setSize(dropWidth, dropHeight);
        enemySprite.setX(0);
        enemySprite.setY(worldHeight);
        enemySprites.add(enemySprite);
    }

    public void pause(){

    }
    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        charSprite.draw(batch);

        for (Sprite enemySprite : enemySprites) {
            enemySprite.draw(batch);
        }

        batch.end();
    }
    @Override
    public void render() {
        // organize code into three methods
        input();
        logic();
        draw();
    }

    private void input() {
        float speed = 50f;
        float delta = Gdx.graphics.getDeltaTime(); // retrieve the current delta

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            charSprite.translateX(speed * delta); // Move the bucket right
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            charSprite.translateX(-speed * delta); // move the bucket left
        }
        if (Gdx.input.isTouched()) { // If the user has clicked or tapped the screen
            touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            charSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float charWidth = charSprite.getWidth();
        float charHeight = charSprite.getHeight();

        charSprite.setX(MathUtils.clamp(charSprite.getX(), 0, worldWidth-charWidth));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }



}
