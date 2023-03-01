package oose.euphoria.backend.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "BIJLAGE")
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    @Id
    @Column(name = "BIJLAGEID")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    @Column(name = "BIJLAGETYPE")
    private String type;
    @Column(name = "BIJLAGENAAM")
    private String name;
    @Column(name = "BIJLAGECONTENT")
    private String content;
}
