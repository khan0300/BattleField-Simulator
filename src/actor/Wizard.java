package actor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.*;
import army.Army;
import util.Input;

/**
 * The <i>Wizard</i> class is a subclass of <i>Actor</i>. The <i>Actor</i> class tracks state information for individual actors in the simulation: <i>name</i>, <i>health</i>, <i>strength</i>, <i>speed</i>, etc (and later, a screen avatar with coordinates). Additional attributes are tracked in the subclasses. The behaviours
 * (moving and battling) are defined in the subclasses. The <i>Actor</i> class is <i>abstract</i>, thus no <i>Actor</i> objects will ever be created -- only subclass objects, such as <i>Wizard</i>.
 * The <i>Wizard</i> class adds characteristics that are unique to elves. 
 * 
 * @author Ammar Khan
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 * @see Actor
 * @see ActorFactory
 * @see Army
 */
public class Wizard extends Actor {
	/** Probability used in generating a true/false boolean value for hasStaff {@value} */
	private final double PROBABILITY_WIZARD_HAS_STAFF = 0.8; // true 80% of the time on random generation
	/** Probability used in generating a true/false boolean value for hasHorse {@value} */
	private final double PROBABILITY_WIZARD_HAS_HORSE = 0.25;// true 25% of the time on random generation
	/** class-oriented variable used to generate a unique identifier for each new <i>Wizard</i> object */
	private static int wizardCount = 0;
	
	/** Gives extra power in combat, and extra speed in moving */
	private boolean hasStaff;
	public void setHasStaff(boolean hasStaff) { this.hasStaff = hasStaff; }
	public boolean getHasStaff() { return hasStaff; }
	/** Results in extra speed in moving */
	private boolean hasHorse;
	public void setHasHorse(boolean hasHorse) { this.hasHorse = hasHorse; }
	public boolean getHasHorse() { return hasHorse; }
	
	/** This subclass of <i>Actor</i> defines its own unique avatar, which can be ANY kind of <i>Node</i>, but for <i>Wizard</i> it will be <i>ImageView</i> based on a .GIF file that has an animated image of a wizard. */
	private ImageView avatar;

	/** supports the generation of random values for automatically created objects. allegiance s*/
	public Wizard(Army armyAllegiance) {
		super(++wizardCount, armyAllegiance); // calls immediate superclass (which is currently Actor, but could change if redesigned)
		hasStaff = (Math.random() < PROBABILITY_WIZARD_HAS_STAFF);  
		hasHorse = (Math.random() < PROBABILITY_WIZARD_HAS_HORSE); // true 25% of the time 
	}

	/** Supports text-oriented input / editing of attributes of a <i>Wizard</i> including the inherited attributes of <i>Actor</i>. */
	@Override public void inputAllFields() {
		super.inputAllFields(); // calls immediate superclass (which is currently Actor, but could change if redesigned)
		hasStaff = Input.instance.getBoolean("Has Staff:");
		hasHorse = Input.instance.getBoolean("Has Horse:");
	} // end inputAllFields()
	
	/** overrides the superclass (<i>Actor</i>) version of <i>toString()</i> and provides a textual representation of the <i>Wizard</i> object. It calls upon the <i>Actor</i> to assemble its <i>toString</i> components, then adds the <i>Wizard</i>-specific details. */
	@Override public String toString() {
		return String.format("%s Staff:%b Horse:%b", super.toString(), hasStaff, hasHorse);
	}
	
	/** Each subclass of <i>Actor</i> MUST define its own unique avatar, which can be ANY kind of <i>Node</i>. It MUST because the method <i>createAvatar()</i> is defined as an <i>abstract</i> method. The <i>Wizard</i> creates an <i>ImageView</i> based on a .GIF file that has an animated image of a wizard. */
	@Override
	public void createAvatar() {
		try {
			avatar = new ImageView(new Image(new FileInputStream("AnimatedWizard-1.gif"))); // avatar could have been defined as type Node and it would work on this line.
			avatar.setFitWidth(30.0); // However, on THIS LINE avatar CANNOT be defined as type Node, since we need to call a method (setFitWidth()) that is not available in the Node layer (that is, part of the Node "contract")
			avatar.setPreserveRatio(true); // Similar need to define avatar as type ImageView on THIS LINE.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	} // create Avatar()
	
	/** Even though avatar is defined as an <i>ImageView</i> (in the <i>Wizard</i> class), it is returned as a reference-to-<i>Node</i>, to simplify use of the avatar object elsewhere in the program. */
	@Override
	public Node getAvatar() {	return avatar; }
	@Override
	protected Point2D findNewLocation(Actor opponent) {
		return new Point2D((getAvatar().getTranslateX())+(opponent.getAvatar().getTranslateX())/2.0, ((getAvatar().getTranslateX())+opponent.getAvatar().getTranslateY())/2.0);
		}
	@Override
	public boolean isVisible() {
		return ! hasStaff;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		 out.writeBoolean(hasStaff);     // SimpleDoubleProperty name is NOT serializable, so I do it manually
		 out.writeBoolean(hasHorse);     // SimpleDoubleProperty name is NOT serializable, so I do it manually		
		 } // end writeObject() to support serialization

		  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		  hasStaff = in.readBoolean();
		  hasHorse = in.readBoolean();
		 } // end readObject() to support serialization
} // end class Wizard