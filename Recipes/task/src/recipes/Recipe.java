package recipes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Recipe {
    @Column
    String name;

    @Column
    String description;

    @ElementCollection
    @Column
    List<String> ingredients;

    @ElementCollection
    @Column
    List<String> directions;

    @Column
    @Id
    int id;
}
