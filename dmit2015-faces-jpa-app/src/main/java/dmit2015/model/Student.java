package dmit2015.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;

/**
 * This Jakarta Persistence class is mapped to a relational database table with the same name.
 * If Java class name does not match database table name, you can use @Table annotation to specify the table name.
 * <p>
 * Each field in this class is mapped to a column with the same name in the mapped database table.
 * If the field name does not match database table column name, you can use the @Column annotation to specify the database table column name.
 * The @Transient annotation can be used on field that is not mapped to a database table column.
 */
@Entity
//@Table(schema = "CustomSchemaName", name="CustomTableName")
@Getter
@Setter
public class Student implements Serializable {

    private static final Logger _logger = Logger.getLogger(Student.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "ColumnName", nullable = false)
    private Long id;

    @NotBlank(message = "First name is required.")
    private String firstName;
    @NotBlank(message = "Last name is required.")
    private String lastName;
    private String courseSection;

    public Student() {

    }

    @Version
    private Integer version;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    private void beforePersist() {
        createTime = LocalDateTime.now();
    }

    @PreUpdate
    private void beforeUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        return (
                (obj instanceof Student other) &&
                        Objects.equals(id, other.id)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Factory method to create a new Student instance
    public static Student of(Faker faker) {
        Student currentStudent = new Student();

        currentStudent.setFirstName(faker.name().firstName());
        currentStudent.setLastName(faker.name().lastName());
        currentStudent.setCourseSection("DMIT2015-1243-OE01");
        return currentStudent;
    }

    public static Optional<Student> parseCsv(String line) {
        final var DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        String[] tokens = line.split(DELIMITER, -1);  // The -1 limit allows for any number of fields and not discard trailing empty fields
        /*
         * The order of the columns are:
         * 0 - column1
         * 1 - column2
         * 2 - column3
         * 3 - column4
         */
        if (tokens.length == 5) {
            Student parsedStudent = new Student();

            try {
                // String stringColumnValue = tokens[0].replaceAll("\"","");
                // boolean booleanColumnValue = Boolean.parse(tokens[0]);
                // LocalDate dateColumnValue = tokens[0].isBlank() ? null : LocalDate.parse(tokens[0]);
                // BigDecimal decimalColumnValue = tokens[0].isBlank() ? null : BigDecimal.valueOf(Double.parseDouble(tokens[0]));
                // Integer IntegerColumnValue = tokens[0].isBlank() ? null : Integer.valueOf(tokens[0]);
                // Double DoubleColumnValue = tokens[0].isBlank() ? null : Double.valueOf(tokens[0]);
                // int intColumnValue = tokens[0].isBlank() ? 0 : Integer.parseInt(tokens[0]);
                // double doubleColumnValue = tokens[0].isBlank() ? 0 : Double.parseDouble(tokens[0]);

                // parsedStudent.setProperty1(column1Value);

                return Optional.of(parsedStudent);
            } catch (Exception ex) {
                _logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return Optional.empty();
    }

}