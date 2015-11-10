package actor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import util.*;
import army.*;

/**
 * The <i>Orc</i> class is a subclass of <i>Actor</i>. The <i>Actor</i> class tracks state information for individual actors in the simulation: <i>name</i>, <i>health</i>, <i>strength</i>, <i>speed</i>, etc (and later, a screen avatar with coordinates). Additional attributes are tracked in the subclasses. The behaviours
 * (moving and battling) are defined in the subclasses. The <i>Actor</i> class is <i>abstract</i>, thus no <i>Actor</i> objects will ever be created -- only subclass objects, such as <i>Orc</i>.
 * The <i>Orc</i> class adds characteristics that are unique to elves. 
 * 
 * @author Ammar Khan
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 * @see Actor
 * @see ActorFactory
 * @see Army
 */
public class Orc extends Actor {
	/** class-oriented variable used to generate a unique identifier for each new Orc object */
	private static int orcCount = 0;
	/** {@value} */
	public static final double MAX_SMELL = 1000.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation
	/** {@value} */
	public static final double MIN_SMELL = 100.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation
	/** May be used for future functionality . . . though not now. */
	private SimpleDoubleProperty smell;
	public void setSmell(SimpleDoubleProperty smell) { this.smell.set(Math.max(Math.min(smell.get(), MAX_SMELL), MIN_SMELL)); } // this style replaces an if-else-if-else
	public SimpleDoubleProperty getSmell() { return smell; }
	
	/** This subclass of <i>Actor</i> defines its own unique avatar, which can be ANY kind of <i>Node</i>, but for <i>Orc</i> it will be <i>Rectangle</i>. */
	private Rectangle avatar;

	/** Supports the generation of random values for automatically created objects. */
	public Orc(Army armyAllegiance) {
		super(++orcCount, armyAllegiance);
		smell = new SimpleDoubleProperty(SingletonRandom.instance.getNormalDistribution(MIN_SMELL, MAX_SMELL, 4.0));
	}

	/** Supports text-oriented input / editing of attributes of an <i>Orc</i> including the inherited attributes of <i>Actor</i>. */
	@Override
	public void inputAllFields() {
		super.inputAllFields();
		smell.set(Input.instance.getDouble("Smell:"+smell, MIN_SMELL, MAX_SMELL));
	} // end inputAllFields()
	
	/** overrides the superclass (<i>Actor</i>) version of <i>toString()</i> and provides a textual representation of the <i>Orc</i> object. It calls upon the <i>Actor</i> to assemble its <i>toString</i> components, then adds the <i>Orc</i>-specific details. */
	@Override
	public String toString() {
		return String.format("%s Smell:%4.1f", super.toString(), smell);
	} // end toString()

	/** Each subclass of <i>Actor</i> MUST define its own unique avatar, which can be ANY kind of <i>Node</i>. It MUST because the method <i>createAvatar()</i> is defined as an <i>abstract</i> method. The <i>Orc</i> creates a <i>Rectangle</i> object. */
	@Override
	public void createAvatar() {
		avatar = new Rectangle(3.0, 10.0, Color.RED);
	}

	/** Even though avatar is defined as a <i>Rectangle</i> (in the <i>Orc</i> class), it is returned as a reference-to-<i>Node</i>, to simplify use of the avatar object elsewhere in the program. */
	@Override
	public Node getAvatar() {	return avatar; }
	@Override
	protected Point2D findNewLocation(Actor opponent) {
		// TODO Auto-generated method stub
		return new Point2D((getAvatar().getTranslateX())+(opponent.getAvatar().getTranslateX()*0.75), ((getAvatar().getTranslateX())+opponent.getAvatar().getTranslateY()*0.75));
	}
	@Override
	public boolean isVisible() {
		return true;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		 out.writeDouble(getSmell().get());     // SimpleDoubleProperty name is NOT serializable, so I do it manually
		 } // end writeObject() to support serialization

		  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			smell = new SimpleDoubleProperty(in.readDouble());
		 } // end readObject() to support serialization
}
