package glym.glym_spring.domain.font.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AIRequestAckDto {
    private String jobId;
    private String status; // ACCEPTED
    private String message; // "Request received"
}
