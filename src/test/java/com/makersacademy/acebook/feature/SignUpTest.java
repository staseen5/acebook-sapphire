package com.makersacademy.acebook.feature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.cloudinary.Cloudinary;
import com.github.javafaker.Faker;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.microsoft.playwright.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SignUpTest {
    Playwright playwright;
    Browser browser;
    Page page;
    Faker faker;

    @MockitoBean
    private Cloudinary cloudinary;

    @BeforeEach
    public void setup() {
        faker = new Faker();
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        page = browser.newPage();
    }

    @AfterEach
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    public void successfulSignUpAlsoLogsInUser() {
//        page.navigate("http://localhost:8081/");
//
//        // Create user variables
//        String username = faker.name().username() + "@email.com";
//        String email = username + "@email.com";
//        String firstName = faker.name().firstName();
//        String lastName = faker.name().lastName();
//
//        // Sign up with Auth0
//        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign up")).click();
//        page.locator("[name=email]").fill(email);
//        page.locator("[name=password]").fill("P@55qw0rd");
//        page.locator("[name=action]").click();
//
//        // Complete user setup
//        page.locator("[name=username]").fill(username);
//        page.locator("[name=firstName]").fill(firstName);
//        page.locator("[name=lastName]").fill(lastName);
//        page.locator("[name=updateUserBtn]").click();
//
//        // Check greeting is correct
//        Locator greeting = page.locator("#greeting");
//        assertThat(greeting).hasText("Hello, " + firstName + ' ' + lastName);
    }
}
