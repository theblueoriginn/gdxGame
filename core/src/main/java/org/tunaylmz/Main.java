package org.tunaylmz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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

    float enemyTimer;
//collision detection
    Rectangle charRectangle;
    Rectangle enemyRectangle;
    private boolean destination = false;
    private Vector2 destPos;


    @Override
    public void create() {
        setScreen(new FirstScreen());
        charTexture = new Texture("char.png"  );
        batch = new SpriteBatch();
        viewport = new FitViewport(width, height);
        charSprite = new Sprite(charTexture);

        charSprite.setPosition(width/2,height/2);
        charSprite.setSize(128, 128);
        touchPos = new Vector2();

        enemySprites = new Array<>();

        charRectangle = new Rectangle();
        enemyRectangle = new Rectangle();

        createCursor();


    }
    public  void createCursor (){
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        Cursor cursor= Gdx.graphics.newCursor(pixmap,6,6);
        pixmap.dispose();
        Gdx.graphics.setCursor(cursor);
    }
    public void createEnemies(){
        float enemyWidth = 8;
        float enemyHeight = 8;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        enemyTexture = new Texture("bucket.png");
        // create the drop sprite
        Sprite enemySprite = new Sprite(enemyTexture);
        enemySprite.setSize(enemyWidth, enemyHeight);
        enemySprite.setX(MathUtils.random(0f,worldWidth - enemyWidth)  );
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
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            charSprite.translateY(speed * delta); // Move the bucket UP
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            charSprite.translateY(-speed * delta); // Move the bucket DOWN
        }

        //mouse controllers.

        touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        touchPos = viewport.unproject(touchPos);

        Vector2 charPos = new Vector2(charSprite.getX(),charSprite.getY());
        if (Gdx.input.isTouched()   &&  !charPos.equals(touchPos) ) { // If the user has clicked and is not in same pos as touchPos.
                destination = true;
                destPos = touchPos;

            }


    }

    private void logic() {

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float charWidth = charSprite.getWidth();
        float charHeight = charSprite.getHeight();

        charSprite.setX(MathUtils.clamp(charSprite.getX(), 0, worldWidth-charWidth));

        float delta = Gdx.graphics.getDeltaTime();
        charRectangle.set(charSprite.getX() ,charSprite.getY(),charWidth,charHeight);

        //destroy if out of bounds.
        for (int i = enemySprites.size - 1; i >= 0; i--) {
            Sprite enemySprite = enemySprites.get(i); // Get the sprite from the list
            float enemyWidth = enemySprite.getWidth();
            float enemyHeight = enemySprite.getHeight();

            enemySprite.translateY(-90f * delta);

            enemyRectangle.set(enemySprite.getX() , enemySprite.getY(),enemyWidth,enemyHeight);


            // if the top of the drop goes below the bottom of the view, remove it
            if (enemySprite.getY() < -enemyHeight) enemySprites.removeIndex(i);
            else if(enemyRectangle.overlaps(charRectangle) ){
                    enemySprites.removeIndex(i);
            }
        }


        enemyTimer += delta;
        if(enemyTimer > 1.f){
            createEnemies();
            enemyTimer = 0.f;
        }

        if(destination && destPos != null){
            Vector2 charPos = new Vector2(charSprite.getX(),charSprite.getY());
            viewport.unproject(charPos);
            if(destPos != charPos){

                charSprite.translate( (destPos.x  - charSprite.getX())* 2f* delta ,  (destPos.y  - charSprite.getY()) * 2f * delta     );

            }
            else{
                destination  = false;
                destPos = null;
            }
        }




    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }



}
