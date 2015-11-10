package actor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import util.*;
import army.Army;

/**
 * The <i>Elf</i> class is a subclass of <i>Actor</i>. The <i>Actor</i> class tracks state information for individual actors in the simulation: <i>name</i>, <i>health</i>, <i>strength</i>, <i>speed</i>, etc (and later, a screen avatar with coordinates). Additional attributes are tracked in the subclasses. The behaviours
 * (moving and battling) are defined in the subclasses. The <i>Actor</i> class is <i>abstract</i>, thus no <i>Actor</i> objects will ever be created -- only subclass objects, such as <i>Elf</i>.
 * The <i>Elf</i> class adds characteristics that are unique to elves. 
 * 
 * @author Ammar Khan
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 * @see Actor
 * @see ActorFactory
 * @see Army
 */
public class Elf extends Actor {
	/** {@value} */
	public static final double PROBABILITY_ELF_HAS_CLOAK = 0.6;
	/** class-oriented variable used to generate a unique identifier for each new Elf object */
	private static int elfCount = 0;
	
	/** Directly changes the visibility of this object; if true, this <i>Elf</i> object cannot be seen by others. */
	private boolean hasInvisibilityCloak;
	
	/** This subclass of <i>Actor</i> defines its own unique avatar, which can be ANY kind of <i>Node</i>, but for <i>Elf</i> it will be <i>Rectangle</i>. */
	private Rectangle avatar;

	/** Supports the generation of random values for automatically created objects. */
	public Elf(Army armyAllegiance) {
		super(++elfCount, armyAllegiance);
		hasInvisibilityCloak = (Math.random() < PROBABILITY_ELF_HAS_CLOAK);  
	} // end Constructor
	
	/** Supports text-oriented input / editing of attributes of an <i>Elf</i> including the inherited attributes of <i>Actor</i>. */
	@Override
	public void inputAllFields() {
		super.inputAllFields();
		hasInvisibilityCloak = Input.instance.getBoolean("Has Invisibility Cloak");
	} // end inputAllFields()
	
	/** overrides the superclass (<i>Actor</i>) version of <i>toString()</i> and provides a textual representation of the <i>Elf</i> object. It calls upon the <i>Actor</i> to assemble its <i>toString</i> components, then adds the <i>Elf</i>-specific details. */
	@Override
	public String toString() {
		return String.format("%s Invisibility Cloak:%b", super.toString(), hasInvisibilityCloak);
	} // end toString()

	/** Each subclass of <i>Actor</i> MUST define its own unique avatar, which can be ANY kind of <i>Node</i>. It MUST because the method <i>createAvatar()</i> is defined as an <i>abstract</i> method. The <i>Elf</i> creates a <i>Rectangle</i> object. */
	@Override
	public void createAvatar() {
		avatar = new Rectangle(6.0, 8.0, Color.GREENYELLOW);
	}

	/** Even though avatar is defined as a <i>Rectangle</i> (in the <i>Elf</i> class), it is returned as a reference-to-<i>Node</i>, to simplify use of the avatar object elsewhere in the program. */
	@Override
	public Node getAvatar() {	return avatar; }

	@Override
	protected Point2D findNewLocation(Actor opponent) {
		// TODO Auto-generated method stub
		return new Point2D((getAvatar().getTranslateX())+(opponent.getAvatar().getTranslateX())/2.0, ((getAvatar().getTranslateX())+opponent.getAvatar().getTranslateY())/2.0);
	}

	@Override
	public boolean isVisible() {
		return ! hasInvisibilityCloak;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		 out.writeBoolean(hasInvisibilityCloak);     // SimpleDoubleProperty name is NOT serializable, so I do it manually
		 } // end writeObject() to support serialization

		  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
		  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		  hasInvisibilityCloak = in.readBoolean();
		 } // end readObject() to support serialization

} // end class Elf
