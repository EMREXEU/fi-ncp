package fi.csc.emrex.ncp.dto;

import fi.csc.schemas.elmo.CountryCode;
import fi.csc.schemas.elmo.Elmo.Learner.Identifier;
import fi.csc.schemas.elmo.FlexibleAddress;
import java.math.BigInteger;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LearnerDetailsDto {

  // TODO: what fields really required?

  private CountryCode citizenship;
  private List<Identifier> identifier;
  private String givenNames;
  private String familyName;
  private XMLGregorianCalendar bday;
  private String placeOfBirth;
  private String birthName;
  private FlexibleAddress currentAddress;
  private BigInteger gender;

}
