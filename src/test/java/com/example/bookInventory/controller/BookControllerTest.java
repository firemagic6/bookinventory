package com.example.bookInventory.controller;

import com.example.bookInventory.model.Book;
import com.example.bookInventory.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false) // addFilter = false disables security for unit test
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_ENDPOINT = "/api/books";

    private Book book1, book2;
    private List<Book> bookList = new ArrayList<>();

    // Run before each JUNIT test operation
    @BeforeEach
    void setUp() {

        // Delete all records in the database before starting
        bookRepository.deleteAll();

        // arrange (precondition)
        book1 = Book.builder()
                .title("Bleach")
                .author("Tite Kubo")
                .build();

        // arrange (precondition)
        book2 = Book.builder()
                .title("DragonBall")
                .author("Akira Toriyama")
                .build();

        bookList.add(book1);
        bookList.add(book2);
    }


    @Test
    @DisplayName("** JUNIT test: get all books. **")
    void getAllBooks() throws Exception {
        // arrange - setup precondition
        bookRepository.saveAll(bookList);

        // act - action or behaviour to test
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT));

        // assert - verify the output
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(bookList.size())));
    }

    @Test
    @DisplayName("** JUNIT test: get book by Id")
    void getBookById() throws Exception {
        // arrange - setup precondition
        bookRepository.save(book1);

        // act - action or behaviour to test
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT.concat("/{id}"), book1.getId()));

        // assert - verify the output
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));
    }

    @Test
    @DisplayName("** JUNIT test:create a book ")
    void createBook() throws Exception{
        // arrange - setup precondition
        String requestBody = objectMapper.writeValueAsString(book1);

        // act - action or behaviour to test
        ResultActions resultActions = mockMvc.perform(post(API_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // assert - verify the output
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));
    }

    @Test
    @DisplayName("** JUNIT test: update a ")
    void updateBook() throws Exception {
        // arrange - setup precondition
        bookRepository.save(book1);

        Book updatebook1 = bookRepository.findById(book1.getId()).get();

        updatebook1.setTitle("Updated Customer X");
        updatebook1.setAuthor("udpated_customer_x@gmail.com");

        String requestBody = objectMapper.writeValueAsString(updatebook1);

        // act - action or behaviour to test
        ResultActions resultActions = mockMvc.perform(put(API_ENDPOINT.concat("/{id}"), updatebook1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // assert - verify the output
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(updatebook1.getTitle()))
                .andExpect(jsonPath("$.author").value(updatebook1.getAuthor()));
    }

    @Test
    void deleteBook() throws Exception {
        // arrange - setup precondition
        bookRepository.save(book1);

        Book deleteBook1 = bookRepository.findById(book1.getId()).get();

        String expectedResponse = String.format("%s deleted successfully", deleteBook1.getTitle());

        // act - action or behaviour to test
        ResultActions resultActions = mockMvc.perform(delete(API_ENDPOINT.concat("/{id}"), deleteBook1.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // assert - verify the output
        resultActions.andExpect(status().isOk())
                .andDo(print())
                // Check that the body response matches the expected message
                .andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
    }

}