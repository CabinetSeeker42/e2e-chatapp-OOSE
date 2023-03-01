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
@Table(name = "BEDRIJF_SUPPORT")
@AllArgsConstructor
@NoArgsConstructor
public class CompanySupport {

    @Id
    @Column(name = "GEBRUIKERID")
    private String userID;
    @Id
    @Column(name = "BEDRIJFID")
    private String companyID;

}
