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
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation.ElasticOut;
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

		camera = new OrthographicCamera(1, h / w);
		batch = new ShapeRenderer();
		constants.update(camera);

		gameManager = new GameManager(constants.totalTiles);
	}

	@Override
	public void dispose() {
		batch.dispose();
		fontBatch.dispose();
	}

	Color background = new Color(0xFAF8EFFF);
	Color gameBackground = new Color(0xBBADA0FF);

	Constants constants = new Constants();
	private boolean touchdown;
	private Vector3 touchpos;
	
	boolean renderInput = false;
	float movementData = 0.0f;
	
	Input currentInput = Input.none;
	Input lastInput = Input.none;

	@Override
	public void render() {
		float diff = 0.04f;
		
		renderInput = false;
		movementData = 0.0f;
		
		if (Gdx.input.isTouched(0)) {
			if (touchdown == false) {
				touchdown = true;
				touchpos = getTouchPosScreen();
			}

			Vector3 newTouchPos = getTouchPosScreen();
			Vector3 dist = newTouchPos.sub(touchpos);
			dist.y = -dist.y;

			dist = dist.scl(1.0f / diff);
			
			float d = (float) (Math.sqrt(dist.x * dist.x + dist.y * dist.y) / 10.0f );

			int dir = Maths.SubClassify(dist.x, dist.y, true);
			
			switchToInput(dir);
			
			if( lastInput != currentInput ) {
				
				if( gameManager.isGameTerminated() == false ) {
					if( currentInput == Input.left || currentInput == Input.right || currentInput == Input.up || currentInput == Input.down) {
						if( gameManager.canMove(currentInput) == false ) {
							// can't move
							Gdx.app.log("VIBRATE", "Can't move");
						}
					}
				}
			}
			
			lastInput = currentInput;

			if( currentInput != Input.tap &&  currentInput != Input.none && currentInput != Input.blocked) {
				gameManager.setupMovement(currentInput);
				movementData = d;
				renderInput = true;
			}
		} else {
			if (touchdown) {
				touchdown = false;
				Vector3 newTouchPos = getTouchPosScreen();
				Vector3 dist = newTouchPos.sub(touchpos);
				dist.y = -dist.y;
				float d = dist.len();

				dist = dist.scl(1.0f / diff);

				int dir = Maths.Classify(dist.x, dist.y);
				switchToInput(dir);
				
				if( gameManager.isGameTerminated() == false ) {
					switch (currentInput) {
					case left:
						gameManager.move(Input.left);
						break;
					case right:
						gameManager.move(Input.right);
						break;
					case up:
						gameManager.move(Input.up);
						break;
					case down:
						gameManager.move(Input.down);
						break;
					default:
						break;
					}
					
					if ( gameManager.isGameTerminated() ) {
						Gdx.app.log("VIBRATE", "Game is terminated");
					}
				}
				else {
					if ( currentInput == Input.tap ) {
						Gdx.app.log("VIBRATE", "New game");
					}
				}
				currentInput = Input.none;
			}
		}
		
		
		Gdx.gl.glClearColor(background.r, background.g, background.b,
				background.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gameManager.update(Gdx.graphics.getDeltaTime());

		batch.setProjectionMatrix(camera.combined);
		float aspect = constants.h / constants.w;
		Matrix4 normalProjection = new Matrix4().setToOrtho2D(
				-Gdx.graphics.getWidth() / 2, -(Gdx.graphics.getHeight()
						* aspect / 2), Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight() * aspect);
		fontBatch.setProjectionMatrix(normalProjection);
		// fontBatch.setProjectionMatrix(camera.combined);
		batch.begin(ShapeType.Filled);

		constants.update(camera);

		batch.setColor(gameBackground);
		batch.rect(constants.boardx, constants.boardy, constants.size,
				constants.size);

		for (int x = 0; x < constants.totalTiles; ++x) {
			for (int y = 0; y < constants.totalTiles; ++y) {
				drawTile(x, y, 0, 1.0f, 1.0f);
			}
		}

		gameManager.getActuator().getGrid().eachCell(new CellCallBack() {
			@Override
			public void onCell(int x, int y, Tile tile) {
				if (tile != null) {
					float tx = x;
					float ty = y;
					float a = 1.0f;
					if( renderInput && tile.targetPosition != null) {
						tx = Quart.easeOut( movementData, tx, tile.targetPosition.getX()-x, 1);
						ty = Quart.easeOut( movementData, ty, tile.targetPosition.getY()-y, 1);
						a = tile.targetAlpha;
					}
					drawTile(tx, ty, tile.getValue(), a, 
							Elastic.easeOut(tile.getWobbleTimer(), 0, 1, 1));
				}
			}
		});
		batch.end();

		fontBatch.begin();
		gameManager.getActuator().getGrid().eachCell(new CellCallBack() {
			@Override
			public void onCell(int x, int y, Tile tile) {
				if (tile != null) {
					float tx = x;
					float ty = y;
					float a = 1.0f;
					if( renderInput && tile.targetPosition != null ) {
						tx = Quart.easeOut( movementData, tx, tile.targetPosition.getX()-x, 1);
						ty = Quart.easeOut( movementData, ty, tile.targetPosition.getY()-y, 1);
						a = tile.targetAlpha;
					}
					drawText(tx, ty, tile.getValue(), a);
				}
			}
		});
		fontBatch.end();
	}

	private void switchToInput(int dir) {
		// ignore invalid input suggestions
		if( dir == 7 || dir == 9 || dir == 1 || dir == 3) return;
		
		if( currentInput == Input.none ) {
			if( dir == 5 ) {
				currentInput = Input.tap;
				return;
			}
		}
		
		if( currentInput != Input.blocked ) {
			Input suggestedInput = Input.none;
			switch(dir) {
			case 4:
				suggestedInput = Input.left;
				break;
			case 5:
				suggestedInput = Input.none;
				break;
			case 6:
				suggestedInput = Input.right;
				break;
			case 8:
				suggestedInput = Input.up;
				break;
			case 2:
				suggestedInput = Input.down;
				break;
			default:
				suggestedInput = Input.blocked;
				break;
			}
			
			if( currentInput == suggestedInput ) {
				return;
			}
			
			if( currentInput == Input.tap && suggestedInput == Input.none ) {
				return;
			}
			
			if( currentInput == Input.none || currentInput == Input.tap ) {
				currentInput = suggestedInput;
				return;
			}

			currentInput = Input.blocked;
		}
	}

	private Vector3 getTouchPosScreen() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
		camera.unproject(touchPos);
		return touchPos;
	}

	private void drawTile(float x, float y, int value, float alpha, float size) {
		DoubleColor dc = DoubleColor.FromValue(value);
		Color c = dc.background;
		batch.setColor(c.r, c. g, c.b, alpha);

		float s = constants.tileSize * size;
		float s2 = s / 2 - constants.tileSize / 2;
		
		batch.rect(constants.boardx + constants.spacing
				+ (constants.tileSize + constants.spacing) * x - s2,
				constants.boardy + constants.spacing
						+ (constants.tileSize + constants.spacing) * y - s2, s,
				s);
	}

	private void drawText(float x, float y, int value, float alpha) {
		String v = Integer.toString(value);
		DoubleColor dc = DoubleColor.FromValue(value);
		font.setScale(2);
		TextBounds bounds = font.getBounds(v);
		Color c = dc.font;
		fontBatch.setColor(c.r, c. g, c.b, alpha);
		float xbase = constants.boardx + constants.spacing
				+ (constants.tileSize + constants.spacing) * x
				+ constants.tileSize / 2;
		float ybase = constants.boardy + constants.spacing
				+ (constants.tileSize + constants.spacing) * y
				+ constants.tileSize / 2;
		font.draw(fontBatch, v, xbase * Gdx.graphics.getWidth() - bounds.width
				/ 2, ybase * Gdx.graphics.getHeight() + bounds.height / 2);
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
