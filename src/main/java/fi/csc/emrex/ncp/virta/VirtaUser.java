package fi.csc.emrex.ncp.virta;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by marko.hollanti on 07/10/15.
 */
@Getter
@Setter
public class VirtaUser {

    enum Gender {

        MALE("Mies"),
        FEMALE("Nainen");

        private String value;

        public String getValue() {
            return value;
        }

        Gender(String value) {
            this.value = value;
        }
    }

    public VirtaUser(String firstName, String lastName, Gender gender, LocalDate birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
    }

    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;

}
