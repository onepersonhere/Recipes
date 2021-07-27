package recipes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table
public class Recipe {
    @Column
    String name;
    @ToString.Exclude
    @Column
    String category;
    @ToString.Exclude
    @Column
    String date;
    @ToString.Exclude
    @Column
    String description;
    @ToString.Exclude
    @ElementCollection
    @Column
    List<String> ingredients;
    @ToString.Exclude
    @ElementCollection
    @Column
    List<String> directions;

    @Column
    @Id
    int id;

    @Column
    String author;
}
