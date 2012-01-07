
package game.dream;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;


public class GameMenu extends Widget {

    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
            Display.setTitle("TWL Simple Game Menu Demo");
            Display.setVSyncEnabled(true);

            LWJGLRenderer renderer = new LWJGLRenderer();
            GameMenu gameUI = new GameMenu();
            GUI gui = new GUI(gameUI, renderer);

            ThemeManager theme = ThemeManager.createThemeManager(
                    GameMenu.class.getResource("simpleGameMenu.xml"), renderer);
            gui.applyTheme(theme);

            while(!Display.isCloseRequested() && !gameUI.quit) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

                gui.update();
                Display.update();
            }

            gui.destroy();
            theme.destroy();
        } catch (Exception ex) {

        }
        Display.destroy();
    }

    private final Button[] buttons;

    public boolean quit;

    public GameMenu() {
        buttons = new Button[3];
        buttons[0] = new Button("Resume Game");
        
        buttons[0].addCallback(new Runnable() {
            public void run() {
            	// set the GUI back to normal
            	DoE.gui.setRootPane(DoE.root);
            }
        });        
        
        buttons[1] = new Button("Options");
        buttons[2] = new Button("Quit");
        
        

        for(int i=0 ; i<buttons.length ; i++) {
            add(buttons[i]);
        }
    }

    private static final int TITLE_HEIGHT = 200;
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 50;

    @Override
    protected void layout() {
        int centerX = getInnerX() + getInnerWidth()/2;
        int distY = (getInnerHeight() - TITLE_HEIGHT) / (buttons.length + 1);

        for(int i=0 ; i<buttons.length ; i++) {
            buttons[i].setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
            buttons[i].setPosition(centerX - BUTTON_WIDTH/2,
                    TITLE_HEIGHT + (i+1)*distY - BUTTON_HEIGHT/2);
        }
    }

    @Override
    protected boolean handleEvent(Event evt) {
        if(super.handleEvent(evt)) {
            return true;
        }
        switch (evt.getType()) {
            case KEY_PRESSED:
                switch (evt.getKeyCode()) {
                    case Event.KEY_ESCAPE:
                    	
                        quit = true;
                        return true;

                }
        }
        return false;
    }

}
