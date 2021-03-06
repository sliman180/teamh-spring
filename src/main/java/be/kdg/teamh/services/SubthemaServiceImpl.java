package be.kdg.teamh.services;

import be.kdg.teamh.dtos.request.KaartRequest;
import be.kdg.teamh.dtos.request.SubthemaRequest;
import be.kdg.teamh.entities.*;
import be.kdg.teamh.exceptions.gebruiker.GebruikerNietGevonden;
import be.kdg.teamh.exceptions.hoofdthema.HoofdthemaNietGevonden;
import be.kdg.teamh.exceptions.subthema.SubthemaNietGevonden;
import be.kdg.teamh.repositories.GebruikerRepository;
import be.kdg.teamh.repositories.HoofdthemaRepository;
import be.kdg.teamh.repositories.KaartRepository;
import be.kdg.teamh.repositories.SubthemaRepository;
import be.kdg.teamh.services.contracts.SubthemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubthemaServiceImpl implements SubthemaService
{
    private SubthemaRepository repository;
    private HoofdthemaRepository hoofdthemas;
    private GebruikerRepository gebruikers;
    private KaartRepository kaarten;

    @Autowired
    public SubthemaServiceImpl(SubthemaRepository repository, HoofdthemaRepository hoofdthemas, GebruikerRepository gebruikers, KaartRepository kaarten)
    {
        this.repository = repository;
        this.hoofdthemas = hoofdthemas;
        this.gebruikers = gebruikers;
        this.kaarten = kaarten;
    }

    @Override
    public List<Subthema> all()
    {
        return repository.findAll();
    }

    @Override
    public void create(SubthemaRequest dto) throws HoofdthemaNietGevonden, GebruikerNietGevonden
    {
        Hoofdthema hoofdthema = hoofdthemas.findOne(dto.getHoofdthema());

        if (hoofdthema == null)
        {
            throw new HoofdthemaNietGevonden();
        }

        Gebruiker gebruiker = gebruikers.findOne(dto.getGebruiker());

        if (gebruiker == null)
        {
            throw new GebruikerNietGevonden();
        }

        Subthema subthema = new Subthema();
        subthema.setNaam(dto.getNaam());
        subthema.setBeschrijving(dto.getBeschrijving());
        subthema.setHoofdthema(hoofdthema);
        subthema.setGebruiker(gebruiker);
        subthema = repository.saveAndFlush(subthema);

        hoofdthema.addSubthema(subthema);
        hoofdthemas.saveAndFlush(hoofdthema);

        gebruiker.addSubthema(subthema);
        gebruikers.saveAndFlush(gebruiker);
    }

    @Override
    public Subthema find(int id) throws SubthemaNietGevonden
    {
        Subthema subthema = repository.findOne(id);

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        return subthema;
    }

    @Override
    public void update(int id, SubthemaRequest dto) throws SubthemaNietGevonden, HoofdthemaNietGevonden, GebruikerNietGevonden
    {
        Gebruiker gebruiker = gebruikers.findOne(dto.getGebruiker());

        if (gebruiker == null)
        {
            throw new GebruikerNietGevonden();
        }

        Hoofdthema hoofdthema = hoofdthemas.findOne(dto.getHoofdthema());

        if (hoofdthema == null)
        {
            throw new HoofdthemaNietGevonden();
        }

        Subthema subthema = repository.findOne(id);

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        subthema.setNaam(dto.getNaam());
        subthema.setBeschrijving(dto.getBeschrijving());
        subthema.setHoofdthema(hoofdthema);
        subthema.setGebruiker(gebruiker);

        repository.saveAndFlush(subthema);
    }

    @Override
    public void delete(int id) throws SubthemaNietGevonden
    {
        Subthema subthema = repository.findOne(id);

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        repository.delete(subthema);
    }

    @Override
    public Organisatie findOrganisatie(int id) throws SubthemaNietGevonden
    {
        Subthema subthema = repository.findOne(id);

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        return subthema.getHoofdthema().getOrganisatie();
    }

    @Override
    public Hoofdthema findHoofdthema(int id) throws SubthemaNietGevonden
    {
        Subthema subthema = repository.findOne(id);

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        return subthema.getHoofdthema();
    }

    @Override
    public List<Cirkelsessie> getCirkelsessies(int id) throws SubthemaNietGevonden
    {
        Subthema subthema = repository.findOne(id);

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        return subthema.getCirkelsessies();
    }

    @Override
    public List<Kaart> getKaarten(int id) throws SubthemaNietGevonden
    {
        Subthema subthema = repository.findOne(id);

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        return subthema.getKaarten();
    }

    @Override
    public void addKaart(int subthemaId, KaartRequest kaart) throws SubthemaNietGevonden, GebruikerNietGevonden
    {
        Subthema subthema = repository.findOne(subthemaId);
        Gebruiker gebruiker = gebruikers.findOne(kaart.getGebruiker());

        if (subthema == null)
        {
            throw new SubthemaNietGevonden();
        }

        if (gebruiker == null)
        {
            throw new GebruikerNietGevonden();
        }

        Kaart kaartje = new Kaart();
        kaartje.setImageUrl(kaart.getImageUrl());
        kaartje.setTekst(kaart.getTekst());
        kaartje.setGebruiker(gebruiker);
        kaartje.setSubthema(subthema);
        kaartje.setCommentsToelaatbaar(kaart.isCommentsToelaatbaar());

        Kaart savedKaart = kaarten.save(kaartje);

        subthema.addKaart(savedKaart);
        repository.saveAndFlush(subthema);

        gebruiker.addKaart(savedKaart);
        gebruikers.saveAndFlush(gebruiker);
    }
}
