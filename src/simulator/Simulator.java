package simulator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import actor.Actor;
import actor.ActorFactory;
import army.Army;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * The <i>Simulator</i> class is composed of (HAS-A relationship) two <i>Army</i> objects. 
 * It HAS-A loose association with the <i>primaryStage</i> to support the management of secondary <i>Stage</i> objects that will be created to show the <i>ListView</i> and <i>TableView</i> representations of the <i>Army</i> objects. 
 * @author Rex Woollard
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 */
public class Simulator extends Group {
	/** <i>primaryStage</i> supports the management of secondary <i>Stage</i> objects that will be created to show the <i>ListView</i> and <i>TableView</i> representations of the <i>Army</i> objects.  */
	private Stage primaryStage;
	private Army forcesOfLight;
	private Army forcesOfDarkness;
	private Stage stageListControllerWindow;  // reference-to value for a Stage that contain 2 ListView<Actor> objects with suitable titling.
	private Stage stageTableControllerWindow; // reference-to value for a Stage that contain 2 TableView<Actor> objects with suitable titling.
	private double speedController = 1.0;
	private static final double MAX_SPEED_CONTROLLER = 50.0;
	private static final double MIN_SPEED_CONTROLLER = 1.0;

	
	/**
	 * Constructs the two <i>Army</i> objects and captures the reference-to-<i>Stage</i> value in <i>primaryStage</i>. 
	 * @param <i>primaryStage</i> supports the management of secondary <i>Stage</i> objects that will be created to show the <i>ListView</i> and <i>TableView</i> representations of the <i>Army</i> objects.
	 */
	public Simulator(Stage primaryStage) {
		this.primaryStage = primaryStage;
		forcesOfLight = new Army("Forces of Light", this, Color.WHITE);
		forcesOfDarkness = new Army("Forces of Darkness", this, Color.BLACK);
		forcesOfDarkness.setOpposingArmy(forcesOfLight);
		forcesOfLight.setOpposingArmy(forcesOfDarkness);
		buildListViewWindow(); // creates the Stage object to hold the ListView<Actor> objects with suitable titling.
		buildTableViewWindow(); // creates the Stage object to hold the TableView<Actor> objects with suitable titling.
	}

	/**
	 * Causes each <i>Army</i> to add defined number of <i>Actor</i> objects.
	 */
	public void populate() {
		forcesOfLight.populate(ActorFactory.Type.HOBBIT, 5);
		forcesOfLight.populate(ActorFactory.Type.ELF, 3);
		forcesOfLight.populate(ActorFactory.Type.WIZARD, 2);
		forcesOfLight.populate(ActorFactory.Type.RANDOM, 12);
		
//		forcesOfDarkness.populate(ActorFactory.Type.ELF, 5);
		forcesOfDarkness.populate(ActorFactory.Type.ORC, 23);
	}

	/** Causes each <i>Army</i> to iterate through its <i>Collection</i> of <i>Actor</i> objects, cause each of them to begin a <i>TranslateTransition</i> (using <i>play()</i>). */
	public void run() {
		forcesOfLight.startMotion();
		forcesOfDarkness.startMotion();
	}

	/** Causes each <i>Army</i> to iterate through its <i>Collection</i> of <i>Actor</i> objects, cause each of them to suspend a <i>TranslateTransition</i> (using <i>pause()</i>). */
	public void suspend() {
		forcesOfLight.suspendMotion();
		forcesOfDarkness.suspendMotion();
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// START ListView<Actor> code
	/** Uses JavaFX layout managers (<i>HBox</i> and <i>VBox</i>) to organized the 2 <i>ListView</i> objects with suitable titling. */
  private final void buildListViewWindow() { // final because of its use in the constructor
    VBox vBoxLightArmy = new VBox(5.0, new Text(forcesOfLight.getName()), new ListView<Actor>(forcesOfLight.getObservableListActors()));
    VBox vBoxDarkArmy = new VBox(5.0, new Text(forcesOfDarkness.getName()), new ListView<Actor>(forcesOfDarkness.getObservableListActors()));
    HBox hBoxSceneGraphRoot = new HBox(5.0, vBoxLightArmy, vBoxDarkArmy);

    if (stageListControllerWindow != null) { // if a Stage already exists, clear it of content before building the new . . . this assists in garbage collection.
      stageListControllerWindow.close();
      stageListControllerWindow.setScene(null);
    }
    stageListControllerWindow = new Stage(StageStyle.UTILITY);
    stageListControllerWindow.initOwner(primaryStage);
    stageListControllerWindow.setScene(new Scene(hBoxSceneGraphRoot));
  } // end buildListViewWindow()
	public void openListViewWindow() { stageListControllerWindow.show(); }
	public void closeListViewWindow() { stageListControllerWindow.hide(); }
	// END ListView<Actor> code
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// START TableView<Actor> code
	/** Uses JavaFX layout managers (<i>HBox</i> and <i>VBox</i>) to organized the 2 <i>TableView</i> objects with suitable titling. */
  private final void buildTableViewWindow() { // final because of its use in the constructor
    VBox vBoxLightArmy = new VBox(5.0, new Text(forcesOfLight.getName()), forcesOfLight.getTableViewOfActors());
    VBox vBoxDarkArmy = new VBox(5.0, new Text(forcesOfDarkness.getName()), forcesOfDarkness.getTableViewOfActors());
    HBox hBoxSceneGraphRoot = new HBox(5.0, vBoxLightArmy, vBoxDarkArmy);

    if (stageTableControllerWindow != null) { // if a Stage already exists, clear it of content before building the new . . . this assists in garbage collection.
      stageTableControllerWindow.close();
      stageTableControllerWindow.setScene(null);
    }
    stageTableControllerWindow = new Stage(StageStyle.UTILITY);
    stageTableControllerWindow.initOwner(primaryStage);
    stageTableControllerWindow.setScene(new Scene(hBoxSceneGraphRoot));
  } // end buildTableViewWindow()
	
  
  
  public void openTableViewWindow() { stageTableControllerWindow.show(); }
	public void closeTableViewWindow() { stageTableControllerWindow.hide(); }
	// END TableView<Actor> code
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	public void speedUp() {
		
		if (++speedController > MAX_SPEED_CONTROLLER)
			speedController = MAX_SPEED_CONTROLLER;
	}

	public void slowDown() {

		if (--speedController < MIN_SPEED_CONTROLLER)
			speedController = MIN_SPEED_CONTROLLER;
	}

	public double getSpeedControllerValue() {
		return speedController;
	}

	public void save() {
		  // Using a try block in case there is a file I/O error. Open a file that is configured for binary output.
		  try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("battlefield.ser"))) {
		    forcesOfLight.serialize(out);// "normal" method call that I created. Army class NOT serializable. Actor class and ALL its subclasses are serializable.
		    forcesOfDarkness.serialize(out);// same
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		} // end save()
		public void restore() {
		  // Using a try block in case there is a file I/O error. Open a file that is configured for binary input.
		  try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("battlefield.ser"))) {
		    forcesOfLight.deserialize(in);// "normal" method call that I created. Army class NOT serializable. Actor class and ALL its subclasses are serializable.
		    forcesOfDarkness.deserialize(in); // same
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		} // end restore()



}
