package hu.bme.hit.crysis.sludgeeldiablo.caffbrowser;

import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.CommentDto;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.util.InitUtil;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.util.TestConfig;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.util.UserSessionTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerIntegrationTest extends UserSessionTest {

    private static final String BASE_URL = "/api/comment";
    private static final String BASE_PARAMETER_URL = BASE_URL + "/{commentId}";

    private final MockMvc mockMvc;
    private final JacksonTester<CommentDto> commentJson;

    @Test
    @DisplayName("Hozz??sz??l??s k??phez, k??p azonos??t??ja alapj??n")
    void testSaveComment() throws Exception {
        // given
        CommentDto dto = InitUtil.createValidCommentDto();

        // when, then
        this.mockMvc.perform(post(BASE_URL)
                .contentType((MediaType.APPLICATION_JSON))
                .content(commentJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.imageId", is(1)))
                .andExpect(jsonPath("$.content", is("Hozz??sz??lok ehhez a k??phez")));
    }

    @Test
    @DisplayName("Sikertelen hozz??sz??l??si k??s??rlet nem l??tez?? k??phez")
    void testSaveCommentNotFoundImage() throws Exception {
        // given
        CommentDto dto = InitUtil.createInvalidCommentDto();

        // when, then
        this.mockMvc.perform(post(BASE_URL)
                .contentType((MediaType.APPLICATION_JSON))
                .content(commentJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Hozz??sz??l??s m??dos??t??sa azonos??t?? alapj??n")
    void testUpdateComment() throws Exception {
        // given
        CommentDto dto = InitUtil.createValidCommentDto();

        // when, then
        this.mockMvc.perform(put(BASE_PARAMETER_URL, 1L)
                .contentType((MediaType.APPLICATION_JSON))
                .content(commentJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Hozz??sz??lok ehhez a k??phez")));
    }

    @Test
    @DisplayName("Nem l??tez?? azonos??t??j?? hozz??sz??l??s sikertelen m??dos??t??sa")
    void testUpdateCommentNotFoundComment() throws Exception {
        // given
        CommentDto dto = InitUtil.createValidCommentDto();

        // when, then
        this.mockMvc.perform(put(BASE_PARAMETER_URL, 999L)
                .contentType((MediaType.APPLICATION_JSON))
                .content(commentJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Hozz??sz??l??s t??rl??se azonos??t?? alapj??n")
    void testDeleteComment() throws Exception {
        // when, then
        this.mockMvc.perform(delete(BASE_PARAMETER_URL, 1L)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Nem l??tez?? azonos??t??j?? hozz??sz??l??s sikertelen t??rl??se")
    void testDeleteCommentNotFoundComment() throws Exception {
        // when, then
        this.mockMvc.perform(delete(BASE_PARAMETER_URL, 999L)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
