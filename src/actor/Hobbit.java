package actor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import army.Army;
import util.*;

/**
 * The <i>Hobbit</i> class is a subclass of <i>Actor</i>. The <i>Actor</i> class tracks state information for individual actors in the simulation: <i>name</i>, <i>health</i>, <i>strength</i>, <i>speed</i>, etc (and later, a screen avatar with coordinates). Additional attributes are tracked in the subclasses. The behaviours
 * (moving and battling) are defined in the subclasses. The <i>Actor</i> class is <i>abstract</i>, thus no <i>Actor</i> objects will ever be created -- only subclass objects, such as <i>Hobbit</i>.
 * The <i>Hobbit</i> class adds characteristics that are unique to elves. 
 * 
 * @author Ammar Khan
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 * @see Actor
 * @see ActorFactory
 * @see Army
 */
public class Hobbit extends Actor {
	/** {@value} */
	public static final double MAX_STEALTH = 100.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation
	/** {@value} */
	public static final double MIN_STEALTH = 0.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation
	/** class-oriented variable used to generate a unique identifier for each new <i>Hobbit</i> object */
	private static int hobbitCount = 0;

	/** Influences the visibility of this object; sufficiently high stealth means <i>Hobbit</i> object cannot be seen by others. */
	private SimpleDoubleProperty stealth;
	public void setStealth(SimpleDoubleProperty stealth) { this.stealth.set( Math.max(Math.min(stealth.get(), MAX_STEALTH), MIN_STEALTH)); } // this style replaces an if-else-if-else
	public SimpleDoubleProperty getStealth() { return stealth; }
	
	/** This subclass of <i>Actor</i> defines its own unique avatar, which can be ANY kind of <i>Node</i>, but for <i>Hobbit</i> it will be <i>Circle</i>. */
	private Circle avatar;

	/** Supports the generation of random values for automatically created objects. */
	public Hobbit(Army armyAllegiance) {
		super(++hobbitCount, armyAllegiance); // calls the matching superclass, which is Actor.
		stealth = new SimpleDoubleProperty(SingletonRandom.instance.getNormalDistribution(MIN_STEALTH, MAX_STEALTH, 2.0));
	} // end Constructor
	
	/** Supports text-oriented input / editing of attributes of a <i>Hobbit</i> including the inherited attributes of <i>Actor</i>. */
	@Override
	public void inputAllFields() {
		super.inputAllFields();
		stealth.set(Input.instance.getDouble("Stealth:"+stealth, MIN_STEALTH, MAX_STEALTH));
	} // end inputAllFields()
	
	/** overrides the superclass (<i>Actor</i>) version of <i>toString()</i> and provides a textual representation of the <i>Hobbit</i> object. It calls upon the <i>Actor</i> to assemble its <i>toString</i> components, then adds the <i>Hobbit</i>-specific details. */
	@Override
	public String toString() {
		return String.format("%s Stealth:%4.1f", super.toString(), stealth);
	} // end toString()

	/** Each subclass of <i>Actor</i> MUST define its own unique avatar, which can be ANY kind of <i>Node</i>. It MUST because the method <i>createAvatar()</i> is defined as an <i>abstract</i> method. The <i>Hobbit</i> creates a <i>Circle</i> object. */
	@Override
	public void createAvatar() {
		avatar = new Circle(5.0, Color.AQUAMARINE);
	}
	
	/** Even though avatar is defined as a <i>Circle</i> (in the <i>Hobbit</i> class), it is returned as a reference-to-<i>Node</i>, to simplify use of the avatar object elsewhere in the program. */
	@Override
	public Node getAvatar() {	return avatar; }
	@Override
	protected Point2D findNewLocation(Actor opponent) {
		SimpleDoubleProperty maxY = new SimpleDoubleProperty(armyAllegiance.getScene().getHeight());
		SimpleDoubleProperty maxX = new SimpleDoubleProperty(armyAllegiance.getScene().getWidth());
		SimpleDoubleProperty newX = new SimpleDoubleProperty(1.0);
		SimpleDoubleProperty newY = new SimpleDoubleProperty(1.0);
		if ((opponent.getAvatar().getTranslateX() < (0.5*maxX.get())) && (opponent.getAvatar().getTranslateY() < (0.5*maxY.get()))){
			newX.set(SingletonRandom.instance.getNormalDistribution((0.5*maxX.get()), maxX.get(), 2.0));
			newY.set(SingletonRandom.instance.getNormalDistribution((0.5*maxY.get()), maxY.get(), 2.0));
			return new Point2D(newX.get(), newY.get());
		}
		else if ((opponent.getAvatar().getTranslateX() >= (0.5*maxX.get())) && (opponent.getAvatar().getTranslateY() < (0.5*maxY.get()))){
			newX.set(SingletonRandom.instance.getNormalDistribution(0.0, (0.5*maxX.get()), 2.0));
			newY.set(SingletonRandom.instance.getNormalDistribution((0.5*maxY.get()), maxY.get(), 2.0));
			return new Point2D(newX.get(), newY.get());
		}
		else if ((opponent.getAvatar().getTranslateX() < (0.5*maxX.get())) && (opponent.getAvatar().getTranslateY() >= (0.5*maxY.get()))){
			newX.set(SingletonRandom.instance.getNormalDistribution((0.5*maxX.get()), maxX.get(), 2.0));
			newY.set(SingletonRandom.instance.getNormalDistribution(0.0, (0.5*maxY.get()), 2.0));
			return new Point2D(newX.get(), newY.get());
		}
		else {
			newX.set(SingletonRandom.instance.getNormalDistribution(0.0, (0.5*maxX.get()), 2.0));
			newY.set(SingletonRandom.instance.getNormalDistribution(0.0, (0.5*maxY.get()), 2.0));
			return new Point2D(newX.get(), newY.get());
		}
	}
	@Override
	public boolean isVisible() {
		return getStealth().get() < (MIN_STEALTH+MAX_STEALTH)/2.0;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		 out.writeDouble(getStealth().get());     // SimpleDoubleProperty name is NOT serializable, so I do it manually
		 } // end writeObject() to support serialization

		  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			stealth = new SimpleDoubleProperty(in.readDouble());
		 } // end readObject() to support serialization
} // end class Hobbit
