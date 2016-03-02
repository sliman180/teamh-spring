package be.kdg.teamh.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Bericht {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String tekst;

    @NotNull
    private LocalDateTime datum;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Chat chat;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Gebruiker gebruiker;


    public Bericht() {
        //JPA
    }

    public Bericht(String tekst, LocalDateTime datum, Gebruiker gebruiker) {
        this.tekst = tekst;
        this.datum = datum;
        this.gebruiker = gebruiker;
    }


    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Gebruiker getGebruiker() {
        return gebruiker;
    }

    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }
}