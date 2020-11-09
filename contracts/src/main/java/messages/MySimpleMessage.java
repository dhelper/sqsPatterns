package messages;

import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MySimpleMessage {
    private String title;
    private String content;
}
