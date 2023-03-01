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
@Table(name = "GEBRUIKER")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "GEBRUIKERID")
    private String id;
    @Column(name = "GEBRUIKERBEDRIJFID")
    private String companyID;
    @Column(name = "GEBRUIKERPUBLICKEY")
    private String publicKey;
    @Column(name = "GEBRUIKERNAAM")
    private String username;
    @Column(name = "GEBRUIKERISSUPPORT")
    private boolean isSupport;
}