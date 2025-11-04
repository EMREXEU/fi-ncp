package fi.csc.emrex.ncp.dto;

import fi.csc.schemas.elmo.Elmo.Learner.Identifier;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
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
  private List<Identifier> identifier;
  private String givenNames;
  private String familyName;
  private String alternateName;
  private LocalDate bday;
  private BigInteger gender;

}
