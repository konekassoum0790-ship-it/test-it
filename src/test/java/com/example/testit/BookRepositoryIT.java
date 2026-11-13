package com.example.testit;

import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookRepositoryIT {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByMemberAndStatus_avecDeuxMembres() {

        //given
        Member kassoum = memberRepository.save(new Member("kassoum"));

        Member serigne = memberRepository.save(new Member("serigne"));

        Book livre1 = new Book("Solo Leveling", "Chugong");
        livre1.setStatus(Status.EMPRUNTE);
        livre1.setBorrower(kassoum);
        bookRepository.save(livre1);

        Book livre2 = new Book("One Piece", "Oda");
        livre2.setStatus(Status.RENDU);
        livre2.setBorrower(serigne);
        bookRepository.save(livre2);

        Book livre3 = new Book("Naruto", "Kishimoto");
        livre3.setStatus(Status.EMPRUNTE);
        livre3.setBorrower(serigne);
        bookRepository.save(livre3);

        //when

        List<Book> resultats = bookRepository.findByMemberAndStatus(kassoum.getId(), Status.EMPRUNTE);


        //then

        assertEquals(1, resultats.size());
        assertEquals("Solo Leveling", resultats.get(0).getTitle());

        // Apres test : la requête OR retourne aussi les livres d'autres membres (bug)
    }
}