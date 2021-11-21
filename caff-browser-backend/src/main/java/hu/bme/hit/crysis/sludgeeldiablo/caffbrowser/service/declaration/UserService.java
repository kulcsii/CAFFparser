package hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.service.declaration;

import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.UserDto;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.model.User;

import java.util.List;

public interface UserService {

    /**
     * Új felhasználó mentése
     *
     * @param userDto az új felhasználó adatai
     * @return a mentett felhasználó
     */
    UserDto save(UserDto userDto);

    /**
     * Felhasználó lekérése id alapján
     *
     * @param id felhasználó azonosítója
     * @return a talált felhasználó adatai
     */
    UserDto get(Long id);

    /**
     * Az összes felhasználó lekérése
     *
     * @return az összes felhasználó listája
     */
    List<UserDto> getAll();

    /**
     * Felhasználó entitás keresése név alapján
     *
     * @param username felhasználónév
     * @return a talált felhasználó entitás
     */
    User findByUsername(String username);

    /**
     * Aktuálisan bejelentkezett felhasználó adatainak lekérése
     *
     * @return talált felhasználó adatai
     */
    UserDto getMe();

    /**
     * Aktuálisan bejelentkezett felhasználó adatainak módosítása
     *
     * @param userDto új adatok
     * @return módosított felhasználó adatai
     */
    UserDto updateMe(UserDto userDto);

    /**
     * Felhasználó adatainak módosítása id alapján
     *
     * @param id felhasználó azonosítója
     * @param userDto módosított adatok
     * @return a módosított felhasználó
     */
    UserDto update(Long id, UserDto userDto);

    /**
     * Felhasználó törlése id alapján
     *
     * @param id felhasználó azonosítója
     */
    void delete(Long id);
}
