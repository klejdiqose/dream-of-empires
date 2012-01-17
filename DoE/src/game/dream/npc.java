package game.dream;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.Animation;
import java.util.Random;

public class npc {
	
Vector2f position;
DoE mainGame;
int currentDirection = 0;

private Animation sprite, up, down, left, right;

	public npc(Vector2f position, DoE mainGame)
	{
		this.position = position;
		this.mainGame = mainGame;
		try {
			Image [] movementUp = {new Image("data/knt1_bk1.gif"), new Image("data/knt1_bk2.gif")};
			Image [] movementDown = {new Image("data/knt1_fr1.gif"), new Image("data/knt1_fr2.gif")};
			Image [] movementLeft = {new Image("data/knt1_lf1.gif"), new Image("data/knt1_lf2.gif")};
			Image [] movementRight = {new Image("data/knt1_rt1.gif"), new Image("data/knt1_rt2.gif")};
			int [] duration = {300, 300};
	    	up = new Animation(movementUp, duration, false);
	    	down = new Animation(movementDown, duration, false);
	    	left = new Animation(movementLeft, duration, false);
	    	right = new Animation(movementRight, duration, false); 
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	sprite = down;
	}
	public void render(){
		sprite.draw(DoE.screenWidth/2 - mainGame.playerPosition.x + position.x,DoE.screenHeight/2 - mainGame.playerPosition.y +  position.y);
	}
	public void update(int delta){
		
		int direction = 1;
		Random random = new Random();
		if(random.nextInt(1000) < 999)
		{
			direction = currentDirection;
		}else{
			direction = random.nextInt(4);
		}
		currentDirection = direction;
		
    	if (direction == 0)
    	{
    		if (!mainGame.isBlocked(position.x, position.y - delta * 0.1f))
    		{
	    	    sprite = up;
	    	    sprite.update(delta);
	    	    // The lower the delta the slowest the sprite will animate.
	    	    position.y -= delta * 0.1f;
    		}
    	}
    	else if (direction == 1)
    	{
    		if (!mainGame.isBlocked(position.x, position.y + delta * 0.1f))
    		{
	    	    sprite = down;
	    	    sprite.update(delta);
	    	    position.y += delta * 0.1f;
    		}
    	}
    	else if (direction == 2)
    	{
    		if (!mainGame.isBlocked(position.x  - delta * 0.1f, position.y))
    		{
	    	    sprite = left;
	    	    sprite.update(delta);
	    	    position.x -= delta * 0.1f;
    		}
    	}
    	else if (direction == 3)
    	{
    		if (!mainGame.isBlocked(position.x  + delta * 0.1f, position.y))
    		{
	    	    sprite = right;
	    	    sprite.update(delta);
	    	    position.x += delta * 0.1f;
    		}
    	}
		
	}
}
