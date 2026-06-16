USE lms;

INSERT INTO PERSON (ID, PNAME, PASSWORD, ADDRESS, PHONE_NO) VALUES
(1, 'Admin Librarian', '1', 'Library Office', 1000000001),
(2, 'Checkout Clerk', '2', 'Front Desk', 1000000002),
(3, 'Demo Borrower', '3', 'Student Housing', 1000000003);

INSERT INTO STAFF (S_ID, TYPE, SALARY) VALUES
(1, 'Librarian', 50000),
(2, 'Clerk', 30000);

INSERT INTO LIBRARIAN (L_ID, OFFICE_NO) VALUES
(1, 101);

INSERT INTO CLERK (C_ID, DESK_NO) VALUES
(2, 1);

INSERT INTO BORROWER (B_ID) VALUES
(3);

INSERT INTO BOOK (ID, TITLE, AUTHOR, SUBJECT, IS_ISSUED) VALUES
(1, 'Clean Code', 'Robert Martin', 'Programming', FALSE),
(2, 'Effective Java', 'Joshua Bloch', 'Programming', FALSE),
(3, 'Design Patterns', 'Gamma et al', 'Software Design', FALSE);
