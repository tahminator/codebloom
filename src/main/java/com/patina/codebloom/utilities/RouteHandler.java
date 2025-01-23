package com.patina.codebloom.utilities;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * This is used to handle the routing for React.
 * 
 * Some examples for whether it gets captured or not here include:
 * /home -> Yes -> Loads index.html
 * /about -> Yes -> Loads index.html
 * /index.html -> No -> This loads index.html, but that's because it's a static file.
 * /script.js -> No -> Static file, loads it.
 * /images/logo.png -> static file, loads it.
 */
@Controller
public class RouteHandler {

    @GetMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}