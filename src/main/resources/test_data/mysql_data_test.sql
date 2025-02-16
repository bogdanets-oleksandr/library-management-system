INSERT INTO library.books (book_isbn) VALUES ('9780141974873');
INSERT INTO library.books (book_isbn) VALUES ('9780735211292');

INSERT INTO library.authorization_staff(username, password) VALUES ('demostaff', SHA2('demopassword', 256));
INSERT INTO library.authorization_students(username, password) VALUES ('demostudent', SHA2('demopassword', 256));

INSERT INTO library.recipients(first_name, last_name) VALUES ('oleksandr', 'bogdanets');
INSERT INTO library.books (book_isbn, book_holder) VALUES ('9781449373320', 1)