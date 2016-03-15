package be.kdg.teamh.controllers;

import be.kdg.teamh.entities.Commentaar;
import be.kdg.teamh.entities.Kaart;
import be.kdg.teamh.entities.Spelkaart;
import be.kdg.teamh.entities.Subthema;
import be.kdg.teamh.exceptions.CommentsNotAllowed;
import be.kdg.teamh.exceptions.notfound.GebruikerNotFound;
import be.kdg.teamh.exceptions.notfound.KaartNotFound;
import be.kdg.teamh.services.contracts.KaartenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kaarten")
public class KaartenController {
    @Autowired
    private KaartenService service;

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Kaart> index() {
        return service.all();
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void create(@RequestBody Kaart kaart, @RequestHeader(name = "Authorization") String token) throws GebruikerNotFound {

        service.create(getUserId(token), kaart);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Kaart show(@PathVariable("id") int id) throws KaartNotFound {
        return service.find(id);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public void update(@PathVariable("id") int id, @RequestBody Kaart kaart) throws KaartNotFound {
        service.update(id, kaart);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") int id) throws KaartNotFound {
        service.delete(id);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "{id}/subthema", method = RequestMethod.GET)
    public Subthema getSubthema(@PathVariable("id") int id) throws KaartNotFound {
        return service.getSubthema(id);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(value = "{id}/subthemas", method = RequestMethod.POST)
    public void addSubthemaToKaart(@PathVariable("id") int id, @RequestBody Subthema subthema) throws KaartNotFound {
        service.addSubthema(id, subthema);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "{id}/comments", method = RequestMethod.GET)
    public List<Commentaar> comments(@PathVariable("id") int id) throws KaartNotFound {
        return service.allComments(id);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(value = "{id}/comments", method = RequestMethod.POST)
    public void createComment(@PathVariable("id") int id,@RequestHeader(name = "Authorization") String token, @RequestBody Commentaar comment) throws CommentsNotAllowed, KaartNotFound, GebruikerNotFound {
        service.createComment(id,getUserId(token),comment);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "{id}/spelkaarten", method = RequestMethod.GET)
    public List<Spelkaart> getSpelkaarten(@PathVariable("id") int id) throws KaartNotFound {
        return service.getSpelkaarten(id);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(value = "{id}/spelkaarten", method = RequestMethod.POST)
    public void addSpelkaartAanKaart(@PathVariable("id") int id, @RequestBody Spelkaart spelkaart) throws KaartNotFound {
        service.addSpelkaart(id, spelkaart);
    }

    private int getUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey("kandoe")
                .parseClaimsJws(token.substring(7)).getBody();
        return Integer.parseInt(claims.getSubject());
    }
}
