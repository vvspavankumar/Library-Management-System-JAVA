package LMS;

public class DomainSmokeTest {
    public static void main(String[] args) {
        Person.setIDCount(0);
        Book.setIDCount(0);

        Library library = Library.getInstance();
        library.setName("Test Library");
        library.setFine(20);
        library.setRequestExpiry(7);
        library.setReturnDeadline(5);

        Borrower borrower = new Borrower(-1, "Demo Borrower", "Campus", 1234567890);
        Clerk clerk = new Clerk(-1, "Demo Clerk", "Front Desk", 1234567891, 25000, 1);
        Book book = new Book(-1, "Effective Java", "Programming", "Joshua Bloch", false);

        library.addBorrower(borrower);
        library.addClerk(clerk);
        library.addBookinLibrary(book);
        book.issueBook(borrower, clerk);

        assertEquals("Test Library", library.getLibraryName(), "library name");
        assertEquals(1, library.getBooks().size(), "book count");
        assertEquals(1, borrower.getBorrowedBooks().size(), "borrowed book count");
        assertTrue(book.getIssuedStatus(), "book should be issued");

        System.out.println("Domain smoke test passed.");
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
