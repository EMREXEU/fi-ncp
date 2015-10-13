package fi.csc.emrex.ncp.virta;

/**
 * Created by marko.hollanti on 13/10/15.
 */
public enum Gender {
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
