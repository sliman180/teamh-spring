package be.kdg.teamh;

import be.kdg.teamh.entities.*;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SpelkaartTest
{
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Gson gson;

    @Mock
    private Gebruiker gebruiker;

    @Mock
    private Cirkelsessie cirkelsessie;

    @Mock
    private Kaart kaart;

    @Mock
    private Subthema subthema;

    @Mock
    private Spelkaart spelkaart;


    @Before
    public void setUp() throws Exception
    {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        kaart = new Kaart("Een kaartje", "http://www.afbeeldingurl.be", true, gebruiker);
        cirkelsessie = new Cirkelsessie("Session one", 10, 5, subthema, gebruiker);
        spelkaart = new Spelkaart(kaart, cirkelsessie);
    }

    @Test
    public void indexSpelkaart() throws Exception
    {
        this.mvc.perform(get("/api/spelkaarten").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void createSpelkaart() throws Exception
    {
        String json = gson.toJson(spelkaart);

        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        this.mvc.perform(get("/api/spelkaarten").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].positie", is(0)));
    }

    @Test
    public void showSpelkaart() throws Exception
    {
        String json = gson.toJson(spelkaart);
        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        this.mvc.perform(get("/api/spelkaarten/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.positie", is(0)));
    }

    @Test(expected = NestedServletException.class)
    public void showSpelkaart_nonExistingKaart() throws Exception
    {
        String json = gson.toJson(spelkaart);

        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        this.mvc.perform(get("/api/spelkaarten/2").accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateSpelkaart() throws Exception
    {
        String json = gson.toJson(spelkaart);
        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        spelkaart.setPositie(5);

        json = gson.toJson(spelkaart);

        this.mvc.perform(put("/api/spelkaarten/1").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk());

        this.mvc.perform(get("/api/spelkaarten/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.positie", is(5)));
    }

    @Test(expected = NestedServletException.class)
    public void verschuifKaartMetEénStap_maxLimitReached() throws Exception
    {

        Kaart kaart = new Kaart("Een kaartje", "http://www.afbeeldingurl.be", false, gebruiker);

        Spelkaart spelkaart = new Spelkaart(kaart, cirkelsessie);
        spelkaart.setPositie(cirkelsessie.getAantalCirkels());

        String spelkaartJson = gson.toJson(spelkaart);

        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(spelkaartJson))
            .andExpect(status().isCreated());


        this.mvc.perform(patch("/api/spelkaarten/verschuif/1").contentType(MediaType.APPLICATION_JSON).content(spelkaartJson))
            .andExpect(status().isConflict());

    }

    @Test
    public void legKaartenBuitenDeCirkel() throws Exception
    {

        Kaart kaart;
        Spelkaart spelkaart;
        String spelkaartJson;

        for (int x = 0; x < 5; x++)
        {

            kaart = new Kaart("Een kaartje" + x, "http://www.afbeeldingurl.be", false, gebruiker);

            spelkaart = new Spelkaart(kaart, cirkelsessie);

            spelkaartJson = gson.toJson(spelkaart);

            this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(spelkaartJson))
                .andExpect(status().isCreated());
        }

        this.mvc.perform(get("/api/spelkaarten").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].positie", is(0)))
            .andExpect(jsonPath("$[1].positie", is(0)))
            .andExpect(jsonPath("$[2].positie", is(0)))
            .andExpect(jsonPath("$[3].positie", is(0)))
            .andExpect(jsonPath("$[4].positie", is(0)));
    }

    @Test(expected = NestedServletException.class)
    public void updateSpelkaart_nonExistingSpelkaart() throws Exception
    {
        String json = gson.toJson(spelkaart);

        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        spelkaart.setPositie(2);
        json = gson.toJson(spelkaart);

        this.mvc.perform(put("/api/spelkaarten/2").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    public void deleteSpelkaart() throws Exception
    {
        String json = gson.toJson(spelkaart);

        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        this.mvc.perform(delete("/api/spelkaarten/1"))
            .andExpect(status().isOk());

        this.mvc.perform(get("/api/spelkaarten").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test(expected = NestedServletException.class)
    public void deleteSpelkaart_nonExistingSpelkaart() throws Exception
    {
        String json = gson.toJson(spelkaart);

        this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        this.mvc.perform(delete("/api/spelkaarten/2"));
    }

    @Test
    public void kiesKaartenUitEenVerzameling() throws Exception
    {


        Kaart kaart;
        Spelkaart spelkaart;
        String spelkaartJson;

        for (int x = 0; x < 5; x++)
        {

            kaart = new Kaart("Een kaartje" + x, "http://www.afbeeldingurl.be", false, gebruiker);

            spelkaart = new Spelkaart(kaart, cirkelsessie);

            spelkaartJson = gson.toJson(spelkaart);

            this.mvc.perform(post("/api/spelkaarten").contentType(MediaType.APPLICATION_JSON).content(spelkaartJson))
                .andExpect(status().isCreated());
        }


        this.mvc.perform(get("/api/spelkaarten").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)))
            .andExpect(jsonPath("$[0].cirkelsessie.id", is(1)))
            .andExpect(jsonPath("$[1].cirkelsessie.id", is(2)))
            .andExpect(jsonPath("$[2].cirkelsessie.id", is(3)))
            .andExpect(jsonPath("$[3].cirkelsessie.id", is(4)))
            .andExpect(jsonPath("$[4].cirkelsessie.id", is(5)));

    }

}