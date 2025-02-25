package com.patina.codebloom.api.testjda;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.dto.ApiResponder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

@RestController
@RequestMapping("/api/jda")
public class TestJdaController {
    private final JDA jda;

    public TestJdaController(final JDA jda) {
        this.jda = jda;
    }

    @GetMapping("/guilds")
    public ResponseEntity<ApiResponder<?>> getGuilds() {
        List<Guild> guilds = jda.getGuilds();
        return ResponseEntity.ok().body(ApiResponder.success("Guilds retreived", guilds));
    }
}
