package army;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import simulator.Simulator;
import actor.*;
/**
 * <i>Army</i> class manages a collection of <i>Actor</i> objects. The <i>Army</i> class does not need to know any detailed information about subclasses of <i>Actor</i>.
 * <i>Army</i> takes responsibility for adding each avatar (a reference-to<i>Node</i>) to the <i>Simulator</i> (which inherits <i>Group</i> and <i>Group</i> maintains a list of child <i>Node</i> objects that are to be displayed on the <i>Scene</i>).
 * @author Ammar Khan
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 * @see Actor
 * @see ActorFactory
 * @see Simulator
 */
public class Army {
	private String name;
	/** Each <i>Army</i> object must have a <i>Collection</i> that can hold references to <i>Actor</i> objects. Currently, the <i>Collection</i> is implemented as an <i>ArrayList</i>*/
	private ObservableList<Actor> collectionActors = FXCollections.observableList(new ArrayList<>());
	public ObservableList<Actor> getObservableListActors() { return FXCollections.unmodifiableObservableList(collectionActors);	}
	/** The reference-to-<i>Simulator</i> gives the Army access to the list of child <i>Node</i> objects maintained by the <i>Simulator</i> (which inherits from <i>Group</i>). When avatars are added the reference-to-<i>Simulator</i> provides access to the <i>Scene</i>. */
	private Simulator simulator;
	public Scene getScene() { return simulator.getScene(); }
	private Army opposingArmy;
	/** Used to support the color of the <i>DropShadow</i> which is applied to each avatar. */
	private Color color;
	/** An <i>Effect</i> that is applied to each <i>Node</i> object that expresses the avatar. */
	private DropShadow dropShadow;
	public static final String FONT_NAME = "Perpetua";
	private static final Font NOTIFICATION_FONT_SMALL = new Font(FONT_NAME, 14.0);
	private static final Font NOTIFICATION_FONT_LARGE = new Font(FONT_NAME, 36.0);
	
	/**
	 * Constructor builds an </i>Army</i> object 
	 * @param name is the <i>String</i> label for the <i>Army</i>.
	 * @param simulator is a reference-to-<i>Simulator</i> that is captured by <i>Army</i>. It is used to gain access to the <i>Group</i> list of child <i>Node</i> objects. <i>Army</i> takes responsibility for adding each avatar to the <i>Group</i>.
	 * @param color is used to define a </i>DropShadow</i> (a subclass of <i>Effect</i>) that is applied to each <i>Node</i> object that expresses the avatar. The <i>DropShadow</i> effect shows a visual representation of the <i>Node</i> allegiance to the <i>Army</i>.
	 */
	public Army(String name, Simulator simulator, Color color) {
		this.name = name;
		this.simulator = simulator;
		this.color = color;
		dropShadow = new DropShadow(10.0, this.color);
	} // end Constructor
	
	/**
	 * Creates the specified number of <i>Actor</i> objects, adding each new <i>Actor</i> object to the <i>Collection</i>. Additionally, each <i>Actor</i> object has a reference-to-<i>Node</i> for the avatar. That reference-to-<i>Node</i> value is added to the <i>Group</i> list of children (and <i>Simuloatr</i> is a kind of <i>Group</i>.
	 * @param type is an <i>enum</i> value which is a reference-to an <i>ActorFactory.Type</i> (such as <b>HOBBIT</b?, <b>WIZARD</b>, <b>ORC</b>, <b>ELF</b> or <b>RANDOM</b>. That reference-to-<i>enum</i> value is used to call the <i>create()</i> virtual method.
	 * @param numToAdd is the number of objects to add to the <i>Army</i>.
	 */
	public void populate(ActorFactory.Type type, int numToAdd) {
		for (int i=0; i<numToAdd; ++i) {
			Actor actor = type.create(this);
			collectionActors.add(actor); // send "this" so that Actor object can capture its allegiance
			if (simulator != null) { // if called by basic jUnit tests that do not use Simulator, the following code is ignored
				Node avatar = actor.getAvatar();
				// Note: initial placement of avatar cannot be performed in the Actor constructor. The Actor does not yet have access to the Scene (where it can determine the width/height dimensions)
				// BEFORE execution of the next line, the avatar WILL NOT know about the Scene in which it has been placed.
				simulator.getChildren().add(avatar);
				// AFTER execution of the preceding line, the avatar WILL know about the Scene in which it has been placed, thus we have access to dimensioning information.
				avatar.setTranslateX(avatar.getScene().getWidth()*Math.random()); avatar.setTranslateY(avatar.getScene().getHeight()*Math.random()); 
				avatar.setEffect(dropShadow);
			} // end if (simulator != null)
		}
	} // end populate()

	/** Outputs textual display to the console. */
	public void display() {
		System.out.println(name);
		for (Actor actor : collectionActors) {
			System.out.println(actor);
		}
	} // end display()
	
	/** Used to edit a single <i>Actor</i> object in the <i>Collection</i> of <i>Actor</i> objects. */
	public boolean edit(int index) {
		if (index<0 || index>=collectionActors.size()) {
			System.err.println("Index into ArrayList out of range.");
			return false;
		}
		collectionActors.get(index).inputAllFields();
		return true;
	} // end edit()


	public int getSize() { return collectionActors.size(); }

	/** Iterates through the <i>Collection</i> of <i>Actor</i> objects and instructs each to begin (or resume) motion (ultimately using <i>TranslateTransition</i> objects that define animated movement). The start maps to a call to <i>play()</i> for the <i>TranslateTransition</i> object. */
	public void startMotion() {
		for (Actor actor : collectionActors)
			actor.startMotion(false);
	}

	/** Iterates through the <i>Collection</i> of <i>Actor</i> objects and instructs each to suspend motion (ultimately using <i>TranslateTransition</i> objects that define animated movement). The suspend maps to a call to <i>pause()</i> for the <i>TranslateTransition</i> object. */
	public void suspendMotion() {
		for (Actor actor : collectionActors)
			actor.pauseMotion();
	}

	@Override
	public String toString() {
		return String.format("%s:%d", name, collectionActors.size());
	}

	public String getName() { return name; }

	/** Calls upon class Actor to create the TableView; then associates the ObservableList of Actor objects with the TableView<Actor> */
	public Node getTableViewOfActors() {
		TableView<Actor> tableView = Actor.createTable(); // Actor class knows the details of the fields, so let Actor class take responsibility for defining the TableView (that's an example of encapsulation and decoupling).
		tableView.setItems(collectionActors); // collectionActors is Observable, thus the TableView<Actor> object will receive future change-of-state notifications when an Actor object is added ro removed from the Collection.
		return tableView;
	}

	public Army getOpposingArmy() {	return opposingArmy; }
	public void setOpposingArmy(Army opposingArmy) { this.opposingArmy = opposingArmy; }

	public Actor findNearestOpponent(Actor actorToMove) {
		Actor nearest = null;
		double distanceToClosest = Double.MAX_VALUE; 
		for (Actor current : collectionActors) {
			if (current.isVisible()) {
				double actorToMoveX = actorToMove.getAvatar().getTranslateX();
				double actorToMoveY = actorToMove.getAvatar().getTranslateY();
				double currentX = current.getAvatar().getTranslateX();
				double currentY = current.getAvatar().getTranslateY();
				double deltaX = actorToMoveX - currentX;
				double deltaY= actorToMoveY - currentY;
				double calculatedDistance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
				if (calculatedDistance < distanceToClosest) {
					distanceToClosest = calculatedDistance;
					nearest = current;
				}
			}
		}
		return nearest;
	}

	public void removeNowDeadActor(Actor nowDeadActor) {
		final ObservableList<Node> listJavaFXNodesOnBattlefield = simulator.getChildren(); // creating as a convenience variable, since the removeNowDeadActor() method needs to manage many Node objects in the simulator collection of Node objects
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // START: Create Notification message about the nowDeadActor: Create, then add two Transition Animations, packing in a ParallelTransition
		 { // setup Stack Frame to allow re-use of variable identifiers "tt", "ft" and "pt"
		 Text message = new Text(240.0, 100.0, "Dead: " + nowDeadActor.getName()); message.setFont(NOTIFICATION_FONT_SMALL); message.setStroke(color);
		 final Duration duration = Duration.seconds(3.0);
		 FadeTransition ft = new FadeTransition(duration); ft.setToValue(0.0); // no need to associate with the Text (message) here, that will be done in the ParallelTransition
		 TranslateTransition tt = new TranslateTransition(duration); tt.setByY(200.0);  // no need to associate with the Text (message) here, that will be done in the ParallelTransition
		 ParallelTransition pt = new ParallelTransition(message, ft, tt); pt.setOnFinished(event->listJavaFXNodesOnBattlefield.remove(message)); pt.play(); // couple both Transitions in the ParallelTransition and associate with Text
		 listJavaFXNodesOnBattlefield.add(message); // it will play() and after playing the code in the setOnFinished() method will called to remove the temporay message from the scenegraph.
		 }
		 // END: Create Notification message about the nowDeadActor: Create, then add two Transition Animations, packing in a ParallelTransition
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

		 collectionActors.remove(nowDeadActor); // removes nowDeadActor from the collection of active Actor objects that are part of this army.
		 listJavaFXNodesOnBattlefield.remove(nowDeadActor.getAvatar()); // removes the avatar from the screnegraph (the Node object). The actor will disappear from the screen.
		 
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // START: Create Final Announcement of Winning Army
		 if (collectionActors.size() == 0) { // Army has been wiped out, since no Actor objects remain in the collection. Therefore . . . the opposing Army wins.
		 Text winner = new Text(260.0, 300.0, "Winner: " + opposingArmy.getName()); winner.setFont(NOTIFICATION_FONT_LARGE); winner.setStroke(opposingArmy.color); winner.setEffect(opposingArmy.dropShadow);
		 final Duration duration = Duration.seconds(1.0);
		 FadeTransition ft = new FadeTransition(duration, winner); ft.setToValue(0.2); ft.setCycleCount(10); ft.setAutoReverse(true); ft.setOnFinished(event->listJavaFXNodesOnBattlefield.remove(winner)); ft.play();
		 listJavaFXNodesOnBattlefield.add(winner); // it will play() and after playing the code in the setOnFinished() method will called to remove the temporary winner from the scenegraph.
		 }
		 // END: Create Final Announcement of Winning Army
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		 
	}

	public double getSpeedControllerValue() {
		return simulator.getSpeedControllerValue();
	}
	
	public void serialize(ObjectOutputStream out) throws IOException {
		  out.writeObject(name);
		  out.writeDouble(color.getRed());
		  out.writeDouble(color.getGreen());
		  out.writeDouble(color.getBlue());
		  out.writeDouble(color.getOpacity());
		  out.writeInt(collectionActors.size());
		  for (Actor a : collectionActors)
		    out.writeObject(a);
		  } // end serialize() to support serialization

		public void deserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
		  collectionActors.clear();
		  name = (String) in.readObject();
		  color = new Color(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
		  dropShadow = new DropShadow(10.0, color);
		  int size = in.readInt();
		  for (int i = 0; i < size; ++i) {
		    Actor actor = (Actor) in.readObject();
		    actor.setArmyAllegiance(this);
		    actor.getAvatar().setEffect(dropShadow);
		    simulator.getChildren().add(actor.getAvatar());
		    collectionActors.add(actor);
		  }
		} // end deserialize() to support serialization

} // end class Army