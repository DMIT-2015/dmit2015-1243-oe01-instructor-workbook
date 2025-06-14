package dmit2015.jpa.tools;

import dmit2015.entity.GamingPlatform;
import dmit2015.entity.VideoGame;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

/**
 * This program generates data for a JPA entity for a persistence unit name
 * defined in `persistence.xml` with a RESOURCE_LOCAL transaction type.
 * <p>
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