package hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.service.declaration;

import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.CommentDto;

public interface CommentService {

    /**
     * Hozzászólás írása egy képhez
     *
     * @param commentDto a hozzászólás adatai
     * @return a létrejött hozzászólás
     */
    CommentDto save(CommentDto commentDto);

    /**
     * Hozzászólás módosítása id alapján
     *
     * @param id hozzászólás azonosítója
     * @param commentDto módosított adatok
     * @return módosított hozzászólás adatai
     */
    CommentDto update(Long id, CommentDto commentDto);

    /**
     * Hozzászólás törlése id alapján
     *
     * @param id hozzászólás azonosítója
     */
    void delete(Long id);

    /**
     * Visszaadja, hogy az aktuálisan bejelentkezett felhasználó módosíthat-e egy hozzászóláson
     *
     * @param id hozzászólás azonosítója
     * @return logikai érték
     */
    Boolean canCurrentUserModifyComment(Long id);
}
