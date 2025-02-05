package editor.application;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;

import javax.swing.WindowConstants;

import framework.core.navigation.MenuBuilder;
import framework.core.system.Application;
import framework.core.system.EngineProperties;
import framework.core.system.EngineProperties.Property;
import framework.utils.globalisation.Localization;

import editor.menu.AboutMenuItem;
import editor.menu.AllViewsMenuItem;
import editor.menu.ExitMenuItem;
import editor.menu.ExportDataMenuItem;
import editor.menu.ExportImagesMenuItem;
import editor.menu.ImportImageMenuItem;
import editor.menu.LoadMenuItem;
import editor.menu.ProjectMenuItem;
import editor.menu.ProjectSettingsMenuItem;
import editor.menu.PropertiesMenuItem;
import editor.menu.ResetPerspective;
import editor.menu.SaveMenuItem;
import editor.menu.ShowTileMapMenu;
import editor.menu.TileLayersMenuItem;
import editor.menu.TileMapMenuItem;
import editor.menu.TileMapSettingsMenuItem;
import resources.ResourceKeys;

/**
 * This is the main application, the main method resides within this class
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public final class Editor extends Application {

    /**
     * Constructs a new instance of this class type
     * 
     * @param isDebug TRUE if the application is in debug mode, FALSE otherwise
     */
    public Editor(boolean isDebug) {
        super(isDebug); 
        
        // Pressing on the close button won't do it's default action
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(1280, 768);
		setLocationRelativeTo(null);		
    }

    /**
     * The main method entry-point for the application
     * 
     * @param args The outside argument / command line argument
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                boolean debugMode = false;
                for(String arg : args) {
                    if(arg.trim().equalsIgnoreCase("debug")) {
                        debugMode = true;
                        break;
                    }
                }
                
              EngineProperties.instance().setProperty(Property.LOCALIZATION_PATH_CVS, "/resources/resources.csv");
              //EngineProperties.instance().setProperty(Property.LOG_DIRECTORY, System.getProperty("user.home") + File.separator + "desktop" + File.separator);
              EngineProperties.instance().setProperty(Property.ENGINE_OUTPUT, Boolean.toString(false));
              EngineProperties.instance().setProperty(Property.SUPPRESS_SIGNAL_REGISTRATION_OUTPUT, Boolean.toString(true));
              EngineProperties.instance().setProperty(Property.DISABLE_TRANSLATIONS_PLACEHOLDER, Boolean.toString(true));
               
              Editor editor = new Editor(debugMode);
              editor.setVisible(true);
            }
        });
    }

    /**
     * Populates the file menu
     */
    private void populateFileMenu() {

        MenuBuilder rootBuilder = MenuBuilder.start(getJMenuBar())
                .addMenu(Localization.instance().getLocalizedString(ResourceKeys.File))
                .addMenuItem(ProjectMenuItem.class)
                .addMenuItem(TileMapMenuItem.class)
                .addSeparator()
                .addMenuItem(LoadMenuItem.class)
                .addMenuItem(SaveMenuItem.class)
                .addSeparator();

        rootBuilder.addBuilder(MenuBuilder.start(rootBuilder)
                .addMenu(Localization.instance().getLocalizedString(ResourceKeys.Import))
                .addMenuItem(ImportImageMenuItem.class));

        rootBuilder.addBuilder(MenuBuilder.start(rootBuilder)
                .addMenu(Localization.instance().getLocalizedString(ResourceKeys.Export))
                .addMenuItem(ExportDataMenuItem.class)
                .addSeparator()
                .addMenuItem(ExportImagesMenuItem.class))
        .addSeparator();

        rootBuilder.addMenuItem(ExitMenuItem.class);
    }

    /**
     * Populates the edit menu
     */
    private void populateEditMenu() {
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString(ResourceKeys.Edit))
            .addMenuItem(ProjectSettingsMenuItem.class)
            .addMenuItem(TileMapSettingsMenuItem.class);
    }
    
    /**
     * Populates the view menu
     */
    private void populateViewMenu() {
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString(ResourceKeys.View))
        .addMenuItem(AllViewsMenuItem.class)
        .addSeparator()
        .addMenuItem(TileLayersMenuItem.class)
        .addMenuItem(PropertiesMenuItem.class)
        .addSeparator()
        .addMenu(ShowTileMapMenu.class)
        .addMenuItem(ResetPerspective.class);
    }

    /**
     * Populates the help menu
     */
    private void populateHelpMenu() {
        MenuBuilder.start(getJMenuBar())
        .addMenu(Localization.instance().getLocalizedString(ResourceKeys.Help))
        .addMenuItem(AboutMenuItem.class);
    }

    @Override public void windowOpened(WindowEvent windowEvent) {
    	super.windowOpened(windowEvent);
    	
        populateFileMenu();
        populateEditMenu();
        populateViewMenu();
        populateHelpMenu();
    }    
}