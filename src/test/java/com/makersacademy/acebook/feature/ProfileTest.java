package com.makersacademy.acebook.feature;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.github.javafaker.Faker;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")

public class ProfileTest {
        Playwright playwright;
        Browser browser;
        Page page;
        Faker faker;

        @Autowired
        UserRepository userRepository;

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
    public void profilePageLoads() {
        String email = faker.name().username() + "@email.com";
        User user = new User(email);
        userRepository.save(user);

        page.navigate("http://localhost:8081/profile/" + email);
        Locator pageBody = page.locator("body");
        assertThat(pageBody).containsText(email);
    }

    @Test
    public void profilePageDisplaysUsername() {
        String email = faker.name().username() + "@email.com";
        User user = new User(email);
        userRepository.save(user);

        page.navigate("http://localhost:8081/profile/" + email);
        Locator pageBody = page.locator("body");
        assertThat(pageBody).containsText(email);
    }

}
