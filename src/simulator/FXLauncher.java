package simulator;

import java.io.*;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.stage.*;

/**
 * The class <i>FXLauncher</i> binds the JavaFX render engine to the application (through the declaration <i>extends Application</i>.
 * @author Rex Woollard
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 */
public class FXLauncher extends Application {
  private Simulator simulator; // Must be a heap-oriented instance field so that MenuItem objects can make repeated calls to it.

  /**
   * The <i>start()</i> method is defined as an <i>abstract</i> method in class <i>Application</i>, thus you MUST <i>Override</i> the <i>start()</i> method.
   * The effective entry point to program execution for a JavaFX application is through the <i>start()</i> method. By the time execution arrives here, the render engine will be active and a <i>Stage</i> object will have already been built.
   * @param primaryStage Existing window that has been pre-built by the render engine.
   * Program execution will drive through the start() method in microseconds . . . then the program patiently waits for user-events to occur . . . responding when necessary.
   * Typical events include user menu selections.
   */
	@Override
	public void start(Stage primaryStage) throws Exception {
    simulator = new Simulator(primaryStage); // Simulator HAS the two Army objects. It provides a communication path between the JavaFX GUI and the Battlefield logic.
		primaryStage.setTitle("Battlefield Simulator");
		primaryStage.setScene(createScene()); // The Scene contains an organized collection of ALL the JavaFX Node objects that are to be displayed on the screen.
		primaryStage.show(); // Once the objects have been assembled, the window can be opened (like raising the "curtain on a theatrical stage".
	} // end start()

	/**
	 * Assembles the JavaFX <i>Node</i> objects that are to be displayed on the screen.
	 * @return <i>Scene</i> is an object that contains an organized collection of ALL JavaFX <i>Node</i> objects that are to be displayed on the screen.
	 */
  private Scene createScene() {
  	ImageView imageViewBackground = createBackground(); // attempts to load a disk-based file into an Image object which is then wrapped inside an ImageView object (and an ImageView object can be added to a Scene)
    double aspectRatio = imageViewBackground.getImage().getHeight() / imageViewBackground.getImage().getWidth(); // auto-adjust the window aspect-ratio based on the image.
    final double SCENEWIDTH = 1000.0;
    final Group simulatorContainer = new Group(imageViewBackground, simulator); // Order matters here. The imageViewBackground is first, thus on the bottom visually. The simulator sits on top of that.

    Group sceneGraphRoot = new Group(simulatorContainer, createMenuBar());// Order matters here. The simulatorContainer is first, thus on the bottom visually. The newly contructed MenuBar sits on top of that.
    Scene mainScene = new Scene(sceneGraphRoot, SCENEWIDTH, SCENEWIDTH * aspectRatio); // Scene needs the Parent Node (and a parent Node will have child Node objects). Scene also needs to know its initial size.
    imageViewBackground.fitWidthProperty().bind(mainScene.widthProperty()); // resize the Background automatically, based on the Scene resizing

    return mainScene;
  } // end createScene()

  /**
   * The method is <i>private</i> since it is used only internally to class FXLauncher. The method exists to make the code more readable. It attempts to load a disk-based file and Create a JavaFX <i>Node</i> (called an <i>ImageView</i>) that can display <i>Image</i> objects.
   * @return <i>ImageView</i> object that has been created from an <i>Image</i> object which was loaded from a disk-based file
   */
  private ImageView createBackground() {
    final String filename = "MiddleEarth-3.jpg"; // must reside in the project directory to support this relative pathname
		try (FileInputStream fileInputStream = new FileInputStream(filename)) { // try-catch block implemented to manage potential file loading issues.
			Image imageBackground = new Image(fileInputStream);
			ImageView imageViewBackground = new ImageView(imageBackground);
	    imageViewBackground.setPreserveRatio(true); // maintain aspect ratio
	    imageViewBackground.setManaged(false); // suppresses the automatic centering . . . facilitates management of layout for POI markers
	    return imageViewBackground; // SUCCESS, we now have the ImageView object. Send a reference-to this object back to createScene()
		} catch (IOException exception) {
			exception.printStackTrace();
			System.err.println(exception.getMessage());
			System.exit(0);
		}
		return null; // failed to load Image, thus return nothing . . .
  } // end createBackground()

  /**
   * The method is <i>private</i> since it is used only internally to class FXLauncher. The method exists to make the code more readable.
   * @return the newly created <i>MenuBar</i> object.
   */
  private MenuBar createMenuBar() {
    // Create the "Run" Menu
    MenuItem populateMenuItem = new MenuItem("_Populate"); populateMenuItem.setMnemonicParsing(true); populateMenuItem.setOnAction(event->simulator.populate());	// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.populate())
    MenuItem runMenuItem = new MenuItem("_Run");           runMenuItem.setMnemonicParsing(true);      runMenuItem.setOnAction(event->simulator.run());						// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.run())
    MenuItem speedUpMenuItem = new MenuItem("Speed _Up");           speedUpMenuItem.setMnemonicParsing(true);      speedUpMenuItem.setOnAction(event->simulator.speedUp());	speedUpMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+U"));					// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.run())
    MenuItem slowDownMenuItem = new MenuItem("Slow _Down");           slowDownMenuItem.setMnemonicParsing(true);      slowDownMenuItem.setOnAction(event->simulator.slowDown());	slowDownMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));   
    MenuItem suspendMenuItem = new MenuItem("_Suspend");   suspendMenuItem.setMnemonicParsing(true);  suspendMenuItem.setOnAction(event->simulator.suspend());		// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.suspend())
    MenuItem saveMenuItem = new MenuItem("S_ave");   saveMenuItem.setMnemonicParsing(true);  saveMenuItem.setOnAction(event->simulator.save());
    MenuItem restoreMenuItem = new MenuItem("R_estore");   restoreMenuItem.setMnemonicParsing(true);  restoreMenuItem.setOnAction(event->simulator.restore());
    Menu menuRun = new Menu("_Run"); menuRun.setMnemonicParsing(true); menuRun.getItems().addAll(populateMenuItem, runMenuItem, suspendMenuItem, speedUpMenuItem, slowDownMenuItem, saveMenuItem, restoreMenuItem);	// assemble MenuItems in the "Run" Menu

    // Create the "Properties" Menu
    MenuItem openArmyListsMenuItem = new MenuItem("Show Army _Lists");		openArmyListsMenuItem.setMnemonicParsing(true);  openArmyListsMenuItem.setOnAction(event->simulator.openListViewWindow());		// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.openListViewWindow())
    MenuItem closeArmyListsMenuItem = new MenuItem("Close Army L_ists");	closeArmyListsMenuItem.setMnemonicParsing(true); closeArmyListsMenuItem.setOnAction(event->simulator.closeListViewWindow());	// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.closeListViewWindow())
    MenuItem openArmyTableMenuItem = new MenuItem("Show Army _Tables");		openArmyTableMenuItem.setMnemonicParsing(true);  openArmyTableMenuItem.setOnAction(event->simulator.openTableViewWindow());		// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.openListViewWindow())
    MenuItem closeArmyTableMenuItem = new MenuItem("Close Army T_ables");	closeArmyTableMenuItem.setMnemonicParsing(true); closeArmyTableMenuItem.setOnAction(event->simulator.closeTableViewWindow());	// create CALLBACK, that is, the code to execute when triggered by user event (in this case, simulator.closeListViewWindow())
    Menu menuProperties = new Menu("_Properties"); menuProperties.setMnemonicParsing(true); menuProperties.getItems().addAll(openArmyListsMenuItem, closeArmyListsMenuItem, openArmyTableMenuItem, closeArmyTableMenuItem);	// assemble MenuItems in the "Properties" Menu

    // Assemble Menu objects in new MenuBar and return
    return new MenuBar(menuRun, menuProperties);
  } // end createMenuBar()
  
	public static void main(String[] args) { launch(args); } // typically, this is as big as void main() gets in a regular JavaFX application.
}