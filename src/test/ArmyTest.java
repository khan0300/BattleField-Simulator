package test;

import javafx.scene.paint.Color;
import util.*;
import actor.*;
import army.*;

/**
 * Simple class to test behaviour of <i>Army</i> and <i>Actor</i> classes. This is NOT jUnit based.
 * 
 * @author Ammar Khan
 * @see Army
 * @see Actor
 * @version Lab 4 Assignment
 */
public class ArmyTest {
	public static void main(String[] args) {
		Army forcesOfLight = new Army("Forces of Light", null, Color.RED);  // testing without a coupled Simulator object, thus "null" is an argument
		forcesOfLight.populate(ActorFactory.Type.HOBBIT, 4);
		forcesOfLight.populate(ActorFactory.Type.ELF, 3);
		forcesOfLight.populate(ActorFactory.Type.WIZARD, 2);
		forcesOfLight.display();
		forcesOfLight.edit(Input.instance.getInt("Index to Edit", 0, forcesOfLight.getSize()-1));
		forcesOfLight.display();

		Army forcesOfDarkness = new Army("Forces of Darkness", null, Color.GREEN);   // testing without a coupled Simulator object, thus "null" is an argument
		forcesOfDarkness.populate(ActorFactory.Type.ORC, 4);
		forcesOfDarkness.display();
		forcesOfDarkness.edit(Input.instance.getInt("Index to Edit", 0, forcesOfDarkness.getSize()-1));
		forcesOfDarkness.display();
		
		Army forcesRandom = new Army("Random Forces", null, Color.BLUE);  // testing without a coupled Simulator object, thus "null" is an argument
		forcesRandom.populate(ActorFactory.Type.RANDOM, 100);
		forcesRandom.display();
	} // end main()
} // end class ArmyTest
