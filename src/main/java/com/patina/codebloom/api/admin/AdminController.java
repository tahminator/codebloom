package com.patina.codebloom.api.admin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@Tag(name = "Admin routes", description = "This controller is responsible for handling all admin routes.")
@RequestMapping("/api/admin")
public class AdminController {

}