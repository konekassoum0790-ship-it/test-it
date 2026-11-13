package com.example.testit;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import com.example.testit.service.BookService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Test
    void emprunterUnLivreDisponible() {
        // Les mocks
        BookRepository bookRepository = mock(BookRepository.class);
        MemberRepository memberRepository = mock(MemberRepository.class);
        MailService mailService = mock(MailService.class);
        BookService bookService = new BookService(bookRepository, memberRepository, mailService);

        // Given
        Book book = new Book("Solo Leveling", "Chugong");
        book.setId(1L);
        book.setStatus(Status.DISPONIBLE);

        Member member = new Member("Sung Jin-woo");
        member.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findByMemberAndStatus(1L, Status.RENDU)).thenReturn(Collections.emptyList());
        when(bookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        Book resultat = bookService.borrowBook(1L, 1L);

        // Then
        assertEquals(Status.EMPRUNTE, resultat.getStatus());
        assertEquals(member, resultat.getBorrower());

        // envoi de mail
        verify(mailService).sendMail(eq(member), anyString(), anyString());

        //Apres test on constate que : 
        // Verifie envoi de mail echoue : borrowBook() n'envoie aucun mail (bug dans le code)
    }
}