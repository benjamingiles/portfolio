package Fill;

import com.google.gson.Gson;
import dao.DataAccessException;
import dao.EventDAO;
import dao.PersonDAO;
import model.Event;
import model.Person;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.util.Random;
import java.util.UUID;

public class Fill {

  private String associatedUsername;
  private Connection conn;
  private Person user;
  private int personCount;
  private int eventCount;

  public int getPersonCount() {
    return personCount;
  }

  public void setPersonCount(int personCount) {
    this.personCount = personCount;
  }

  public int getEventCount() {
    return eventCount;
  }

  public void setEventCount(int eventCount) {
    this.eventCount = eventCount;
  }

  public Fill(String associatedUsername, Connection conn, Person user) {
    this.associatedUsername = associatedUsername;
    this.conn = conn;
    this.user = user;
    personCount = 0;
    eventCount = 0;
  }

  public Person generatePerson(String gender, int generations, boolean first, int year) throws DataAccessException, FileNotFoundException {
    Person mother = null;
    Person father = null;
    String eventID;
    EventDAO eventDAO = new EventDAO(conn);
    PersonDAO personDAO = new PersonDAO(conn);

    Gson gson = new Gson();
    Reader reader = new FileReader("json/fnames.json");
    Name fnames = gson.fromJson(reader, Name.class);
    reader = new FileReader("json/mnames.json");
    Name mnames = gson.fromJson(reader, Name.class);
    reader = new FileReader("json/snames.json");
    Name snames = gson.fromJson(reader, Name.class);
    reader = new FileReader("json/locations.json");
    LocationData locationData = gson.fromJson(reader, LocationData.class);

    Random random = new Random();
    int index = 0;

    if (generations > 0) {
      mother = generatePerson("f", generations - 1, false, year - 23);
      father = generatePerson("m", generations - 1, false, year - 23);

      mother.setSpouseID(father.getPersonID());
      father.setSpouseID(mother.getPersonID());
      personDAO.updateSpouseID(mother.getPersonID(), mother.getSpouseID());
      personDAO.updateSpouseID(father.getPersonID(), father.getSpouseID());

      index = random.nextInt(978);
      eventID = UUID.randomUUID().toString();
      Location location = locationData.getData()[index];
      Event marriage = new Event(eventID, associatedUsername, mother.getPersonID(), location.getLatitude(), location.getLongitude(),
              location.getCountry(), location.getCity(), "marriage", year + 20);

      eventDAO.createEvent(marriage);
      ++eventCount;

      eventID = UUID.randomUUID().toString();
      marriage = new Event(eventID, associatedUsername, father.getPersonID(), location.getLatitude(), location.getLongitude(),
              location.getCountry(), location.getCity(), "marriage", year + 20);
      eventDAO.createEvent(marriage);
      ++eventCount;
    }


    UUID uuid = UUID.randomUUID();
    String personID = uuid.toString();
    String motherID = null;
    String fatherID = null;
    if (mother != null) {
      motherID = mother.getPersonID();
    }
    if (father != null) {
      fatherID = father.getPersonID();
    }


    index = random.nextInt(152);

    if (!first) {
      String lastName = snames.getNames()[index];
      String firstName;

      if (gender.equals("f")) {
        index = random.nextInt(147);
        firstName = fnames.getNames()[index];
      } else {
        index = random.nextInt(144);
        firstName = mnames.getNames()[index];
      }

      Person person = new Person(personID, associatedUsername, firstName, lastName, gender, fatherID, motherID, null);
      personDAO.createPerson(person);
      ++personCount;

      index = random.nextInt(978);
      eventID = UUID.randomUUID().toString();
      Location location = locationData.getData()[index];
      Event birth = new Event(eventID, associatedUsername, person.getPersonID(), location.getLatitude(), location.getLongitude(),
              location.getCountry(), location.getCity(), "birth", year);
      eventDAO.createEvent(birth);
      ++eventCount;

      index = random.nextInt(978);
      eventID = UUID.randomUUID().toString();
      location = locationData.getData()[index];
      Event death = new Event(eventID, associatedUsername, person.getPersonID(), location.getLatitude(), location.getLongitude(),
              location.getCountry(), location.getCity(), "death", year + 80);

      eventDAO.createEvent(death);
      ++eventCount;

      return person;
    } else {
      user.setFatherID(fatherID);
      user.setMotherID(motherID);
      personDAO.createPerson(user);
      ++personCount;

      index = random.nextInt(978);
      eventID = UUID.randomUUID().toString();
      Location location = locationData.getData()[index];
      Event birth = new Event(eventID, associatedUsername, user.getPersonID(), location.getLatitude(), location.getLongitude(),
              location.getCountry(), location.getCity(), "birth", year);

      eventDAO.createEvent(birth);
      ++eventCount;
    }
    return user;
  }
}
