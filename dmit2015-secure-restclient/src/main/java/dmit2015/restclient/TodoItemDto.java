package dmit2015.restclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemDto {

    private Long id;

    private String name;

    private boolean complete;

    private Integer version;

}