package oose.euphoria.backend.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "BERICHT")
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @Column(name = "BERICHTID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "VERZENDERGEBRUIKERID")
    private String senderID;
    @Column(name = "ONTVANGERGEBRUIKERID")
    private String receiverID;
    @Column(name = "BERICHTINHOUD")
    private String content;
    @Column(name = "BERICHTVERSTUURDDATUM")
    private String sendDate;
    @Column(name = "BERICHTGELEZENDATUM")
    private String readDate;
    @Column(name = "BERICHTBIJLAGEID")
    private String attachmentID;
    @Column(name = "BERICHTISOMROEP")
    private boolean isBroadcast;
    @Transient
    private String attachmentName;

    public Message(String senderID) {
        this.senderID = senderID;
    }
}
