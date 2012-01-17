/* 
* To change this template, choose Tools | Templates 
* and open the template in the editor. 
*/ 
package game.dream;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.tiled.*;
import org.newdawn.slick.Image; 
import org.newdawn.slick.Animation;
import org.newdawn.slick.Input;
import de.matthiasmann.twl.*;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import de.matthiasmann.twl.Widget;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;


public class DoE extends BasicGame implements ComponentListener
{
	private TiledMap currentMap;
	private Animation sprite, up, down, left, right;
	private boolean[][] blocked;
	private static final int tileSize = 32;
	public static final int screenHeight = 600;
	public static final int screenWidth = 800;
	private TWLInputAdapter twlInputAdapter;
    private LWJGLRenderer lwjglRenderer;
    private ThemeManager theme;
    public static GUI gui;
    public static Widget root;
    public static GameMenu gameMenu;
    public static DoE mainGame;
    public boolean buildMode = false;
    public Vector2f playerPosition = new Vector2f(0f, 0f);
    public Item[] inventory;
    private final int buildButtons = 2;
    private MouseOverArea[] buildButton = new MouseOverArea[buildButtons];
    private int buildItem = 0;
    private npc dude;
    

    
    public DoE()
    {
        super("Dream of Empires");
    }

    public static void main(String[] arguments)
    {
        try
        {
        	mainGame = new DoE();
            AppGameContainer app = new AppGameContainer(mainGame);
            app.setDisplayMode(screenWidth, screenHeight, false);
            app.start();
            
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void init(GameContainer container) throws SlickException
    {
    	inventory = new Item[2];
    	inventory[0] = new Item("Pants", new Image("data/knt1_lf1.gif"));
    	inventory[1] = new Item("Underpants", new Image("data/knt1_rt1.gif"));    	
    	dude = new npc(new Vector2f(5f, 5f), mainGame);        
    	currentMap = new TiledMap("data/desert.tmx");
    	
    	Image [] movementUp = {new Image("data/knt1_bk1.gif"), new Image("data/knt1_bk2.gif")};
    	Image [] movementDown = {new Image("data/knt1_fr1.gif"), new Image("data/knt1_fr2.gif")};
    	Image [] movementLeft = {new Image("data/knt1_lf1.gif"), new Image("data/knt1_lf2.gif")};
    	Image [] movementRight = {new Image("data/knt1_rt1.gif"), new Image("data/knt1_rt2.gif")};
    	int [] duration = {300, 300};
    	
    	up = new Animation(movementUp, duration, false);
    	down = new Animation(movementDown, duration, false);
    	left = new Animation(movementLeft, duration, false);
    	right = new Animation(movementRight, duration, false); 
    	
    	sprite = down;
    	calculateBlocked();
    	
    	  // add build buttons
        for (int i=0;i<buildButtons;i++) {
            Image tile = null;
             
            switch(i)
            {
            case 0:
                tile = currentMap.getTileSet(0).tiles.getSubImage(7,3);
                break;
            case 1:
                tile = currentMap.getTileSet(0).tiles.getSubImage(2,3);
                break;               
            }
           
            buildButton[i] = new MouseOverArea(container, tile, 0, (i*tileSize), tileSize, tileSize, this);
				
            
            Sound click = new Sound("/data/click.wav");
            buildButton[i].setMouseDownSound(click);
        }
    	
    	 // construct & configure root widget
        root = new Widget();
        root.setSize(screenWidth, screenHeight);
        root.setTheme("");

        // create the popup menu
		gameMenu = new GameMenu();
		gameMenu.setTheme("");
		gameMenu.setPosition(screenWidth/2, screenHeight/2);


        // save Slick's GL state while loading the theme
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            lwjglRenderer = new LWJGLRenderer();
            theme = ThemeManager.createThemeManager(this.getClass().getResource("simple.xml"), lwjglRenderer);
            gui = new GUI(root, lwjglRenderer);
            gui.applyTheme(theme);
        } catch (LWJGLException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            // restore Slick's GL state
            GL11.glPopAttrib();
        }

        // connect input
        twlInputAdapter = new TWLInputAdapter(gui, container.getInput());
        container.getInput().addPrimaryListener(twlInputAdapter);
        
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException
    {
    	dude.update(delta);
    	twlInputAdapter.update();
    	Input input = container.getInput();
    	if (input.isKeyDown(Input.KEY_UP) ||input.isKeyDown(Input.KEY_W))
    	{
    		if (!isBlocked(playerPosition.x, playerPosition.y - delta * 0.1f))
    		{
	    	    sprite = up;
	    	    sprite.update(delta);
	    	    // The lower the delta the slowest the sprite will animate.
	    	    playerPosition.y -= delta * 0.1f;
    		}
    	}
    	else if (input.isKeyDown(Input.KEY_DOWN) ||input.isKeyDown(Input.KEY_S))
    	{
    		if (!isBlocked(playerPosition.x, playerPosition.y + delta * 0.1f))
    		{
	    	    sprite = down;
	    	    sprite.update(delta);
	    	    playerPosition.y += delta * 0.1f;
    		}
    	}
    	else if (input.isKeyDown(Input.KEY_LEFT) ||input.isKeyDown(Input.KEY_A))
    	{
    		if (!isBlocked(playerPosition.x  - delta * 0.1f, playerPosition.y))
    		{
	    	    sprite = left;
	    	    sprite.update(delta);
	    	    playerPosition.x -= delta * 0.1f;
    		}
    	}
    	else if (input.isKeyDown(Input.KEY_RIGHT) ||input.isKeyDown(Input.KEY_D))
    	{
    		if (!isBlocked(playerPosition.x  + delta * 0.1f, playerPosition.y))
    		{
	    	    sprite = right;
	    	    sprite.update(delta);
	    	    playerPosition.x += delta * 0.1f;
    		}
    	}
    	
    	if (input.isKeyPressed(Input.KEY_ESCAPE))
    	{
    		// Switch to the game menu
    		gui.setRootPane(gameMenu);    		
    	}
    	
    	if (input.isKeyPressed(Input.KEY_B))
    	{
    		buildMode = !buildMode;
    	}
    	if (input.isKeyPressed(Input.KEY_I))
    	{
            for (int i=0;i<inventory.length;i++) {
            	System.out.println(inventory[i].name);
            }
    	}
        if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
        {
            // make sure clicking a button doesn't accidentally put down a building
            if (buildMode && input.getMouseX() > tileSize)
            {
              int middleX = screenWidth / 2;
                int middleY = screenHeight / 2;
              int tileX = getTilePosition(-middleX + input.getMouseX() + playerPosition.x);
              int tileY = getTilePosition(-middleY + input.getMouseY() + playerPosition.y) - 1;
              if(!isBlocked(tileX, tileY))
              {
                  switch(buildItem)
                  {
                      case 0:
                      currentMap.setTileId (tileX,tileY,0,32);
                      break;
                      case 1:
                      currentMap.setTileId (tileX,tileY,0,27);
                      break;
                  }
                  calculateBlocked();
              }
            }
        }
    }


    public void render(GameContainer container, Graphics g) throws SlickException
    {
    	int middleX = screenWidth / 2;
    	int middleY = screenHeight / 2;
    	currentMap.render(middleX - (int)playerPosition.x + 16 , middleY - (int)playerPosition.y + tileSize);
    	sprite.draw(middleX, middleY);
    	dude.render();
    	
    	twlInputAdapter.render();
    	
    	if (buildMode)
    	{
    	  Input input = container.getInput();	
    	  int tileX = getTilePosition(-middleX + input.getMouseX() + playerPosition.x);
    	  int tileY = getTilePosition(-middleY + input.getMouseY() + playerPosition.y) - 1;
    	  int mouseX = tileX * tileSize - (int)playerPosition.x + middleX + tileSize / 2; 
    	  int mouseY = tileY * tileSize - (int)playerPosition.y + middleY + tileSize; 
    	  
          Image tile = null;
          
          switch(buildItem)
          {
          case 0:
              tile = currentMap.getTileSet(0).tiles.getSubImage(7,3);
              break;
          case 1:
              tile = currentMap.getTileSet(0).tiles.getSubImage(2,3);
              break;               
          }  
    	  tile.setAlpha(.5f);
    	  tile.draw(mouseX,mouseY);
    	  
    	  // render build buttons
          for (int i=0;i<buildButtons;i++)
          {
            buildButton[i].render(container, g);         
          }

    	}
    }
    
    public boolean isBlocked(float x, float y)
    {
    	if (x < 0) return true;
    	if (y < 0) return true;
    	if (x > currentMap.getWidth() * tileSize) return true;
    	if (y > currentMap.getHeight() * tileSize) return true;
    	
         int xBlock = (int)x / tileSize;
         int yBlock = (int)y / tileSize;
         return blocked[xBlock][yBlock];
    }
    
    private int getTilePosition(float x)
    {
    	return (int)x / tileSize;
    }


    private void calculateBlocked()
    {
    	blocked = new boolean[currentMap.getWidth()][currentMap.getHeight()];
    	for (int xAxis=0;xAxis<currentMap.getWidth(); xAxis++)
    	{
	         for (int yAxis=0;yAxis<currentMap.getHeight(); yAxis++)
	         {
	             int tileID = currentMap.getTileId(xAxis, yAxis, 0);
	             String value = currentMap.getTileProperty(tileID, "blocked", "false");
	             if ("true".equals(value))
	             {
	                 blocked[xAxis][yAxis] = true;
	             }
	         }
    	 }
    	
    }
    public void componentActivated(AbstractComponent source) {
        for (int i=0;i<buildButtons;i++) {
            if (source == buildButton[i]) {
                buildItem = i;
            }
        }
    }
    
    
}