package com.example.testit;

import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import com.example.testit.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookServiceIT {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void emprunterPuisRendreUnLivre() {
        // Given 

        Member referent = memberRepository.save(new Member("referent_kassoum"));
        Member kassoum = new Member("kassoum");
        kassoum.setReferent(referent);
        kassoum = memberRepository.save(kassoum);

        Book livre = new Book("Solo Leveling", "Chugong");
        livre.setStatus(Status.DISPONIBLE);
        livre = bookRepository.save(livre);

        // When 
        Book emprunte = bookService.borrowBook(livre.getId(), kassoum.getId());

        // Then

        //livre emprunter par kassoum
        assertEquals(Status.EMPRUNTE, emprunte.getStatus());
        assertEquals(kassoum.getId(), emprunte.getBorrower().getId());

        // When 

        //kassoum rend le livre
        Book rendu = bookService.returnBook(livre.getId(), kassoum.getId());

        // Then 
        assertEquals(Status.RENDU, rendu.getStatus());

        // Bug : returnBook() ne verifie pas que le memberId correspond au borrower

        
    }

    @Test
    void rendreUnLivreParUnAutreMembre() {
        Member referent = memberRepository.save(new Member("referent_kassoum"));
        Member kassoum = new Member("kassoum");
        kassoum.setReferent(referent);
        kassoum = memberRepository.save(kassoum);
        Member serigne = memberRepository.save(new Member("serigne"));

        Book livre = new Book("Solo Leveling", "Chugong");
        livre.setStatus(Status.DISPONIBLE);
        livre = bookRepository.save(livre);

        bookService.borrowBook(livre.getId(), kassoum.getId());

        Book rendu = bookService.returnBook(livre.getId(), serigne.getId());

        assertEquals(kassoum.getId(), rendu.getBorrower().getId());
    }
    
    
    // Bug : le livre est rendu alors que serigne ne l'avait pas emprunte

}