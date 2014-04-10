package com.madeso.slide2048;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	//// SOUNDS
	private Sound sndGameTerminated;
	private Sound sndNewGame;
	private Sound sndInputBlocked;
	private Sound sndCantMove;
	
	private Sound sndCreated;
	private Sound sndCombine4   ;
	private Sound sndCombine8   ;
	private Sound sndCombine16  ;
	private Sound sndCombine32  ;
	private Sound sndCombine64  ;
	private Sound sndCombine128 ;
	private Sound sndCombine256 ;
	private Sound sndCombine512 ;
	private Sound sndCombine1024;
	private Sound sndCombine2048;

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
		
		sndGameTerminated= Gdx.audio.newSound(Gdx.files.internal("data/terminated.wav"));
		sndNewGame= Gdx.audio.newSound(Gdx.files.internal("data/new-game.wav"));
		sndInputBlocked= Gdx.audio.newSound(Gdx.files.internal("data/input-blocked.wav"));
		sndCantMove= Gdx.audio.newSound(Gdx.files.internal("data/cant-move.wav"));
		sndCreated     = Gdx.audio.newSound(Gdx.files.internal("data/created.wav"));
		sndCombine4    = Gdx.audio.newSound(Gdx.files.internal("data/combine/4.wav"));
		sndCombine8    = Gdx.audio.newSound(Gdx.files.internal("data/combine/8.wav"));
		sndCombine16   = Gdx.audio.newSound(Gdx.files.internal("data/combine/16.wav"));
		sndCombine32   = Gdx.audio.newSound(Gdx.files.internal("data/combine/32.wav"));
		sndCombine64   = Gdx.audio.newSound(Gdx.files.internal("data/combine/64.wav"));
		sndCombine128  = Gdx.audio.newSound(Gdx.files.internal("data/combine/128.wav"));
		sndCombine256  = Gdx.audio.newSound(Gdx.files.internal("data/combine/256.wav"));
		sndCombine512  = Gdx.audio.newSound(Gdx.files.internal("data/combine/512.wav"));
		sndCombine1024 = Gdx.audio.newSound(Gdx.files.internal("data/combine/1024.wav"));
		sndCombine2048 = Gdx.audio.newSound(Gdx.files.internal("data/combine/2048.wav"));
	}

	@Override
	public void dispose() {
		batch.dispose();
		fontBatch.dispose();
		
		sndGameTerminated.dispose();
		sndNewGame.dispose();
		sndInputBlocked.dispose();
		sndCantMove.dispose();
		
		sndCreated.dispose();
		sndCombine4   .dispose();
		sndCombine8   .dispose();
		sndCombine16  .dispose();
		sndCombine32  .dispose();
		sndCombine64  .dispose();
		sndCombine128 .dispose();
		sndCombine256 .dispose();
		sndCombine512 .dispose();
		sndCombine1024.dispose();
		sndCombine2048.dispose();
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
					if( currentInput == Input.blocked ) {
						playSound(sndInputBlocked);
						Gdx.input.vibrate(500);
					}
					if( currentInput == Input.left || currentInput == Input.right || currentInput == Input.up || currentInput == Input.down) {
						if( gameManager.canMove(currentInput) == false ) {
							// can't move
							playSound(sndCantMove);
							Gdx.input.vibrate(200);
							
							gameManager.shake();
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
					
					int score = 0;
					
					if( currentInput == Input.left || currentInput == Input.right || currentInput == Input.up || currentInput == Input.down) {
						score = gameManager.move(currentInput);
					}
					
					if ( gameManager.isGameTerminated() ) {
						playSound(sndGameTerminated);
						Gdx.input.vibrate(1000);
					}
					else {
						Gdx.app.log("SOUND", String.format("Score: %d", score));
						
						if( score == 1 ) { playSound(sndCreated) ;}
						else if( score == 4 )    { playSound(sndCombine4    );}
						else if( score == 8 )    { playSound(sndCombine8    );}
						else if( score == 16 )   { playSound(sndCombine16   );}
						else if( score == 32 )   { playSound(sndCombine32   );}
						else if( score == 64 )   { playSound(sndCombine64   );}
						else if( score == 128 )  { playSound(sndCombine128  );}
						else if( score == 256 )  { playSound(sndCombine256  );}
						else if( score == 512 )  { playSound(sndCombine512  );}
						else if( score == 1024 ) { playSound(sndCombine1024 );}
						else if( score == 2048 ) { playSound(sndCombine2048 );}
					}
				}
				else {
					if ( currentInput == Input.tap ) {
						playSound(sndNewGame);
						gameManager.restart();
						Gdx.input.vibrate(100);
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
				drawTile(x, y, 0, 1.0f, 1.0f, 1, 0);
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
							Elastic.easeOut(tile.getWobbleTimer(), 0, 1, 1), tile.getShake(), tile.getShakeIntensity());
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

	private void playSound(Sound snd) {
		// TODO Auto-generated method stub
		snd.play();
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

	private void drawTile(float x, float y, int value, float alpha, float size, float shake, float shakeIntensity) {
		DoubleColor dc = DoubleColor.FromValue(value);
		Color c = dc.background;
		batch.setColor(c.r, c. g, c.b, alpha);

		float s = constants.tileSize * size;
		float s2 = s / 2 - constants.tileSize / 2;
		
		/*batch.rect(constants.boardx + constants.spacing
				+ (constants.tileSize + constants.spacing) * x - s2,
				constants.boardy + constants.spacing
						+ (constants.tileSize + constants.spacing) * y - s2, s,
				s);*/
		float xbase = constants.boardx + constants.spacing
				+ (constants.tileSize + constants.spacing) * x - s2;
		float ybase = constants.boardy + constants.spacing
				+ (constants.tileSize + constants.spacing) * y - s2;
		batch.rect(xbase, ybase, s,s, s/2, s/2, 20 * ShakeFunction(shake, 4)*shakeIntensity);
	}
	
	float ShakeFunction(float x, int times) {
		return (float) Math.cos(Math.PI * times * 2 * x) * (1-Quart.easeIn(x, 0, 1, 1));
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
