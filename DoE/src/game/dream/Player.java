package game.dream;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

public class Player {
	private Animation sprite, up, down, left, right;
    public boolean buildMode = false;
    public Vector2f position = new Vector2f(0f, 0f);
    public Item[] inventory = new Item[50];
    
	public Player(Vector2f position)
	{
		this.position = position;
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
	public void init(GameContainer container) throws SlickException
    {
    	
	
	inventory[0] = new Item("Pants", DoE.inventoryItem[0]);
	inventory[1] = new Item("Underpants", DoE.inventoryItem[3]);
    }
	
	public void update(GameContainer container, int delta) throws SlickException
    {
		Input input = container.getInput();
    	if (input.isKeyDown(Input.KEY_UP) ||input.isKeyDown(Input.KEY_W))
    	{
    		if (!DoE.mainGame.isBlocked(position.x, position.y - delta * 0.1f))
    		{
	    	    sprite = up;
	    	    sprite.update(delta);
	    	    // The lower the delta the slowest the sprite will animate.
	    	    position.y -= delta * 0.1f;
    		}
    	}
    	else if (input.isKeyDown(Input.KEY_DOWN) ||input.isKeyDown(Input.KEY_S))
    	{
    		if (!DoE.mainGame.isBlocked(position.x, position.y + delta * 0.1f))
    		{
	    	    sprite = down;
	    	    sprite.update(delta);
	    	    position.y += delta * 0.1f;
    		}
    	}
    	else if (input.isKeyDown(Input.KEY_LEFT) ||input.isKeyDown(Input.KEY_A))
    	{
    		if (!DoE.mainGame.isBlocked(position.x  - delta * 0.1f, position.y))
    		{
	    	    sprite = left;
	    	    sprite.update(delta);
	    	    position.x -= delta * 0.1f;
    		}
    	}
    	else if (input.isKeyDown(Input.KEY_RIGHT) ||input.isKeyDown(Input.KEY_D))
    	{
    		if (!DoE.mainGame.isBlocked(position.x  + delta * 0.1f, position.y))
    		{
	    	    sprite = right;
	    	    sprite.update(delta);
	    	    position.x += delta * 0.1f;
    		}
    	}
    	    	
    	if (input.isKeyPressed(Input.KEY_B))
    	{
    		buildMode = !buildMode;
    	}
    	if (input.isKeyPressed(Input.KEY_I))
    	{
    		if (DoE.gui.getRootPane() == DoE.inventoryMenu)
    		{ DoE.gui.setRootPane(DoE.root);
    			
    		}
    		else {
	            for (int i=0;i<DoE.inventoryItems;i++) {
	
	            	if(inventory[i] == null)
	            	{
	            		DoE.inventoryPanel.setSlot(i,null);
	            	} else {
	            		DoE.inventoryPanel.setSlot(i,inventory[i]);
	            	}
	            }
	        	DoE.gui.setRootPane(DoE.inventoryMenu);
    			}
    	}
    }
	 public void render(GameContainer container, Graphics g) throws SlickException
	    {
	sprite.draw(DoE.screenWidth / 2, DoE.screenHeight / 2);
	    }
}
