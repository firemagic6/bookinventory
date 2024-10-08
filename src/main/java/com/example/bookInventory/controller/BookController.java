package com.example.bookInventory.controller;


import com.example.bookInventory.exception.ResourceNotFoundException;
import com.example.bookInventory.model.Book;
import com.example.bookInventory.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    @Autowired
    private BookService bookService;
    @GetMapping
//    public List<Book> getAllBooks() {
//        return bookService.findAllBooks();
//    }

    public ResponseEntity<Object> getAllBooks() {
        List<Book> bookList = bookService.findAllBooks();

        if (bookList.isEmpty())
            throw new ResourceNotFoundException();

        return new ResponseEntity<>(bookService.findAllBooks(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
//    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
//        Optional<Book> book = bookService.findBookById(id);
//        if (book.isPresent()) {
//            return ResponseEntity.ok(book.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.findBookById(id).orElseThrow(() -> new ResourceNotFoundException());

        return new ResponseEntity<>(book, HttpStatus.OK);
    }


    @PostMapping
//    public Book createBook(@Valid @RequestBody Book book) {
//        return bookService.saveBook(book);
//    }

    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(bookService.saveBook(book), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
//    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
//        Optional<Book> book = bookService.findBookById(id);
//        if (book.isPresent()) {
//            Book updatedBook = book.get();
//            updatedBook.setTitle(bookDetails.getTitle());
//            updatedBook.setAuthor(bookDetails.getAuthor());
//            return ResponseEntity.ok(bookService.saveBook(updatedBook));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        Book checkBook = bookService.findBookById(id).map(_book -> {
            _book.setTitle(bookDetails.getTitle());
            _book.setAuthor(bookDetails.getAuthor());

            return bookService.saveBook(_book);
        }).orElseThrow(() -> new ResourceNotFoundException());

        return new ResponseEntity<>(checkBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
//        Optional<Book> book = bookService.findBookById(id);
//        if (book.isPresent()) {
//            bookService.deleteBookById(id);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    public ResponseEntity<Object> deleteBook(@PathVariable Long id) {

        Book checkBook = bookService.findBookById(id).map(_book -> {
            bookService.deleteBookById(_book.getId());
            return _book;
        }).orElseThrow(() -> new ResourceNotFoundException());

        String response = String.format("%s deleted successfully", checkBook.getTitle());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

