package hu.bme.hit.crysis.sludgeeldiablo.caffbrowser;

import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.PasswordDto;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.util.InitUtil;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.util.TestConfig;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.UserDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest extends UserSessionTest {

    private static final String BASE_URL = "/api/user";
    private static final String PASSWORD_URL = BASE_URL + "/password";

    private final MockMvc mockMvc;
    private final JacksonTester<UserDto> userJson;
    private final JacksonTester<PasswordDto> passwordJson;

    @Test
    @DisplayName("Saj??t felhaszn??l??i adatok sikeres lek??r??se")
    void testGetMyUser() throws Exception {
        // when, then
        this.mockMvc.perform(get(BASE_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is("sludge")))
                .andExpect(jsonPath("$.name", is("Sludge El Diablo")))
                .andExpect(jsonPath("$.email", is("carnage@feat.sludge")));
    }

    @Test
    @DisplayName("Saj??t felhaszn??l??i adatok sikeres m??dos??t??sa")
    void testUpdateMyUser() throws Exception {
        // given
        UserDto dto = InitUtil.createValidUpdateUserDto();

        // when, then
        this.mockMvc.perform(put(BASE_URL)
                .contentType((MediaType.APPLICATION_JSON))
                .content(userJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("sludge")))
                .andExpect(jsonPath("$.name", is("Updated Sludge")))
                .andExpect(jsonPath("$.email", is("updated@carnage.sludge")));
    }

    @Test
    @DisplayName("Saj??t felhaszn??l??n??v m??dos??t??sa nem lehets??ges")
    void testUpdateMyUserUsernameNotChanged() throws Exception {
        // given
        UserDto dto = InitUtil.createValidUpdateUserDtoWithUsernameUpdate();

        // when, then
        this.mockMvc.perform(put(BASE_URL)
                .contentType((MediaType.APPLICATION_JSON))
                .content(userJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("sludge")))
                .andExpect(jsonPath("$.name", is("Sludge El Diablo")))
                .andExpect(jsonPath("$.email", is("carnage@feat.sludge")));
    }

    @Test
    @DisplayName("Opcion??lis felhaszn??l??i adatok t??rl??se")
    void testUpdateMyUserClearData() throws Exception {
        // given
        UserDto dto = InitUtil.createClearUpdateUserDto();

        // when, then
        this.mockMvc.perform(put(BASE_URL)
                .contentType((MediaType.APPLICATION_JSON))
                .content(userJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("sludge")))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    @DisplayName("Saj??t felhaszn??l??i fi??k t??rl??se")
    void testDeleteMyUser() throws Exception {
        // when, then
        this.mockMvc.perform(delete(BASE_URL)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Saj??t felhaszn??l?? jelszav??nak sikeres m??dos??t??sa")
    void testChangePassword() throws Exception {
        // given
        PasswordDto dto = InitUtil.createValidPasswordDto();

        // when, then
        this.mockMvc.perform(put(PASSWORD_URL)
                .contentType((MediaType.APPLICATION_JSON))
                .content(passwordJson.write(dto).getJson())
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
