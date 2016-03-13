package be.kdg.teamh.services;

import be.kdg.teamh.entities.*;
import be.kdg.teamh.exceptions.CirkelsessieNotFound;
import be.kdg.teamh.exceptions.GebruikerNotFound;
import be.kdg.teamh.repositories.*;
import be.kdg.teamh.services.contracts.CirkelsessieService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CirkelsessieServiceImpl implements CirkelsessieService {
    @Autowired
    private CirkelsessieRepository repository;

    @Autowired
    private SpelkaartenRepository spelkaartenRepository;

    @Autowired
    private KaartenRepository kaartenRepository;

    @Autowired
    private GebruikerRepository gebruikerRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public List<Cirkelsessie> all() {
        return repository.findAll();
    }

    @Override
    public void create(int userId, Cirkelsessie cirkelsessie) {

        Gebruiker gebruiker = gebruikerRepository.findOne(userId);


        Chat chat = new Chat((cirkelsessie.getNaam()), cirkelsessie);
        Chat savedChat = chatRepository.save(chat);


        //cirkelsessie
        cirkelsessie.setGebruiker(gebruiker);
        cirkelsessie.setChat(savedChat);
        Cirkelsessie savedCirkelsessie = repository.save(cirkelsessie);

        //gebruiker
        gebruiker.addCirkelsessie(savedCirkelsessie);
        gebruikerRepository.save(gebruiker);


    }

    @Override
    public Cirkelsessie find(int id) throws CirkelsessieNotFound {
        Cirkelsessie cirkelsessie = repository.findOne(id);

        if (cirkelsessie == null) {
            throw new CirkelsessieNotFound();
        }

        return cirkelsessie;
    }

    @Override
    public void update(int id, Cirkelsessie cirkelsessie) throws CirkelsessieNotFound {
        Cirkelsessie old = find(id);

        old.setNaam(cirkelsessie.getNaam());
        old.setGebruiker(cirkelsessie.getGebruiker());
        old.setMaxAantalKaarten(cirkelsessie.getMaxAantalKaarten());
        old.setAantalCirkels(cirkelsessie.getAantalCirkels());

        repository.saveAndFlush(old);
    }


    @Override
    public void delete(int id) throws CirkelsessieNotFound {
        Cirkelsessie cirkelsessie = find(id);

        repository.delete(cirkelsessie);
    }

    public void clone(int id) throws CirkelsessieNotFound {
        Cirkelsessie old = find(id);
        Cirkelsessie clone = new Cirkelsessie(old.getNaam(), old.getMaxAantalKaarten(), old.getAantalCirkels(), true, new DateTime(), old.getSubthema(), old.getGebruiker(), old.getChat());

        clone.cloneDeelnames(old.getDeelnames());

        repository.save(clone);
    }

    @Override
    public void addSpelkaart(int id, int userId, Kaart kaart) throws CirkelsessieNotFound, GebruikerNotFound {
        Cirkelsessie cirkelsessie = repository.findOne(id);
        Gebruiker gebruiker = gebruikerRepository.findOne(userId);

        if (cirkelsessie == null) {
            throw new CirkelsessieNotFound();
        }

        if (gebruiker == null) {
            throw new GebruikerNotFound();
        }

        //kaart
        kaart.setGebruiker(gebruiker);
        Kaart savedKaart = kaartenRepository.save(kaart);

        //spelkaart
        Spelkaart spelkaart = new Spelkaart(savedKaart, cirkelsessie);
        Spelkaart savedSpelkaart = spelkaartenRepository.save(spelkaart);

        //cirkelsessie
        cirkelsessie.addSpelkaart(savedSpelkaart);
        Cirkelsessie savedCirkelsessie = repository.saveAndFlush(cirkelsessie);

        //gebruiker
        gebruiker.addKaart(savedKaart);
        gebruikerRepository.save(gebruiker);

    }

    @Override
    public List<Cirkelsessie> gepland() {
        List<Cirkelsessie> temp = all();
        List<Cirkelsessie> cirkelsessies = new ArrayList<>();
        DateTime now = new DateTime();

        for (Cirkelsessie cirkelsessie : temp) {
            if (cirkelsessie.isGesloten() && (now.compareTo(cirkelsessie.getStartDatum()) < 1)) {
                cirkelsessies.add(cirkelsessie);
            }
        }

        return cirkelsessies;
    }

    @Override
    public List<Cirkelsessie> actief() {
        List<Cirkelsessie> temp = all();
        List<Cirkelsessie> cirkelsessies = new ArrayList<>();
        DateTime now = new DateTime();

        for (Cirkelsessie cirkelsessie : temp) {
            if (!cirkelsessie.isGesloten() && (now.compareTo(cirkelsessie.getStartDatum()) > 0)) {
                cirkelsessies.add(cirkelsessie);
            }
        }

        return cirkelsessies;
    }
}
