package actor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.text.Text;
import javafx.util.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;
import army.*;
import util.*;

/**
 * The <i>Actor</i> class is the super class for all different types of <i>Actor</i> subclasses. The <i>Actor</i> class tracks state information for individual actors in the simulation: <i>name</i>, <i>health</i>, <i>strength</i>, <i>speed</i>, etc (and later, a screen avatar with coordinates). Additional attributes are tracked in the subclasses. The behaviours
 * (moving and battling) are defined in the subclasses. <i>Actor</i> class is currently a concrete class. Later, the <i>Actor</i> class will become an <i>abstract</i> class where no <i>Actor</i> objects will ever be created -- only subclass objects.
 * 
 * @author Rex Woollard
 * @version Lab Assignment 4: <i>The Hobbit Battlefield Simulator</i>
 */
public abstract class Actor implements Serializable { // Could also be written as " public class Actor extends Object { " but no one ever does . . . why waste time typing something that isn't required.
	// Series of constants which are common to all objects. No instances of these values reside in any Actor objects.
	// The keyword "static" makes a single item (such as MAX_STRENGTH) common to all Actor objects. The keyword "final" makes an item constant.

	/** static variable used to embed sequence number in <i>Actor</i> names; because it is static, there is one-and-only-one instance of this variable regardless of the number of Actor objects in existence (from none to infinity). */
	private static int actorSerialNumber = 0;
	
	// INSTANCE FIELDS: Each Actor object will have its own independent set of instance fields
	/** stores the actual Actor's name, for example, "<i>Gandalf the Gray</i>" */
	private SimpleStringProperty name = new SimpleStringProperty();
	/** captures a reference-to-String value stored in local, stack-oriented local variable <i>name</i>, stores in heap-oriented SimpleStringProperty to implement the <i>Observable</i> design pattern. */
	public void setName(String name) { this.name.set(name); }
	/** returns extracts and returns a copy of the reference-to-String value stored in <i>name SimpleStringProperty</i>; safe to return because <i>String</i> is immutable */
	public String getName() { return name.get(); }

	/** Upper boundary on <i>strength</i> attribute, currently:{@value} */
	public static final double MAX_STRENGTH = 100.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation // "public" is acceptable because it is "final." This provides READ-ONLY access to the value.
	/** Lower boundary on <i>strength</i> attribute, currently:{@value} */
	public static final double MIN_STRENGTH = 10.0; // the use of the JavaDoc tag {@value} causes the constant value to be included in the documentation  // "public" is acceptable because it is "final." This provides READ-ONLY access to the value.
	/** influences degree of damage inflicted in skirmish with other players; stored in a SimpleDoubleProperty to implement the <i>Observable</i> design pattern. */
	private SimpleDoubleProperty strength = new SimpleDoubleProperty();
	/** captures a value (within a guaranteed range) in the field <i>strength</i>  */
	public void setStrength(double simpleDoubleProperty) { 
		if (simpleDoubleProperty<MIN_STRENGTH) 
			simpleDoubleProperty = MIN_STRENGTH; 
		else if (simpleDoubleProperty>MAX_STRENGTH) 
			simpleDoubleProperty = MAX_STRENGTH; 
		this.strength.set(simpleDoubleProperty); 
		}
	/** returns a copy of the <i>double</i> value stored in <i>strength</i> */
	public double getStrength() { return strength.get(); }
	
	/** Upper boundary on <i>health</i> attribute, currently:{@value} */
	public static final double MAX_HEALTH = 100.0; // "public" is acceptable because it is "final." This provides READ-ONLY access to the value.
	/** Lower boundary on <i>health</i> attribute, currently:{@value} */
	public static final double MIN_HEALTH = 1.0; // "public" is acceptable because it is "final." This provides READ-ONLY access to the value.
	/** Defines threshold for ability to move; value is 0.0 to 1.0, currently:{@value} */
	public static final double THRESHOLD_OF_ADEQUATE_HEALTH = 0.3; // effectively 30%
	/** influences ability to survive damage inflicted in skirmish with other players; can also influence mobility (along with speed); stored in a SimpleDoubleProperty to implement the <i>Observable</i> design pattern. */
	private SimpleDoubleProperty health = new SimpleDoubleProperty();
	/** captures a value (within a guaranteed range) in the field <i>health</i>  */
	public void setHealth(double health) {
		if (health < MIN_HEALTH)
			health = MIN_HEALTH;
		else if (health > MAX_HEALTH)
			health = MAX_HEALTH;
		this.health.set(health);
	}
	/** applies <i>changeToValue</i> to the <i>health</i> attribute. */
	public void adjustHealth(double changeToValue) { health.set(health.get() + changeToValue); }
	/** returns a copy of the <i>double</i> value stored in <i>health</i> */
	public double getHealth() { return health.get(); }

	/** Upper boundary on <i>speed</i> attribute, currently:{@value} */
	public static final double MAX_SPEED = 100.0; // "public" is acceptable because it is "final." This provides READ-ONLY access to the value.
	/** Lower boundary on <i>speed</i> attribute, currently:{@value} */
	public static final double MIN_SPEED = 10.0; // "public" is acceptable because it is "final." This provides READ-ONLY access to the value. 
	/** influences speed of movement; stored in a SimpleDoubleProperty to implement the <i>Observable</i> design pattern. */
	private SimpleDoubleProperty speed = new SimpleDoubleProperty();
	/** captures a value (within a guaranteed range) in the field <i>speed</i>  */
	public void setSpeed(double speed) {
		if (speed < MIN_SPEED)
			speed = MIN_SPEED;
		else if (speed > MAX_SPEED)
			speed = MAX_SPEED;
		this.speed.set(speed);
	} // end setSpeed()
	/** returns a copy of the <i>double</i> value stored in <i>speed</i> */
	public double getSpeed() { return speed.get(); }
	
	/** Used to assist in finding nearest opposing <i>Actor</i>; this <i>Actor</i? can ask its <i>Army</i> search the opposing <i>Army</i>, thus the <i>Army</i> will need to know who the opposing <i>Army</i> is, but that's the <i>Army</i> responsibility. */
	@SuppressWarnings("unused") // This will be used in the next phase of development, at which point, we can remove the @SuppressWarnings("unused"). In general, @SuppressWarnings("unused") is BAD FORM . . . but we are committed to its removal. 
	protected Army armyAllegiance; 
	/** Used to manage the avatar motion under JavaFX. */
	private TranslateTransition tt;
	/** Is associated with the avatar and is used to display on-screen information about the <i>Actor</i> object. */
	private Tooltip tooltip;
	
	/**
	 * <i>Actor</i> constructor is used when building <i>Actor</i> objects automatically: <i>strength</i>, <i>health</i>, <i>speed</i> fields are given randomly generated values within their range; <i>name</i> is given a sequentially numbered name: <i>Auto:<b>n</b></i> where
	 * <i><b>n</b></i> is the sequence number. The <i>name</i> can be edited to create an unique <i>Actor</i>.
	 * @param subclassCount used to support automatic naming (which includes a unique serial number).
	 * @param armyAllegiance used to support the <i>Army</i>-specific <i>DropShadow</i> glow around this Actor object.  
	 */
	public Actor(int subclassCount, Army armyAllegiance) {
		this.armyAllegiance = armyAllegiance;
		++actorSerialNumber; // static class-oriented variable. There is one-and-only-one instance of this variable regardless of the number of Actor objects in existence (from none to infinity).
		setName(String.format("%d:%s:%d:", actorSerialNumber, getClass().getSimpleName(), subclassCount)); // An alternate way to assemble a String to use as a name. Because of polymorphism "getClass().getName()" will return the subclass name when they exist.
		setStrength(SingletonRandom.instance.getNormalDistribution(MIN_STRENGTH, MAX_STRENGTH, 2.0));
		setHealth(SingletonRandom.instance.getNormalDistribution(MIN_HEALTH, MAX_HEALTH, 2.0));
		setSpeed(SingletonRandom.instance.getNormalDistribution(MIN_SPEED, MAX_SPEED, 2.0));
		createAvatar();
		tooltip = new Tooltip(toString());
		Tooltip.install(getAvatar(), tooltip);
		tt = new TranslateTransition(); tt.setNode(getAvatar()); // reuse
	} // end Actor constructor

	/** sets all <i>Actor</i> fields, guaranteeing values within the specified range. Later, it will be treated as a virtual method, and subclasses will call this (the superclass method) to perform its work. */
	public void inputAllFields() {
		setName(Input.instance.getString(getClass().getSimpleName()+":Current Name:"+name+" New Name:"));
		setStrength(Input.instance.getDouble(String.format("Strength:%.1f",strength), MIN_STRENGTH, MAX_STRENGTH));
		setHealth(Input.instance.getDouble(String.format("Health:%.1f",health), MIN_HEALTH, MAX_HEALTH));
		setSpeed(Input.instance.getDouble(String.format("Speed:%.1f",speed), MIN_SPEED, MAX_SPEED));
	} // end void inputAllFields()

	/** <i>Actor</i> regain health on each cycle of the simulation (and loose health in battles handled by other code). */
	public void gameCycleHealthGain() {
		final double MAX_CYCLE_HEALTH_GAIN = 2.0;
		adjustHealth(Math.random()*MAX_CYCLE_HEALTH_GAIN);
	}
	
	public double getHitPoints() {
		return getStrength()+getHealth()*.5 * Math.random();
	}
	
	/** 
	 * processes a single round of combat between two Actor objects: the <b>attacker</b> is this object; the <b>defender</b> is received as an argument.
	 * This method is called by the <b>attacker</b> <i>Actor</i> object.
	 * This <b>attacker</b> <i>Actor</i> object chooses another <i>Actor</i> object as the <b>defender</b> by sending a reference-to the second <i>Actor</i> object.
	 * When program execution arrives in <i>combatRound</i>, the method will have access to 2 sets of <i>Actor</i> attributes (a.k.a. instance fields).
	 * In particular, this method will need to use <i>health</i> and <i>strength</i> to process a single round of combat. 
	 * As an outcome of the single round, both <i>Actor</i> objects: the <b>attacker</b> and the <b>defender</b> are likely to loose some <i>health</i> value, but the <i>Actor</i> object with less <i>strength</i> will likely incur more damage to their <i>health</i>.
	 * You access the <b>attacker</b> instance fields (such as <i>health</i> using <i>this.health</i> and the <b>defender</b> instance fields using <i>defender.health</i>.
	 * Of course, <i>defender</i> is the name of the stack-oriented reference-to variable that is sent to the method.
	 * @param defender a reference to a different <i>Actor</i> object that will engage in combat with this <i>Actor</i> object.
	 * @return <i>health</i> of the <i>Actor</i> following the combat round. 
	 * */
	public double combatRound(Actor defender) {
		final double MAX_COMBAT_HEALTH_REDUCTION_OF_LOOSER = 10.0; // health ranges 0.0 to 100.0, thus could loose 0.0 to 10.0
		final double MAX_COMBAT_HEALTH_REDUCTION_OF_WINNER = 3.0; // could loose 0.0 to 3.0
		double healthAdjustmentOfLooser = -(Math.random()*MAX_COMBAT_HEALTH_REDUCTION_OF_LOOSER) - 1.0; // looser looses at least 1.0
		double healthAdjustmentOfWinner= -(Math.random()*MAX_COMBAT_HEALTH_REDUCTION_OF_WINNER) + 1.0; // winner gains at least 1.0

		double proportionHitPoints = getHitPoints() / (getHitPoints() + defender.getHitPoints()); // between 0.0 and 1.0
		if (Math.random() > proportionHitPoints) {
			adjustHealth(healthAdjustmentOfLooser);
			defender.adjustHealth(healthAdjustmentOfWinner);
		}
		else {
			defender.adjustHealth(healthAdjustmentOfLooser);
			adjustHealth(healthAdjustmentOfWinner);
		}
		return getHealth();
	} // end combatRound()

	/** 
	 * based on <i>health</i> determines if the <i>Actor</i> is healthy enough to move; returns a <i>true</i> or <i>false</i> value 
	 * @return boolean value representing whether the <i>Actor</i> can move.
	 * */
	public boolean isHealthyEnoughToMove() { return (getHealth() > MAX_HEALTH * THRESHOLD_OF_ADEQUATE_HEALTH); } // expression results in a boolean value

	/** overrides the superclass (<i>Object</i>) version of <i>toString()</i> and provides a textual representation of the <i>Actor</i> object. */
	@Override
	public String toString() {
		return String.format("Name:%-12s Health:%4.1f Strength:%4.1f Speed:%4.1f", getName(), getHealth(), getStrength(), getSpeed());
		//return name + " Strength:" + strength + " Speed:"+ speed + " Health:" + health; // it works but it's BAD FORM. Many extra String objects are created and thrown away. No field width control.
	} // end String toString()
	
	/** Each subclass will have a different look-and-feel. ALL subclasses MUST have and avatar, thus this <i>abstract</i> method enforces that requirement. */
	public abstract void createAvatar();
	/** Each subclass can have a different <i>Node</i> (e.g. <i>ImageView</i> or <i>Rectangle</i> or <i>Circle</i> etc. They all return their reference-to as type <i>Node</i>, and <i>Node</i> is defined as an <i>abstract</i> type. */
	public abstract Node getAvatar();
	
	/** Defines the characteristics of a <i>TranslateTransition</i>.
	 * Each call results in ONE segment of motion. When that segment is finished, it "chains" another call to <i>startMotion()</i> (which is NOT recursion)!
	 * The initial call is made by the managing <i>Army</i> object; subsequent calls are made through the "chaining" process described here.
	 * @param engageInCombat TODO
	 */
	public void startMotion(boolean engageInCombat) {
		Army opposingArmy = armyAllegiance.getOpposingArmy();
		Actor opponent = opposingArmy.findNearestOpponent(this); // could legitimately return a null: 1) no one is visible 2) no Actors in opposing army

		Point2D newLocation;
		if (opponent != null) {
			System.out.printf("ToMove:[%.1f:%.1f] Opponent:[%.1f:%.1f]\n", getAvatar().getTranslateX(), getAvatar().getTranslateY(), opponent.getAvatar().getTranslateX(), opponent.getAvatar().getTranslateX());
			double DISTANCE_FOR_BATTLE = 50.0;
			if (engageInCombat && distanceTo(opponent) < DISTANCE_FOR_BATTLE) {
				double h1, h2, h3, h4; // debug code
				h1 = this.getHealth();
				h2 = opponent.getHealth();

				combatRound(opponent);
				h3 = this.getHealth();
				h4 = opponent.getHealth();
				h4 = h4;
				if (this.getHealth() <= 0.0) {
					armyAllegiance.removeNowDeadActor(this);
				}
				if (opponent.getHealth() <= 0.0) {
					opponent.armyAllegiance.removeNowDeadActor(opponent);
				}
			} // end if (combat)
			newLocation = findNewLocation(opponent);
		} else // end if (test for null opponent)
			newLocation = meander(); // null opponent means we wander around close to our current location

		if (tt.getStatus() != Animation.Status.RUNNING) { // if NOT yet RUNNING, start . . . otherwise, do nothing.
		// tt.setToX(Math.random()*getAvatar().getScene().getWidth()); tt.setToY(Math.random()*getAvatar().getScene().getHeight());
			tt.setToX(validateCoordinate(newLocation).getX());
			tt.setToY(validateCoordinate(newLocation).getY());
			tt.setDuration(Duration.seconds(MAX_SPEED / (getSpeed() * (armyAllegiance.getSpeedControllerValue()))));
			tt.setOnFinished(event -> startMotion(true)); // NOT RECURSION!!!!
			tt.play(); // give assembled object to the render engine (of course, play() is an object-oriented method which has access to "this" inside, and it can use "this" to give to the render engine.
		}
	} // end startMotion()
	
	private Point2D validateCoordinate(Point2D possibleNewLocation){
		double maxY = armyAllegiance.getScene().getHeight();
		double maxX = armyAllegiance.getScene().getWidth();
		double myX = possibleNewLocation.getX();
		double myY = possibleNewLocation.getY();
		double newX=0.0;
		double newY=0.0;
		if ((myX < 0) && (myY < 0)){
			newX = SingletonRandom.instance.getNormalDistribution(0.0, (0.25*maxX), 2.0);
			newY = SingletonRandom.instance.getNormalDistribution(0.0, (0.25*maxY), 2.0);
			return new Point2D(newX, newY);
		}
		else if ((0 < myX) && (myX < maxX) && (myY < 0)){
			newY = SingletonRandom.instance.getNormalDistribution(0.0, (0.25*maxY), 2.0);
			return new Point2D(myX, newY);
		}
		else if ((myX > maxX) && (myY < 0)){
			newX = SingletonRandom.instance.getNormalDistribution(0.0, (0.75*maxX), 2.0);
			newY = SingletonRandom.instance.getNormalDistribution(0.0, (0.75*maxY), 2.0);
			return new Point2D(newX, newY);
		}
		else if ((0 < myX) && (myY < maxY) && (myY > 0)){
			newX = SingletonRandom.instance.getNormalDistribution(0.0, (0.25*maxX), 2.0);
			return new Point2D(newX, myY);
		}
		else if ((myX > maxX) && (myY < maxY) && (myY > 0)){
			newX = SingletonRandom.instance.getNormalDistribution(0.0, (0.75*maxX), 2.0);
			return new Point2D(newX, myY);
		}
		if ((myX < 0) && (myY > maxY)){
			newX = SingletonRandom.instance.getNormalDistribution(0.0, (0.25*maxX), 2.0);
			newY = SingletonRandom.instance.getNormalDistribution(0.0, (0.75*maxY), 2.0);
			return new Point2D(newX, newY);
		}
		else if ((0 < myX) && (myX < maxX) && (myY > maxY)){
			newY = SingletonRandom.instance.getNormalDistribution(0.0, (0.75*maxY), 2.0);
			return new Point2D(myX, newY);
		}
		else if ((myX > maxX) && (myY > maxY)){
			newX = SingletonRandom.instance.getNormalDistribution(0.0, (0.75*maxX), 2.0);
			newY = SingletonRandom.instance.getNormalDistribution(0.0, (0.75*maxY), 2.0);
			return new Point2D(newX, newY);
		}
		else {
			return new Point2D(myX, myY);
		}
	}


	private double distanceTo(Actor opponent) {
		double actorToMoveX = opponent.getAvatar().getTranslateX();
		double actorToMoveY = opponent.getAvatar().getTranslateY();
		double currentX = getAvatar().getTranslateX();
		double currentY = getAvatar().getTranslateY();
		double deltaX = actorToMoveX - currentX;
		double deltaY= actorToMoveY - currentY;
		double calculatedDistance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
		return calculatedDistance;
	}
	
	protected abstract Point2D findNewLocation(Actor opponent);
	
	protected Point2D meander() {
		final double RANGE_OF_MEANDERING = 20.0;
		double myX = getAvatar().getTranslateX();
		double myY = getAvatar().getTranslateY();
		return new Point2D(
				SingletonRandom.instance.getNormalDistribution(myX-RANGE_OF_MEANDERING, myX+RANGE_OF_MEANDERING, 2.0),
				SingletonRandom.instance.getNormalDistribution(myY-RANGE_OF_MEANDERING, myY+RANGE_OF_MEANDERING, 2.0));
	}
	
	/** Pauses a <i>TranslateTransition</i> if it is actively running. */
	public void pauseMotion() {
		if (tt.getStatus() == Animation.Status.RUNNING)
			tt.pause();
	} // end suspendMotion()

	/** createTable is static to allow Army to define a table without having any Actor objects present. */
	 public static TableView<Actor> createTable() {
	 TableView<Actor> table = new TableView<Actor>();
	 final double PREF_WIDTH_DOUBLE = 80.0;
	 table.setPrefWidth(PREF_WIDTH_DOUBLE*7.5); // 7.0 because there are 6 individual columns, but one of those is DOUBLE-WIDTH, and there is some inter-column spacing
	 table.setEditable(true);
	 
	 TableColumn<Actor, String> nameCol      = new TableColumn<>("Name");     nameCol.setCellValueFactory     (new PropertyValueFactory<Actor, String>("name"));         nameCol.setPrefWidth(PREF_WIDTH_DOUBLE*2.0);
	 TableColumn<Actor, Number> healthCol    = new TableColumn<>("Health");   healthCol.setCellValueFactory   (cell->cell.getValue().health);       healthCol.setPrefWidth(PREF_WIDTH_DOUBLE);
	 TableColumn<Actor, Number> strengthCol  = new TableColumn<>("Strength"); strengthCol.setCellValueFactory (cell->cell.getValue().strength);     strengthCol.setPrefWidth(PREF_WIDTH_DOUBLE);
	 TableColumn<Actor, Number> speedCol     = new TableColumn<>("Speed");    speedCol.setCellValueFactory    (cell->cell.getValue().speed);        speedCol.setPrefWidth(PREF_WIDTH_DOUBLE);
	 TableColumn<Actor, Number> locationXCol = new TableColumn<>("X");        locationXCol.setCellValueFactory(cell-> cell.getValue().getAvatar().translateXProperty()); locationXCol.setPrefWidth(PREF_WIDTH_DOUBLE); 
	 TableColumn<Actor, Number> locationYCol = new TableColumn<>("Y");        locationYCol.setCellValueFactory(cell-> cell.getValue().getAvatar().translateYProperty()); locationYCol.setPrefWidth(PREF_WIDTH_DOUBLE); 
	 ObservableList<TableColumn<Actor, ?>> c = table.getColumns(); c.add(nameCol); c.add(healthCol); c.add(strengthCol); c.add(speedCol); c.add(locationXCol); c.add(locationYCol);
	 // Compare line ABOVE with line BELOW: The BELOW line looks cleaner and does actually work . . . but the compiler spits out a warning. The ABOVE line accomplishes the same thing, less elegantly, but without warnings.
	 // table.getColumns().addAll(nameCol, healthCol, strengthCol, speedCol, locationXCol, locationYCol);
	 
	 // The following code makes each cell in the selected columns editable (Name, Health, Strength, Speed)
	 // We CANNOT implement edit capabilities on the X/Y columns since they are READ-ONLY.
	 nameCol.setCellFactory(TextFieldTableCell.<Actor>forTableColumn());
	 nameCol.setOnEditCommit(event-> { Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow())); a.setName(event.getNewValue()); a.resetAvatarAttributes(); });
	 
	 healthCol.setCellFactory(TextFieldTableCell.<Actor, Number>forTableColumn(new NumberStringConverter()));
	 healthCol.setOnEditCommit(event-> { Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow())); a.setHealth((Double)event.getNewValue()); a.resetAvatarAttributes(); });
	 
	 strengthCol.setCellFactory(TextFieldTableCell.<Actor, Number>forTableColumn(new NumberStringConverter()));
	 strengthCol.setOnEditCommit(event-> { Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow())); a.setStrength((Double)event.getNewValue()); a.resetAvatarAttributes(); });
	 
	 speedCol.setCellFactory(TextFieldTableCell.<Actor, Number>forTableColumn(new NumberStringConverter()));
	 speedCol.setOnEditCommit(event-> { Actor a = (event.getTableView().getItems().get(event.getTablePosition().getRow())); a.setSpeed((Double)event.getNewValue()); a.resetAvatarAttributes(); });
	 
	 return table;
	 } // end createTable()

  
	public void resetAvatarAttributes() { tooltip.setText(toString()); } // Note: This updates the text in the Tooltip that was installed earlier. We re-use the originally installed Tooltip.
	public abstract boolean isVisible();
	
	public void setArmyAllegiance(Army army) {
		// TODO Auto-generated method stub
		
	}
	
	 // Explicit implementation of writeObject, but called implicitly as a result of recursive calls to writeObject() based on Serializable interface
	  private void writeObject(ObjectOutputStream out) throws IOException {
	 out.writeObject(getName());     // SimpleDoubleProperty name is NOT serializable, so I do it manually
	 out.writeDouble(getStrength()); // SimpleDoubleProperty strength is NOT serializable, so I do it manually
	 out.writeDouble(getHealth());   // SimpleDoubleProperty health is NOT serializable, so I do it manually
	 out.writeDouble(getSpeed());    // SimpleDoubleProperty speed is NOT serializable, so I do it manually
	 out.writeDouble(getAvatar().getTranslateX()); // Node battlefieldAvatar is NOT serializable. It's TOO BIG anyway, so I extract the elements that I need (here, translateX property) to retain manually.
	 out.writeDouble(getAvatar().getTranslateY()); // Node battlefieldAvatar is NOT serializable. It's TOO BIG anyway, so I extract the elements that I need (here, translateY property) to retain manually.
	 } // end writeObject() to support serialization

	  // Explicit implementation of readObject, but called implicitly as a result of recursive calls to readObject() based on Serializable interface
	  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	 name = new SimpleStringProperty((String) in.readObject());
	 strength = new SimpleDoubleProperty(in.readDouble());
	 health = new SimpleDoubleProperty(in.readDouble());
	 speed = new SimpleDoubleProperty(in.readDouble());
	 createAvatar();
	 getAvatar().setTranslateX(in.readDouble());
	 getAvatar().setTranslateY(in.readDouble());
	 tooltip = new Tooltip(toString());
	 Tooltip.install(getAvatar(), tooltip);
	 resetAvatarAttributes();
	 } // end readObject() to support serialization

} // end class Actor