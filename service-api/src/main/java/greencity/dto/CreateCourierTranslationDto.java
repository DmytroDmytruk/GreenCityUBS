package greencity.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateCourierTranslationDto {
    @NotNull
    private String name;
    @NotNull
    private Long languageId;
    @NotNull
    private String limitDescription;
}