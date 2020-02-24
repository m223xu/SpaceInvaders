import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


class Const {
	static final float Screen_Width = 720;
	static final float Screen_Height = 480;
	static final float Space_Invaders_Size = 300;
	static final float Layout_Space = 40;
	static final float Bullet_Width = 4;
	static final float Bullet_Height = 12;
	static final float Ship_Width = 50;
	static final float Ship_Height = 30;
	static final float Alien_Width = 30;
	static final float Alien_Height = 20;
	static final int Row = 5;
	static final int Column = 10;
	static final float Alien_Group_Width = Alien_Width * Column;
	static final float Alien_Group_Height = Alien_Height * Row;
	static final float Alien_Down_Step = 20;
	static final int Lives = 2;
	static final float Player_Bullet_Speed = 5.0f;
	static final float Enemy_Bullet_Speed = 3.0f;
	static final int Enemy_Shoot_Interval = 120;
	static final float Player_Speed = 5.0f;
	static final float Enemy_Speed = 2.0f;
	static final float Enemy_Acceleration = 0.06f;
}


enum Direction {
	UP,
	DOWN,
	LEFT,
	RIGHT
}

abstract class Alien {
	int rn; // 0 ~ 4
	int cn; // 0 ~ 9
	boolean alive;

	float x;
	float y;
	float left;
	float right;
	float top;
	float bottom;

	Image image;
	ImageView imageView;

	Alien(int rn, int cn, boolean alive,
		  Image image) {
		this.rn = rn;
		this.cn = cn;
		this.alive = alive;

		this.x = (float) (Const.Screen_Width/2 - Const.Alien_Group_Width/2 + (0.5+cn)* Const.Alien_Width);
		this.y = (float) ((0.5+rn)*Const.Alien_Height);
		this.left = x-Const.Alien_Width/2;
		this.right = x+Const.Alien_Width/2;
		this.top = y-Const.Alien_Height/2;
		this.bottom = y+Const.Alien_Height/2;
		this.image = image;
		this.imageView = new ImageView(image);

		imageView.setLayoutX(left);
		imageView.setLayoutY(top);
	}

	void move(Direction direction, float speed) {
		switch (direction) {
			case LEFT:
				x -= speed;
				left -= speed;
				right -= speed;
				break;
			case RIGHT:
				x += speed;
				left += speed;
				right += speed;
				break;
			case DOWN:
				y += Const.Alien_Down_Step;
				top += Const.Alien_Down_Step;
				bottom += Const.Alien_Down_Step;
				break;
		}
		imageView.setLayoutX(left);
		imageView.setLayoutY(top);
	}

	abstract EnemyBullet shoot ();

	float getLeft() {
		return left;
	}
	float getRight() {
		return right;
	}
	float getTop() {
		return top;
	}
	float getBottom () {
		return bottom;
	}

	ImageView getImageView() {
		return imageView;
	}

	int getRn() {
		return rn;
	}


	boolean getAlive() {
		return alive;
	}

	void setAlive(boolean alive) {
		this.alive = alive;
	}
}

class Alien1 extends Alien {

	Alien1(int rn, int cn, boolean alive) {
		super(rn,cn,alive,
				new Image ("images/enemy1.png", Const.Alien_Width,Const.Alien_Height,false,false));
	}

	@Override
	EnemyBullet shoot() {
		MediaPlayer shootMediaPlayer =
				new MediaPlayer(
						new Media("file:/Users/mingjian/Desktop/CS349/assignments/a2/src/sounds/fastinvader1.wav")
				);
		shootMediaPlayer.play();
		return new Alien1Bullet(Direction.DOWN,x,y);
	}

	public ImageView getImageView() {
		return imageView;
	}
}

class Alien2 extends Alien {

	Alien2(int rn, int cn, boolean alive) {
		super(rn,cn,alive,
				new Image ("images/enemy2.png", Const.Alien_Width,Const.Alien_Height,false,false));
	}

	@Override
	EnemyBullet shoot() {
		MediaPlayer shootMediaPlayer =
				new MediaPlayer(
						new Media("file:/Users/mingjian/Desktop/CS349/assignments/a2/src/sounds/fastinvader2.wav")
				);
		shootMediaPlayer.play();
		return new Alien2Bullet(Direction.DOWN,x,y);
	}

	public ImageView getImageView() {
		return imageView;
	}
}

class Alien3 extends Alien {

	Alien3(int rn, int cn, boolean alive) {
		super(rn,cn,alive,
				new Image ("images/enemy3.png", Const.Alien_Width,Const.Alien_Height,false,false));
	}

	@Override
	EnemyBullet shoot() {
		MediaPlayer shootMediaPlayer =
				new MediaPlayer(
						new Media("file:/Users/mingjian/Desktop/CS349/assignments/a2/src/sounds/fastinvader3.wav")
				);
		shootMediaPlayer.play();
		return new Alien3Bullet(Direction.DOWN,x,y);
	}

	public ImageView getImageView() {
		return imageView;
	}
}

class AlienGroup {
	ArrayList<ArrayList<Alien>> aliens;

	float left;
	float right;
	float top;
	float bottom;
	Direction direction;
	float speed;
	int size;

	AlienGroup(int gameLevel) {
		this.left = Const.Screen_Width/2-Const.Alien_Group_Width/2;
		this.right = Const.Screen_Width/2+Const.Alien_Group_Width/2;
		this.top = 0;
		this.bottom = Const.Alien_Group_Height;
		this.direction = Direction.LEFT;
		this.speed = (float) (Const.Enemy_Speed*(0.8+0.2*gameLevel));
		size = Const.Row * Const.Column;

		aliens = new ArrayList<>();

		ArrayList<Alien> row = new ArrayList<>();
		for (int i = 0; i < Const.Column; i++) {
			row.add(new Alien3(0,i,true));
		}
		aliens.add(row);

		row = new ArrayList<>();
		for (int i = 0; i < Const.Column; i++) {
			row.add(new Alien2(1,i,true));
		}
		aliens.add(row);
		row = new ArrayList<>();
		for (int i = 0; i < Const.Column; i++) {
			row.add(new Alien2(2,i,true));
		}
		aliens.add(row);

		row = new ArrayList<>();
		for (int i = 0; i < Const.Column; i++) {
			row.add(new Alien1(3,i,true));
		}
		aliens.add(row);
		row = new ArrayList<>();
		for (int i = 0; i < Const.Column; i++) {
			row.add(new Alien1(4,i,true));
		}
		aliens.add(row);

	}

	void move_helper(Direction direction) {
		for (var row : aliens) {
			for (var alien : row) {
				alien.move(direction,speed);
			}
		}
	}

	boolean move() {
		if (bottom >= Const.Screen_Height) return false; //game over
		switch (this.direction) {
			case LEFT:
				if (left <= 0) {
					move_helper(Direction.DOWN);
					direction = Direction.RIGHT;
					top += Const.Alien_Down_Step;
					bottom += Const.Alien_Down_Step;
				}
				else {
					move_helper(Direction.LEFT);
					left -= speed;
					right -= speed;
				}
				break;
			case RIGHT:
				if (right >= Const.Screen_Width) {
					move_helper(Direction.DOWN);
					direction = Direction.LEFT;
					top += Const.Alien_Down_Step;
					bottom += Const.Alien_Down_Step;
				}
				else {
					move_helper(Direction.RIGHT);
					left += speed;
					right += speed;
				}
				break;
		}
		return true;
	}

	EnemyBullet shoot() {
		int rand = (int) (Math.random() * size);
		//System.out.println(rand);
		for (ArrayList<Alien> row : aliens) {
			for (Alien alien : row) {
				if (rand == 0 && alien.alive) {
					return alien.shoot();
				}
				if (alien.alive) {
					rand--;
				}
			}
		}
		return null; //silence error
	}

	void increaseSpeed() {
		speed += Const.Enemy_Acceleration;
	}

	void remove(Alien alien) {
		size--;
		alien.setAlive(false);
	}

	void updateBound() {
		left = Const.Screen_Width;
		right = 0;
		top = Const.Screen_Height;
		bottom = 0;

		for (var row : aliens) {
			for (var alien : row) {
				if (alien.alive) {
					left = Math.min(left, alien.getLeft());
					right = Math.max(right, alien.getRight());
					top = Math.min(top, alien.getTop());
					bottom = Math.max(bottom,alien.getBottom());
				}
			}
		}
	}

	int getSize() {
		return size;
	}
}

class Ship {
	float x;
	float y;
	float left;
	float right;
	float top;
	float bottom;
	float speed;
	Image image;
	ImageView imageView;

	Ship() {
		x = Const.Screen_Width/2;
		y = Const.Screen_Height-Const.Ship_Height/2;
		image = new Image("images/player.png",Const.Ship_Width,Const.Ship_Height,false,false);
		imageView = new ImageView(image);
		left = x - Const.Ship_Width/2;
		right = x + Const.Ship_Width/2;
		top = y - Const.Ship_Height/2;
		bottom = y + Const.Ship_Height/2;
		speed = Const.Player_Speed;
		imageView.setLayoutX(left);
		imageView.setLayoutY(top);
	}

	PlayerBullet shoot() {
		MediaPlayer shootMediaPlayer =
				new MediaPlayer(
						new Media("file:/Users/mingjian/Desktop/CS349/assignments/a2/src/sounds/shoot.wav")
				);
		shootMediaPlayer.play();
		return new PlayerBullet(Direction.UP,x,y);
	}

	void respawn() {
		x = Const.Screen_Width/2;
		y = Const.Screen_Height-Const.Ship_Height/2;
		left = x - Const.Ship_Width/2;
		right = x + Const.Ship_Width/2;
		top = y - Const.Ship_Height/2;
		bottom = y + Const.Ship_Height/2;
		imageView.setLayoutX(left);
		imageView.setLayoutY(top);
	}

	void move(Direction direction) {
		switch (direction) {
			case LEFT:
				if (left >= speed) {
					left -= speed;
					x -= speed;
					right -= speed;
				}
				break;
			case RIGHT:
				if (right + speed <= Const.Screen_Width) {
					right += speed;
					x += speed;
					left += speed;
				}
				break;
		}
		imageView.setLayoutX(left);
		imageView.setLayoutY(top);
	}

	ImageView getImageView() {
		return imageView;
	}

	public float getLeft() {
		return left;
	}

	public float getRight() {
		return right;
	}

	public float getTop() {
		return top;
	}

	public float getBottom() {
		return bottom;
	}
}

abstract class Bullet {
	Direction direction;
	float speed;
	float x;
	float y;
	float left;
	float right;
	float top;
	float bottom;
	Image image;
	ImageView imageView;

	Bullet(Direction direction, float speed, float x, float y, Image image) {
		this.direction = direction;
		this.speed = speed;
		this.x = x;
		this.y = y;
		this.image = image;
		this.imageView = new ImageView(image);
		left = x - Const.Bullet_Width/2;
		right = x + Const.Bullet_Width/2;
		top = y - Const.Bullet_Height/2;
		bottom = y + Const.Bullet_Height/2;
		imageView.setLayoutX(left);
		imageView.setLayoutY(top);
	}

	boolean move() {
		switch (direction) {
			case UP:
				y -= speed;
				top -= speed;
				bottom -= speed;
				break;
			case DOWN:
				y += speed;
				top += speed;
				bottom += speed;
				break;
			case LEFT:
				x -= speed;
				left -= speed;
				right -= speed;
				break;
			case RIGHT:
				x += speed;
				left += speed;
				right += speed;
				break;
		}
		imageView.setLayoutX(left);
		imageView.setLayoutY(top);
		return right >= 0 && left <= Const.Screen_Width && bottom >= 0 && top <= Const.Screen_Height;
	}

	public ImageView getImageView() {
		return imageView;
	}
}

class PlayerBullet extends Bullet {

	PlayerBullet(Direction direction, float x, float y) {
		super(direction, Const.Player_Bullet_Speed, x, y,
				new Image("images/player_bullet.png", Const.Bullet_Width, Const.Bullet_Height, false, false));
	}

	// return hitted alien if hit, otherwise null
	Alien hitCheck(AlienGroup alienGroup) {
		for (int i = 4; i >= 0; i--) {
			for (Alien alien : alienGroup.aliens.get(i)) {
				if (alien.getAlive() && alien.getLeft() <= x && x <= alien.getRight()
						&& alien.getTop() <= y && y <= alien.getBottom()) {
					return alien;
				}
			}
		}
		return null;
	}
}

abstract class EnemyBullet extends Bullet{

	EnemyBullet(Direction direction, float speed, float x, float y, Image image) {
		super (direction,speed,x,y,image);
	}

	boolean hitCheck(Ship ship) {
		return ship.getLeft() <= x && x <= ship.getRight() && ship.getTop() <= y && y <= ship.getBottom();
	}
}

class Alien1Bullet extends EnemyBullet {

	Alien1Bullet(Direction direction, float x, float y) {
		super(direction,Const.Enemy_Bullet_Speed,x,y,
				new Image("images/bullet1.png", Const.Bullet_Width, Const.Bullet_Height, false, false));
	}
}


class Alien2Bullet extends EnemyBullet {

	Alien2Bullet(Direction direction, float x, float y) {
		super(direction,Const.Enemy_Bullet_Speed,x,y,
				new Image("images/bullet2.png", Const.Bullet_Width, Const.Bullet_Height, false, false));
	}
}

class Alien3Bullet extends EnemyBullet {

	Alien3Bullet(Direction direction, float x, float y) {
		super(direction,Const.Enemy_Bullet_Speed,x,y,
				new Image("images/bullet3.png", Const.Bullet_Width, Const.Bullet_Height, false, false));
	}
}

class Menu {
	Stage stage;
	Image spaceInvaders;
	ImageView spaceInvadersView;
	Label title;
	Label space;
	Label body1;
	Label body2;
	Label body3;
	Label body4;
	Label body5;
	Label body6;
	Label name;
	VBox layout;
	Scene menu;

	Menu(Stage stage) {
		this.stage = stage;
	}

	public void gameMenu() {
		spaceInvaders = new Image("images/logo.png",
				Const.Space_Invaders_Size,
				Const.Space_Invaders_Size,
				true,
				true);
		spaceInvadersView = new ImageView(spaceInvaders);

		title = new Label("Instructions\n");
		title.setFont(Font.font("Arial", FontWeight.BOLD,40));
		space = new Label("\n");

		body1 = new Label("ENTER - Start Game\n");
		body2 = new Label("A, D - Move ship left or right\n");
		body3 = new Label("SPACE - Fire!\n");
		body4 = new Label("Q - Quit Game\n");
		body5 = new Label("1 or 2 or 3 - Start Game at a specific level\n");
		body6 = new Label("C - Start Game in Cheat Mode (Invincible!!!)\n");


		VBox box = new VBox(title,space,body1,body2,body3,body4,body5,body6);
		box.setAlignment(Pos.CENTER);

		name = new Label("Mingjian Xu 20718007");
		name.setFont(Font.font(10));

		layout = new VBox(spaceInvadersView,box,name);
		layout.setSpacing(Const.Layout_Space);
		layout.setAlignment(Pos.CENTER);

		menu = new Scene(layout,Const.Screen_Width,Const.Screen_Height);
		EventHandler<KeyEvent> switchScene = keyEvent -> {
			GamePlay game;
			switch (keyEvent.getCode()) {
				case ENTER:
				case DIGIT1:
					game = new GamePlay(stage, false);
					game.level(1);
					break;
				case DIGIT2:
					game = new GamePlay(stage, false);
					game.level(2);
					break;
				case DIGIT3:
					game = new GamePlay(stage, false);
					game.level(3);
					break;
				case C:
					game = new GamePlay(stage, true);
					game.level(1);
					break;
				case Q:
					Quit.quit();
					break;
			}
		};
		menu.addEventHandler(KeyEvent.KEY_RELEASED,switchScene);
		stage.setScene(menu);
		stage.show();
	}
}

class GamePlay {
	Stage stage;
	Group group;
	Scene gameBoard;


	AlienGroup alienGroup;
	Ship ship;

	ArrayList<PlayerBullet> activePBullets; // for hit check
	ArrayList<EnemyBullet> activeEBullets;

	int curScore;
	int curLives;
	int curLevel;
	int curAccuracy;

	Label score;
	Label lives;
	Label level;
	Label accuracy;

	int playerShootFrequencyCounter;

	int totalShoot;
	int totalHit;

	Timer timer;
	AnimationTimer animationTimer;

	boolean cheat;


	GamePlay(Stage stage, boolean cheat) {
		this.stage = stage;
		curScore = 0;
		curLives = Const.Lives;
		curLevel = 0;
		playerShootFrequencyCounter = 0;
		totalShoot = 0;
		totalHit = 0;
		this.cheat = cheat;
	}

	void setScore() {
		group.getChildren().remove(score);
		score = new Label("Score: " + curScore);
		score.setLayoutX(10);
		score.setLayoutY(10);
		group.getChildren().add(score);
	}

	void setLives() {
		group.getChildren().remove(lives);
		lives = new Label("Lives: " + curLives);
		lives.setLayoutX(600);
		lives.setLayoutY(10);
		group.getChildren().add(lives);
	}

	void setLevel() {
		group.getChildren().remove(level);
		level = new Label("Level: " + curLevel);
		level.setLayoutX(660);
		level.setLayoutY(10);
		group.getChildren().add(level);
	}

	void setAccuracy() {
		group.getChildren().remove(accuracy);
		curAccuracy = 0;
		if (totalShoot > 0) {
			curAccuracy = (int) ((double)totalHit/(double)totalShoot*100);
		}

		accuracy = new Label("Weapon Accuracy: " + curAccuracy + "%");
		accuracy.setLayoutX(90);
		accuracy.setLayoutY(10);
		group.getChildren().add(accuracy);
	}

	public void level(int gameLevel) {
		group = new Group();
		gameBoard = new Scene(group,Const.Screen_Width,Const.Screen_Height);
		stage.setScene(gameBoard);

		curLevel = gameLevel;

		alienGroup = new AlienGroup(curLevel);
		ship = new Ship();

		activePBullets = new ArrayList<>();
		activeEBullets = new ArrayList<>();

		setScore();
		setLives();
		setLevel();
		setAccuracy();

		group.getChildren().add(ship.getImageView());
		for (var row : alienGroup.aliens) {
			for (var alien : row) {
				group.getChildren().add(alien.getImageView());
			}
		}

		timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (playerShootFrequencyCounter > 0) playerShootFrequencyCounter--;
				//System.out.println(activeEBullets.size());  // for debug
				//System.out.println(activePBullets.size());
			}
		};
		timer.schedule(task,0,500);


		EventHandler<KeyEvent> control = keyEvent -> {
			switch (keyEvent.getCode()) {
				case SPACE:
					if (playerShootFrequencyCounter < 2) {
						playerShootFrequencyCounter++;
						PlayerBullet bullet = ship.shoot();
						totalShoot++;
						setAccuracy();
						activePBullets.add(bullet);
						group.getChildren().add(bullet.getImageView());
					}
					break;
				case Q:
					Quit.quit();
					break;
				case A:
					ship.move(Direction.LEFT);
					break;
				case D:
					ship.move(Direction.RIGHT);
					break;
			}
		};
		gameBoard.addEventHandler(KeyEvent.KEY_PRESSED,control);

		animationTimer = new AnimationTimer() {
			int enemyShootCounter = 0;
			@Override
			public void handle(long l) {
				enemyShootCounter++;
				if (enemyShootCounter >= Const.Enemy_Shoot_Interval*(1-curLevel*0.2)) {
					enemyShootCounter = 0;
					EnemyBullet bullet = alienGroup.shoot();
					activeEBullets.add(bullet);
					group.getChildren().add(bullet.getImageView());
				}
				handle_animation(group);
			}
		};
		animationTimer.start();
	}

	public void handle_animation(Group group) {
		for (var playerBullet : activePBullets) {
			if (!playerBullet.move()) {
				activePBullets.remove(playerBullet);
				group.getChildren().remove(playerBullet.getImageView());
				break;
			}
		}
		for (var enemyBullet : activeEBullets) {
			if (!enemyBullet.move()) {
				activeEBullets.remove(enemyBullet);
				group.getChildren().remove(enemyBullet.getImageView());
				break;
			}
		}
		if (!alienGroup.move()) {
			animationTimer.stop();
			timer.cancel();
			EndGame endGame = new EndGame(stage,cheat,curScore,curAccuracy);
			endGame.lose();
			return;
		}
		// two hit checks

		for (var bullet : activePBullets) {
			Alien alien = bullet.hitCheck(alienGroup);

			if (alien !=  null) {
				// remove alien and image, remove bullet and image, increase speed and score
				int rn = alien.getRn();
				alienGroup.remove(alien);
				activePBullets.remove(bullet);
				group.getChildren().remove(bullet.getImageView());
				group.getChildren().remove(alien.getImageView());
				alienGroup.increaseSpeed();
				int points = 30 - (rn+1) / 2 * 10;
				curScore += points;
				setScore();
				totalHit++;
				setAccuracy();
				// update bound
				alienGroup.updateBound();
				// insert sound
				MediaPlayer shootMediaPlayer =
						new MediaPlayer(
								new Media("file:/Users/mingjian/Desktop/CS349/" +
										"assignments/a2/src/sounds/invaderkilled.wav")
						);
				shootMediaPlayer.play();
				break;
			}
		}
		if (alienGroup.getSize() == 0) {
			animationTimer.stop();
			timer.cancel();
			if (curLevel == 3) {
				EndGame endGame = new EndGame(stage,cheat,curScore,curAccuracy);
				endGame.win();
				return;
			}
			else {
				level(curLevel+1);
			}
		}

		if (cheat) {
			return;
		}

		for (var bullet : activeEBullets) {
			if (bullet.hitCheck(ship)) {
				MediaPlayer shootMediaPlayer =
						new MediaPlayer(
								new Media("file:/Users/mingjian/Desktop/CS349/" +
										"assignments/a2/src/sounds/explosion.wav")
						);
				shootMediaPlayer.play();
				if (curLives > 0) {
					curLives--;
					ship.respawn();
					setLives();
				}
				else {
					animationTimer.stop();
					timer.cancel();
					EndGame endGame = new EndGame(stage,cheat,curScore,curAccuracy);
					endGame.lose();
				}
				return;
			}
		}

	}
}

class EndGame {
	Stage stage;
	boolean cheat;
	int score;
	int accuracy;

	EndGame(Stage stage, boolean cheat, int score, int accuracy) {
		this.stage = stage;
		this.cheat = cheat;
		this.score = score;
		this.accuracy = accuracy;
	}

	public void lose() {
		Label gg1;
		Label gg2;
		if (!cheat) {
			gg1 = new Label("Game Over!!!\n");
			gg2 = new Label("I recommend you cheating.\n");
		}
		else {
			gg1 = new Label("You lose even though cheating!\n");
			gg2 = new Label("I suggest playing other games.\n");
		}
		gg1.setFont(Font.font("Arial", FontWeight.MEDIUM, 30));
		gg2.setFont(Font.font("Arial", FontWeight.MEDIUM,30));

		Label score = new Label("You scored " + this.score + " with "
				+ accuracy + "% weapon accuracy.");

		Label instruction = new Label("R - Restart in normal mode.\n" +
				"Q - Quit.\n" +
				"M - Back to menu.\n" +
				"C - I want to cheat...");

		VBox vbox = new VBox(gg1,gg2,score,instruction);
		vbox.setSpacing(Const.Layout_Space);
		vbox.setAlignment(Pos.CENTER);
		Scene loseScreen = new Scene(vbox,Const.Screen_Width,Const.Screen_Height);
		EventHandler<KeyEvent> switchScene = keyEvent -> {
			GamePlay game;
			switch (keyEvent.getCode()) {
				case R:
					game = new GamePlay(stage, false);
					game.level(1);
					break;
				case Q:
					Quit.quit();
					break;
				case M:
					Menu menu = new Menu(stage);
					menu.gameMenu();
					break;
				case C:
					game = new GamePlay(stage, true);
					game.level(1);
					break;
			}
		};
		loseScreen.addEventHandler(KeyEvent.KEY_RELEASED,switchScene);

		stage.setScene(loseScreen);
	}

	public void win() {
		Label congrat = new Label("Congratulations!!!");
		Label ggez = new Label("You win!!!");
		ggez.setFont(Font.font("Arial", FontWeight.BOLD, 40));

		Label score = new Label("You scored " + this.score + " with "
				+ accuracy + "% weapon accuracy.");

		Label instruction = new Label("R - Restart in normal mode.\n" +
				"Q - Quit.\n" +
				"M - Back to menu.\n" +
				"C - I want to cheat...");

		VBox vbox = new VBox(congrat,ggez,score,instruction);
		vbox.setSpacing(Const.Layout_Space);
		vbox.setAlignment(Pos.CENTER);
		Scene winScreen = new Scene(vbox,Const.Screen_Width,Const.Screen_Height);
		EventHandler<KeyEvent> switchScene = keyEvent -> {
			GamePlay game;
			switch (keyEvent.getCode()) {
				case R:
					game = new GamePlay(stage,false);
					game.level(1);
					break;
				case Q:
					Quit.quit();
					break;
				case M:
					Menu menu = new Menu(stage);
					menu.gameMenu();
					break;
				case C:
					game = new GamePlay(stage, true);
					game.level(1);
					break;
			}
		};
		winScreen.addEventHandler(KeyEvent.KEY_RELEASED,switchScene);

		stage.setScene(winScreen);
	}


}

class Quit {
	public static void quit() {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Confirm.");
		//window.setMinWidth(250);

		Label label = new Label("Are you sure to quit?");
		label.setWrapText(true);
		Button yesButton= new Button("Yes");
		yesButton.setOnAction(event -> {
			window.close();
			System.exit(0);
		});
		Button noButton= new Button("No");
		noButton.setOnAction(event -> window.close());

		VBox layout = new VBox();
		layout.getChildren().addAll(label, yesButton, noButton);
		layout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(layout, 250, 300);
		window.setScene(scene);
		window.showAndWait();
	}
}

public class SpaceInvaders extends Application {

	@Override
	public void start(Stage stage) {
		stage.setResizable(false);
		Menu menu = new Menu(stage);
		menu.gameMenu();
	}
}



