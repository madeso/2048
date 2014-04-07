package com.madeso.slide2048;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Slide2048 implements ApplicationListener {
	private OrthographicCamera camera;
	private ShapeRenderer batch;
	
	SpriteBatch fontBatch;
    BitmapFont font;
	
	GameManager gameManager;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		

	    fontBatch = new SpriteBatch();
	    font = new BitmapFont();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new ShapeRenderer();
		constants.update(camera);
		
		gameManager = new GameManager(constants.totalTiles);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
	
	Color background = new Color(0xFAF8EFFF);
	Color gameBackground = new Color(0xBBADA0FF);
	
	Constants constants = new Constants();
	private boolean touchdown;
	private Vector3 touchpos;
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		// Matrix4 normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Matrix4 normalProjection = new Matrix4().setToOrtho2D(-Gdx.graphics.getWidth()/2, -Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		/*Matrix4 normalProjection = new Matrix4().setToOrtho2D(-Gdx.graphics.getWidth(), Gdx.graphics.getWidth(),
				-Gdx.graphics.getHeight(), Gdx.graphics.getHeight(),
				0, 1);*/
		fontBatch.setProjectionMatrix(normalProjection);
		batch.begin(ShapeType.Filled);
		
		constants.update(camera);
		
		batch.setColor(gameBackground);
		batch.rect(constants.boardx, constants.boardy, constants.size, constants.size);
		
		for(int x=0; x<constants.totalTiles; ++x) {
			for(int y=0; y<constants.totalTiles; ++y) {
				drawTile(x, y, 0);
			}
		}
		
		gameManager.getActuator().getGrid().eachCell(new CellCallBack() {
			@Override
			public void onCell(int x, int y, Tile tile) {
				if( tile != null ) {
					drawTile(x, y, tile.getValue() );
				}
			}
		} );
		batch.end();
		
		fontBatch.begin();
		gameManager.getActuator().getGrid().eachCell(new CellCallBack() {
			@Override
			public void onCell(int x, int y, Tile tile) {
				if( tile != null ) {
					drawText(x, y, tile.getValue() );
				}
			}
		} );
		batch.end();
        fontBatch.end();
		
		float diff = 0.04f;
		
		if (Gdx.input.isTouched(0)) {
			if (touchdown == false) {
				touchdown = true;
				touchpos = getTouchPosScreen();
			}

			Vector3 rtouchPos = new Vector3(touchpos);

			Vector3 newTouchPos = getTouchPosScreen();
			Vector3 dist = newTouchPos.sub(touchpos);
			dist.y = -dist.y;

			dist = dist.scl(1.0f / diff);

			int dir = Maths.Classify(dist.x, dist.y);
		
	} else {
		if (touchdown) {
			touchdown = false;
			Vector3 newTouchPos = getTouchPosScreen();
			Vector3 dist = newTouchPos.sub(touchpos);
			dist.y = -dist.y;
			float d = dist.len();

			dist = dist.scl(1.0f / diff);

			int dir = Maths.Classify(dist.x, dist.y);

			switch (dir) {
				case 5:
				//game.input(Game.Input.tap);
				break;
				case 4:
					gameManager.move(Input.left);
				break;
				case 6:
					gameManager.move(Input.right);
				break;
				case 8:
					gameManager.move(Input.up);
				break;
				case 2:
				gameManager.move(Input.down);
				break;
			}

			if (d > 1.0f) {
			}
		}
	}
	}
	
	private Vector3 getTouchPosScreen() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
		camera.unproject(touchPos);
		return touchPos;
	}
	
	private void drawTile(float x, float y, int value) {
		DoubleColor dc = DoubleColor.FromValue(value);
		batch.setColor( dc.background );
		
		batch.rect(constants.boardx + constants.spacing + (constants.tileSize + constants.spacing)*x,
				constants.boardy + constants.spacing + (constants.tileSize + constants.spacing)*y,
				constants.tileSize, constants.tileSize);
	}
	
	private void drawText(float x, float y, int value) {
		String v = Integer.toString(value);
		DoubleColor dc = DoubleColor.FromValue(value);
		font.setScale(2);
		float s = font.getScaleX();
		// fontBatch.setColor( dc.font );
		fontBatch.setColor(Color.BLUE);
		float xbase = constants.boardx + constants.spacing + (constants.tileSize + constants.spacing)*x;
		float ybase = constants.boardy + constants.spacing + (constants.tileSize + constants.spacing)*y;
        font.draw(fontBatch, v, xbase*Gdx.graphics.getWidth(), ybase*Gdx.graphics.getHeight());
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
