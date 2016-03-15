package be.kdg.teamh;

import be.kdg.teamh.dtos.Token;
import be.kdg.teamh.entities.Gebruiker;
import be.kdg.teamh.entities.Rol;
import be.kdg.teamh.entities.Subthema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubthemaTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void indexSubthema() throws Exception {
        this.mvc.perform(get("/api/subthemas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void createSubthema() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        this.mvc.perform(get("/api/subthemas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].naam", is("Houffalize")))
                .andExpect(jsonPath("$[0].beschrijving", is("Route 6")));
    }

    @Test(expected = NestedServletException.class)
    public void createSubthema_nullInput() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema(null, null, null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()));
    }

    @Test
    public void showSubthema() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        this.mvc.perform(get("/api/subthemas/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam", is("Houffalize")))
                .andExpect(jsonPath("$.beschrijving", is("Route 6")));
    }

    @Test(expected = NestedServletException.class)
    public void showSubthema_nonExistingSubthema() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        this.mvc.perform(get("/api/subthemas/2").accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateSubthema() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 3", null));

        this.mvc.perform(put("/api/subthemas/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        this.mvc.perform(get("/api/subthemas/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam", is("Houffalize")))
                .andExpect(jsonPath("$.beschrijving", is("Route 3")));
    }

    @Test(expected = NestedServletException.class)
    public void updateHoofdthema_nullInput() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        json = objectMapper.writeValueAsString(new Subthema(null, null, null));

        this.mvc.perform(put("/api/subthemas/1").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test(expected = NestedServletException.class)
    public void updateSubthema_nonExistingSubthema() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 3", null));

        this.mvc.perform(put("/api/subthemas/2").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    public void deleteSubthema() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        this.mvc.perform(delete("/api/subthemas/1"))
                .andExpect(status().isOk());

        this.mvc.perform(get("/api/subthemas").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test(expected = NestedServletException.class)
    public void deleteSubthema_nonExistingSubthema() throws Exception {
        String json = objectMapper.writeValueAsString(new Subthema("Houffalize", "Route 6", null));

        this.mvc.perform(post("/api/subthemas").contentType(MediaType.APPLICATION_JSON).content(json).header("Authorization", getUserToken()))
                .andExpect(status().isCreated());

        this.mvc.perform(delete("/api/subthemas/2"));
    }

    private String getUserToken() throws Exception {
        String json = objectMapper.writeValueAsString(new Gebruiker("user", "user", new ArrayList<>(Collections.singletonList(new Rol("user", "user")))));
        MvcResult mvcResult = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();

        return "Bearer " + objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Token.class).getToken();
    }

}