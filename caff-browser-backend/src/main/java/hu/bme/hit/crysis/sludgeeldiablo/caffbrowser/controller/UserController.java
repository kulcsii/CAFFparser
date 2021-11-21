package hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.controller;

import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.dto.UserDto;
import hu.bme.hit.crysis.sludgeeldiablo.caffbrowser.service.declaration.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getMyUser() {
        log.trace("UserController : getMyUser");
        return ResponseEntity.ok(userService.getMe());
    }

    @PutMapping
    public ResponseEntity<UserDto> updateMyUser(@RequestBody UserDto userDto) {
        log.trace("UserController : updateMyUser, userDto=[{}]", userDto);
        return ResponseEntity.ok(userService.updateMe(userDto));
    }

    // TODO: changePassword

    // TODO: deleteMyUser
}
