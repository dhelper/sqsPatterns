package messages;

import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MyOtherMessage {
    private String data;
    private int number;
}
