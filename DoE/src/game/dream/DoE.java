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
	private boolean[][] blocked;
	private static final int tileSize = 32;
	public static final int screenHeight = 600;
	public static final int screenWidth = 800;
	public static int inventoryItems = 4;
	public static Image[] inventoryItem = new Image[inventoryItems];
	private TWLInputAdapter twlInputAdapter;
    private LWJGLRenderer lwjglRenderer;
    private ThemeManager theme;
    public static GUI gui;
    public static Widget root;
    public static GameMenu gameMenu;
    public static ResizableFrame inventoryMenu;
    public static InventoryPanel inventoryPanel;
    public static DoE mainGame;
    public static Player player;
    public static int inventorySlots = 50;
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
    	    	
    	dude = new npc(new Vector2f(5f, 5f));        
    	player = new Player(new Vector2f(0f, 0f));        
    	currentMap = new TiledMap("data/desert.tmx");
    	// define inventory icons
        inventoryItem[0] = new Image ("data/knt1_rt1.gif");
        inventoryItem[1] = new Image ("data/knt1_rt2.gif");
        inventoryItem[2] = new Image ("data/knt1_lf1.gif");
        inventoryItem[3] = new Image ("data/knt1_lf2.gif");
    	player.init(container);
        
        
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
        
        inventoryMenu = new ResizableFrame();
        inventoryMenu.setSize(screenWidth, screenHeight);
        inventoryMenu.setTitle("inventory");
        inventoryMenu.setResizableAxis(ResizableFrame.ResizableAxis.NONE);
        inventoryPanel = new InventoryPanel (10, 5);
        inventoryMenu.add(inventoryPanel);
        inventoryMenu.setTheme("");

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
    	player.update(container,delta);
    	twlInputAdapter.update();
    	Input input = container.getInput();
    	
    	if (input.isKeyPressed(Input.KEY_ESCAPE))
    	{
    		// Switch to the game menu
    		gui.setRootPane(gameMenu);    		
    	}
    	
        if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
        {
            // make sure clicking a button doesn't accidentally put down a building
            if (player.buildMode && input.getMouseX() > tileSize)
            {
              int middleX = screenWidth / 2;
                int middleY = screenHeight / 2;
              int tileX = getTilePosition(-middleX + input.getMouseX() + player.position.x);
              int tileY = getTilePosition(-middleY + input.getMouseY() + player.position.y) - 1;
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
    	currentMap.render(middleX - (int)player. position.x + 16 , middleY - (int)player.position.y + tileSize);
    	player.render(container, g);
    	dude.render();
    	
    	twlInputAdapter.render();
    	
    	if (player.buildMode)
    	{
    	  Input input = container.getInput();	
    	  int tileX = getTilePosition(-middleX + input.getMouseX() + player.position.x);
    	  int tileY = getTilePosition(-middleY + input.getMouseY() + player.position.y) - 1;
    	  int mouseX = tileX * tileSize - (int)player.position.x + middleX + tileSize / 2; 
    	  int mouseY = tileY * tileSize - (int)player.position.y + middleY + tileSize; 
    	  
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