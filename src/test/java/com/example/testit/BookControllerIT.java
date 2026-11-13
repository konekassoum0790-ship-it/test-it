package com.example.testit;

import com.example.testit.adapter.member.CurrentMemberServiceFake;
import com.example.testit.model.Member;
import com.example.testit.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class BookControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CurrentMemberServiceFake currentMemberService;

    @Test
    void creerPuisEmprunterUnLivre() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // Given
        Member kassoum = memberRepository.save(new Member("kassoum"));
        currentMemberService.setCurrent(kassoum.getId());

        // When : creation du livre
        String reponse = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Solo Leveling\",\"author\":\"Chugong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Solo Leveling"))
                .andExpect(jsonPath("$.author").value("Chugong"))
                .andReturn().getResponse().getContentAsString();

        Long bookId = com.jayway.jsonpath.JsonPath.parse(reponse).read("$.id", Long.class);

        // When : 
        // emprunt du livre
        mockMvc.perform(post("/books/" + bookId + "/borrow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EMPRUNTE"));
        // Bugs : 
        // createBook passe title 2 fois et borrowBook appelle returnBook
    }
}