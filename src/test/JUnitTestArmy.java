package test;

import static org.junit.Assert.*;
import javafx.scene.paint.Color;

import org.junit.Test;

import army.*;
import actor.*;

/**
 * Simple jUnit class to test behaviour of <i>Army</i> and <i>Actor</i> classes
 * 
 * @author Ammar Khan
 * @see Army
 * @see Actor
 * @version Lab 4 Assignment
 */
public class JUnitTestArmy {

	@Test
	public void test() {
		Army forcesOfLight = new Army("Forces of Light", null, Color.RED);
		final int NUM_ELF = 5;
		forcesOfLight.populate(ActorFactory.Type.ELF, NUM_ELF);
		final int NUM_HOBBIT = 6;
		forcesOfLight.populate(ActorFactory.Type.HOBBIT, NUM_HOBBIT);
		final int NUM_ORC = 7;
		forcesOfLight.populate(ActorFactory.Type.ORC, NUM_ORC);
		final int NUM_WIZARD = 8;
		forcesOfLight.populate(ActorFactory.Type.WIZARD, NUM_WIZARD);
		forcesOfLight.display();
		assertTrue("Number Actors created mismatches number stored", forcesOfLight.getSize() == NUM_ELF+NUM_HOBBIT+NUM_ORC+NUM_WIZARD);
	}

}
