package DAO;

import Encoder.JsonSerializer;
import Model.*;
import Service.Request.LoginRequest;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.UUID;

public class Generator{

    private Names femaleNames;
    private Names maleNames;
    private Names surnames;
    private Locations locations;
    private int numPeople;
    private String associatedUsername;
    private PersonDAO pDao;
    private EventDAO eDao;
    private int yearOfBirth;
    private int yearOfMarriage;
    private int yearOfDeath;
    private int yearHolder;
    private int yearHolder2;
    //once a final variable has been assigned, it always contains the same value.
    private final Connection connection;

    public Generator(Connection connection) {
        this.connection = connection;
    }

    public void setUp() throws Exception {
        //we get the path to the female names file in a string
        String filePathFName = "json/fnames.json";
        femaleNames = objectNamesCreator(filePathFName);
        //we get the path to the female names file in a string
        String filePathMName = "json/mnames.json";
        maleNames = objectNamesCreator(filePathMName);
        //we get the path to the female names file in a string
        String filePathSName = "json/snames.json";
        surnames = objectNamesCreator(filePathSName);

        String filePathLocations = "json/locations.json";
        locations = objectLocationsCreator(filePathLocations);
    }

    public Locations objectLocationsCreator(String filePathLocations) throws Exception {
        Locations locations = new Locations();
        try {
            //we create a file and pass the path to find the file
            File fileLocations = new File(filePathLocations);
            //we convert the file to InputStream
            InputStream fileData = new FileInputStream(fileLocations);
            //we read and convert the input stream into a string
            String stringOfLocations = readString(fileData);
            //converting the String into an object
            locations = JsonSerializer.deserialize(stringOfLocations, Locations.class);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            System.out.println("Error occurred while trying to convert files into objects");
        }
        return locations;
    }
    public Names objectNamesCreator(String filePathName) throws Exception {
        Names names = new Names();
        try {
            //we create a file and pass the path to find the file
            File fileName = new File(filePathName);
            //we convert the file to InputStream
            InputStream fileData = new FileInputStream(fileName);
            //we read and convert the input stream into a string
            String stringOfNames = readString(fileData);
            //converting the String into an object
            names = JsonSerializer.deserialize(stringOfNames, Names.class);
        }
        catch(Exception exception) {
            exception.printStackTrace();
            System.out.println("Error occurred while trying to convert files into objects");
        }
        return names;
    }

    public void ancestorGenerator(Person userPerson, String username, int generations) throws DataAccessException {
        pDao = new PersonDAO(connection);
        eDao = new EventDAO(connection);
        int count = 0;
        associatedUsername = username;
        ArrayList<Person> descendant = new ArrayList<>();
        descendant.add(userPerson);
        numPeople = descendant.size();
        while(count <= generations - 1) {//we might have a problem if generations == 0
            ArrayList<Person> holder = generate(descendant);
            if (descendant.size() > 1) {
                for(int i = 0; i < descendant.size(); ++i) {
                    pDao.insert(descendant.get(i));
                }
            }
            descendant.clear();
            descendant = (ArrayList)holder.clone();
            ++count;
            //we record how many people have been generated
            numPeople = numPeople + descendant.size();
        }
        for(int i = 0; i < descendant.size(); ++i) {
            pDao.insert(descendant.get(i));
        }
    }

    public ArrayList<Person> generate(ArrayList<Person> children) throws DataAccessException {
        ArrayList<Person> parents = new ArrayList<>();
        if (children.size() == 1) {
            Person user = children.get(0);
            //we add the birth event of user by calling the userEvent method
            userEvent(user);
            parents = createParents(parents,user);
        }
        else {
            yearHolder2 = yearOfBirth;
            for(int i = 0; i < children.size();  ++i) {
                if (children.size() > 2) { yearOfBirth = yearHolder; }
                else {
                    yearOfBirth = yearHolder2;
                }
                Person child = children.get(i);
                parents = createParents(parents, child);
            }
            yearHolder = yearOfBirth;
        }
        return parents;
    }

    public ArrayList<Person> createParents(ArrayList<Person> parents, Person child) throws DataAccessException {
        //Father
        Person father = new Person();
        //we use UUID (Universal Unique Identifier) to generate random unique strings
        String fatherID = UUID.randomUUID().toString();
        //we assign the Id to the father
        father.setPersonID(fatherID);
        //the Associated Username will the the user that got passed in as a parameter
        father.setAssociatedUsername(associatedUsername);
        //we get a random Male name
        String fatherName = maleNames.getRandomName();
        father.setFirstName(fatherName);
        //since it is the father we use the child's lastname
        father.setLastName(child.getLastName());
        //father is always male
        father.setGender("M");
        //these IDs get null for right now until we generate their parents
        father.setFatherID(null);
        father.setMotherID(null);

        //Mother
        Person mother = new Person();
        //we use UUID (Universal Unique Identifier) to generate random unique strings
        String motherID = UUID.randomUUID().toString();
        //we assign the Id to the father
        mother.setPersonID(motherID);
        //the Associated Username will the the user that got passed in as a parameter
        mother.setAssociatedUsername(associatedUsername);
        //we get a random Male name
        String motherName = femaleNames.getRandomName();
        mother.setFirstName(motherName);
        //since it is the mother the lastname can ge random as well
        mother.setLastName(surnames.getRandomName());
        //mother is always female
        mother.setGender("F");
        //these IDs get null for right now until we generate their parents
        mother.setFatherID(null);
        mother.setMotherID(null);

        //we assign the child's FatherID and MotherID
        child.setFatherID(fatherID);
        child.setMotherID(motherID);

        //we set the spouseID for each other
        father.setSpouseID(motherID);
        mother.setSpouseID(fatherID);

        eventGenerator(father, mother);

        parents.add(father);
        parents.add(mother);

        return parents;
    }

    private String readString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        int length;
        while ((length = streamReader.read(buffer)) > 0) {
            stringBuilder.append(buffer, 0, length);
        }
        return stringBuilder.toString();
    }


    public void userEvent(Person person) throws DataAccessException {
        Event birth = new Event();
        //we use UUID (Universal Unique Identifier) to generate random unique strings
        String eventID = UUID.randomUUID().toString();
        birth.setEventID(eventID);
        birth.setUsername(associatedUsername);
        birth.setPersonID(person.getPersonID());
        //we get a random location from the Json file
        Location location = locations.getRandomLocation();
        birth.setLatitude(location.getLatitude());
        birth.setLongitude(location.getLongitude());
        birth.setCountry(location.getCountry());
        birth.setCity(location.getCity());
        birth.setEventType("Birth");
        //we get a random year between the years passed as parameters
        int birthYear = getRandomNumber(1997, 2000);
        yearOfBirth = birthYear;
        birth.setYear(birthYear);
        eDao.insert(birth);
    }

    public void eventGenerator(Person father, Person mother) throws DataAccessException {
        /*
        Father's Birth
        */
        Event fBirth = new Event();
        //we use UUID (Universal Unique Identifier) to generate random unique strings
        String eventID = UUID.randomUUID().toString();
        fBirth.setEventID(eventID);
        fBirth.setUsername(associatedUsername);
        fBirth.setPersonID(father.getPersonID());
        //we get a random location from the Json file
        Location location = locations.getRandomLocation();
        fBirth.setLatitude(location.getLatitude());
        fBirth.setLongitude(location.getLongitude());
        fBirth.setCountry(location.getCountry());
        fBirth.setCity(location.getCity());
        fBirth.setEventType("Birth");
        //we get a random year between the years passed as parameters
        int birthYear = getRandomNumber(yearOfBirth - 20, yearOfBirth - 18);
        while ((yearOfBirth - birthYear > 50) && (yearOfBirth - birthYear) < 13) {
            birthYear = getRandomNumber(yearOfBirth - 21, yearOfBirth - 18);
        }
        fBirth.setYear(birthYear);
        eDao.insert(fBirth);
        /*
        Mother's Birth
        */
        Event mBirth = new Event();
        //we use UUID (Universal Unique Identifier) to generate random unique strings
        eventID = UUID.randomUUID().toString();
        mBirth.setEventID(eventID);
        mBirth.setUsername(associatedUsername);
        mBirth.setPersonID(mother.getPersonID());
        //we get a random location from the Json file
        location = locations.getRandomLocation();
        mBirth.setLatitude(location.getLatitude());
        mBirth.setLongitude(location.getLongitude());
        mBirth.setCountry(location.getCountry());
        mBirth.setCity(location.getCity());
        mBirth.setEventType("Birth");
        //we get a random year between the years passed as parameters
        birthYear = getRandomNumber(yearOfBirth - 20, yearOfBirth - 18);
        yearOfMarriage = getRandomNumber(birthYear + 18, birthYear + 21);
        //we set minimum for year of death of parents
        yearOfDeath = yearOfBirth;
        //we set year of birth for next generation
        yearOfBirth = birthYear;
        mBirth.setYear(birthYear);
        eDao.insert(mBirth);
        /*
        Marriage
        */
        Event fMarriage = new Event();
        Event mMarriage = new Event();
        eventID = UUID.randomUUID().toString();
        fMarriage.setEventID(eventID);
        eventID = UUID.randomUUID().toString();
        mMarriage.setEventID(eventID);
        fMarriage.setUsername(associatedUsername);
        mMarriage.setUsername(associatedUsername);
        fMarriage.setPersonID(father.getPersonID());
        mMarriage.setPersonID(mother.getPersonID());
        location = locations.getRandomLocation();
        fMarriage.setLatitude(location.getLatitude());
        mMarriage.setLatitude(location.getLatitude());
        fMarriage.setLongitude(location.getLongitude());
        mMarriage.setLongitude(location.getLongitude());
        fMarriage.setCountry(location.getCountry());
        mMarriage.setCountry(location.getCountry());
        fMarriage.setCity(location.getCity());
        mMarriage.setCity(location.getCity());
        fMarriage.setEventType("Marriage");
        mMarriage.setEventType("Marriage");
        //we get a reasonable age to get married
        int marriageYear = getRandomNumber(yearOfMarriage, yearOfMarriage + 2);
        while ((marriageYear - birthYear) <= 13) {
            marriageYear = getRandomNumber(yearOfMarriage, yearOfMarriage + 2);
        }
        fMarriage.setYear(marriageYear);
        mMarriage.setYear(marriageYear);
        eDao.insert(fMarriage);
        eDao.insert(mMarriage);
        /*
        Father's death
        */
        Event fDeath = new Event();
        eventID = UUID.randomUUID().toString();
        fDeath.setEventID(eventID);
        fDeath.setUsername(associatedUsername);
        fDeath.setPersonID(father.getPersonID());
        location = locations.getRandomLocation();
        fDeath.setLatitude(location.getLatitude());
        fDeath.setLongitude(location.getLongitude());
        fDeath.setCountry(location.getCountry());
        fDeath.setCity(location.getCity());
        fDeath.setEventType("Death");
        int deathYear = getRandomNumber(yearOfDeath, yearOfDeath + 50);
        while((deathYear - yearOfBirth) > 100){
            deathYear = getRandomNumber(yearOfDeath, yearOfDeath + 50);
        }
        fDeath.setYear(deathYear);
        eDao.insert(fDeath);
        /*
        Mother's death
        */
        Event mDeath = new Event();
        eventID = UUID.randomUUID().toString();
        mDeath.setEventID(eventID);
        mDeath.setUsername(associatedUsername);
        mDeath.setPersonID(mother.getPersonID());
        location = locations.getRandomLocation();
        mDeath.setLatitude(location.getLatitude());
        mDeath.setLongitude(location.getLongitude());
        mDeath.setCountry(location.getCountry());
        mDeath.setCity(location.getCity());
        mDeath.setEventType("Death");
        deathYear = getRandomNumber(deathYear - 3, deathYear + 3);
        mDeath.setYear(deathYear);
        eDao.insert(mDeath);
    }

    public int getNumPeople() {
        return numPeople;
    }

    public int getNumEvents() { return numPeople * 3;}

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
        /*
        or alternatively we could use this
        public static int getRandomValue(int min, int max) {
            //get and return the random integer within Min and Max
            return ThreadLocalRandom
                    .current()
                    .nextInt(min, max + 1);
        }
        */
    }
}
