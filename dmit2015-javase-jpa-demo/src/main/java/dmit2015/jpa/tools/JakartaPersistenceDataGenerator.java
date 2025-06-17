package dmit2015.jpa.tools;

import dmit2015.entity.GamingPlatform;
import dmit2015.entity.VideoGame;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

/**
 * To generate and persist fake data to your database using this JakartaPersistenceDatabaseGenerator program:
 * 1.   Configure persistence.xml: Create a persistence.xml file in your project's src/main/resources/META-INF directory.
 *      This file must define a persistence unit that specifies the connection details for your database
 *      (e.g., JDBC URL, username, password, driver class).
 * 2.   Create JPA Entity Classes: Ensure you have created your JPA entity classes with the appropriate annotations.
 * 3.   Implement Data Generation Logic: Open the JakartaPersistenceDatabaseGenerator.java class
 *      and complete the generateData() method.
 *      Within this method, you are responsible for:
 *      - Determining the number of records to generate.
 *      - Creating the fake data for each record.
 *      - Creating new instances of your JPA entity classes.
 *      - Setting the properties of the entity instances using the generated fake data.
 *      - Persisting the entity instances to the database by calling the persist() method of the EntityManager.
 * 4.   Specify Persistence Unit Name (Optional): If you want to use a persistence unit other than the default,
 *      provide the persistence unit name as a command-line argument when running the program.
 * 5.   Run the program: java JakartaPersistenceDatabaseGenerator [persistenceUnitName]
 *
 * This program requires a RESOURCE_LOCAL transaction type in `persistence.xml`.
 * The following is an example of a RESOURCE_LOCAL persistence unit:
 *
 * <pre>{@code
 * <persistence-unit name="resource-local-postgresql-jpa-pu" transaction-type="RESOURCE_LOCAL">
 *     <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
 *
 *     <properties>
 *         <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver" />
 *         <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost/DMIT2015CourseDB" />
 *         <property name="jakarta.persistence.jdbc.user" value="user2015" />
 *         <property name="jakarta.persistence.jdbc.password" value="Password2015" />
 *     </properties>
 * </persistence-unit>*
 * }
 * </pre>
 * <p>
 * To modify the program to run with arguments from IntelliJ IDEA:
 * 1) Click on the green run icon in the gutter area to the left of the main method.
 * 2) Click "Modify Run Configuration..."
 * 3) In the "Program arguments" field enter the name of persistence unit to access such as resource-local-postgresql-jpa-pu
 * 4) Click OK
 * <p>
 * To run the program from IntelliJ IDEA:
 * 1) Click on the green run icon in the gutter area to the left of the main method.
 * 2) Click "Run 'JakartaPersist....main()`"
 * <p>
 * If the following error is reported: Exception in thread "main" java.lang.NoClassDefFoundError: org/jboss/logging/Logger
 * then add the following dependencies to pom.xml
 * <pre>{@code
 *         <dependency>
 *             <groupId>org.jboss.logging</groupId>
 *             <artifactId>jboss-logging</artifactId>
 *             <version>3.6.1.Final</version>
 *         </dependency>
 * }
 * </pre>
 *
 * @author Sam Wu
 */
public class JakartaPersistenceDataGenerator {

    public static void main(String[] args) {
        try {
            Class.forName("org.hibernate.jpa.HibernatePersistenceProvider");
            // The persistenceUnitName can be passed as an argument otherwise it defaults to "resource-local-postgresql-jpa-pu"
            String persistenceUnitName = (args.length == 1) ? args[0] : "resource-local-postgresql-jpa-pu";
            // https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1.html#obtaining-an-entity-manager-factory-in-a-java-se-environment
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            JakartaPersistenceDataGenerator.generateData(em);
            em.getTransaction().commit();
            em.close();
            emf.close();
            System.out.printf("Successfully generated data for entity classes in persistence unit %s.", persistenceUnitName);
        } catch (Exception e) {
            System.err.println("Error generating data with exception: " + e.getMessage());
        }
    }

    public static void generateData(EntityManager em) {
         /* TODO: Write your own code to implement data generation logic.
        You are responsible for:
            - Determining the number of records to generate.
            - Creating the fake data for each record.
            - Creating new instances of your JPA entity classes.
            - Setting the properties of the entity instances using the generated fake data.
            - Persisting the entity instances to the database by calling the persist() method of the EntityManager.
        */
        var faker = new Faker();
        for (int i = 0; i < 10; i++) {
            VideoGame videoGame = new VideoGame();
            videoGame.setTitle(faker.videoGame().title());
            videoGame.setPlatform(GamingPlatform.NINTENDO);
            videoGame.setPrice(faker.number().numberBetween(1, 100));

            em.persist(videoGame);
        }

    }
}