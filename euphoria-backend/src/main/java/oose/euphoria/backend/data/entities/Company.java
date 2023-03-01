package oose.euphoria.backend.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "BEDRIJF")
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @Column(name = "BEDRIJFID")
    private String companyID;
    @Column(name = "BEDRIJFNAAM")
    private String companyName;

}