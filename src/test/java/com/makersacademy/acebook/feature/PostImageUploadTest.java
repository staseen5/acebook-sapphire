package com.makersacademy.acebook.feature;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.github.javafaker.Faker;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.makersacademy.acebook.model.Post;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostImageUploadTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private Cloudinary cloudinary;

    private Faker faker = new Faker();

    @BeforeEach
    public void setup() {
        postRepository.deleteAll();
        if (userRepository.findByUsername("user") == null) {
            userRepository.save(new User("user"));
        }
    }

//    @Test
//    @WithMockUser
//    public void successfulPostWithImage() throws Exception{
//        String fakePostContent = faker.lorem().sentence();
//        String fakeImgUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg";
//
//        Map<String, Object> mockResult = new HashMap<>();
//        mockResult.put("secure_url", fakeImgUrl);
//
//        Uploader uploader = mock(Uploader.class);
//        when(cloudinary.uploader()).thenReturn(uploader);
//        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(mockResult);
//
//        MockMultipartFile mockFile = new MockMultipartFile(
//                "file",
//                "photo.jpg",
//                "image/jpeg",
//                "fake-binary-data".getBytes()
//        );
//
//        mockMvc.perform(multipart("/").file(mockFile).param("content", fakePostContent)).andExpect(status().is3xxRedirection()).andExpect(header().string("Location", "/posts"));
//
//        boolean postExists = postRepository.existsByContentAndImageUrl(fakePostContent, fakeImgUrl);
//        assertTrue(postExists);
//    }
}
