package com.makersacademy.acebook.feature;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.github.javafaker.Faker;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SearchPostsTest {
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

        String username = faker.name().username();
        String email = username + "@email.com";

        page.navigate("http://localhost:8081/");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign up")).click();
        page.locator("[name=email]").fill(email);
        page.locator("[name=password]").fill("P@55qw0rd");
        page.locator("[name=action]").click();


        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        page.locator("[name=firstName]").fill(firstName);
        page.locator("[name=lastName]").fill(lastName);
        page.locator("[name=username]").fill(username);
        page.locator("[name=updateUserBtn]").click();
    }

    @AfterEach
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    public void searchPostsWithKeywordBobReturnsTwoPosts() {
        // Enter keyword into searchbar
        page.locator("[name=keyword]").fill("Bob");
        page.keyboard().press("Enter");

        // Check posts content is correct
        Locator postsContent = page.locator(".post-content-text");
        assertThat(postsContent.nth(0)).containsText("Hey everyone, Bob here!");
        assertThat(postsContent.nth(1)).containsText("Bob posting again. Loving this platform so far.");
    }
}
