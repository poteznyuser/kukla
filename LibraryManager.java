import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;

public class LibraryManager {
    
    static class Book {
        private String id;
        private String title;
        private String author;
        private int year; 
        private ArrayList<String> tags; 

        public Book(String id, String title, String author, int year) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.year = year; 
            this.tags = new ArrayList<>(); 
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getYear() {
            return year; 
        }

        public void setYear(int year) {
            this.year = year; 
        }

        public ArrayList<String> getTags() {
            return tags;
        }

        public void addTag(String tag) {
            tags.add(tag); 
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Title: " + title + ", Author: " + author + ", Year: " + year + ", Tags: " + tags;
        }
    }

    
    private static ArrayList<Book> books = new ArrayList<>();
    private static int nextId = 1;
    private static ArrayList<String> operationHistory = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean exit = false;

        
        if (!authenticate(scanner)) {
            System.out.println("Incorrect password, exiting the program.");
            return;
        }

        do {
            printMenu(); 
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    addBook(scanner);
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    updateBook(scanner);
                    break;
                case 4:
                    deleteBook(scanner);
                    break;
                case 5:
                    searchBook(scanner);
                    break;
                case 6:
                    saveBooksToFile();
                    break;
                case 7:
                    loadBooksFromFile();
                    break;
                case 8:
                    undoLastOperation();
                    break;
                case 9:
                    displayTotalBooks();
                    break;
                case 10:
                    addTagsToBook(scanner);
                    break;
                case 11:
                    viewBooksByTag(scanner);
                    break;
                case 12:
                    viewBooksAfterYear(scanner);  
                    break;
                case 13:
                    recommendRandomBook();
                    break;
                case 14:
                    
                    break;
                case 15:
                    exportBooksToCSV();  
                    break;
                case 16:
                    addMultipleBooks(scanner); 
                    break;
                case 17:
                    exit = exitWithConfirmation(scanner);
                    break;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        } while (!exit);

        scanner.close();
    }

    
    private static boolean authenticate(Scanner scanner) {
        String correctPassword = "admin"; 
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        return password.equals(correctPassword);
    }

    
    private static void printMenu() {
        System.out.println("\n===========================================");
        System.out.println("          Library Manager Menu             ");
        System.out.println("===========================================");
        System.out.println("1. Add a Book");
        System.out.println("2. View Books");
        System.out.println("3. Update a Book");
        System.out.println("4. Delete a Book");
        System.out.println("5. Search a Book");
        System.out.println("6. Save Books to File");
        System.out.println("7. Load Books From File");
        System.out.println("8. Undo Last Operation");
        System.out.println("9. Display Total Number of Books");
        System.out.println("10. Add Tags to Book");
        System.out.println("11. View Books by Tag");
        System.out.println("12. View Books Published After a Given Year");
        System.out.println("13. Recommend Random Book");
        System.out.println("14. Validate Title and Author Length");
        System.out.println("15. Export Books to CSV");
        System.out.println("16. Add Multiple Books");  
        System.out.println("17. Exit");  
        System.out.print("Choose an option: ");
    }

    
    private static void addBook(Scanner scanner) {
        String title, author;
        int year;

        do {
            System.out.print("Enter book title: ");
            title = scanner.nextLine();
            System.out.print("Enter book author: ");
            author = scanner.nextLine();

            if (!validateTitleAndAuthor(title, author)) {
                System.out.println("Please try again.");
            } else {
                break;  
            }
        } while (true);

        System.out.print("Enter publication year: ");
        year = scanner.nextInt();
        scanner.nextLine(); 
        String id = "B" + String.format("%06d", nextId++);
        books.add(new Book(id, title, author, year));

        operationHistory.add("ADD:" + id);
        System.out.println("Book added successfully!");
    }

    private static void updateBook(Scanner scanner) {
        System.out.print("Enter book ID to update: ");
        String id = scanner.nextLine();
        for (Book book : books) {
            if (book.getId().equals(id)) {
                System.out.print("Enter new title: ");
                book.setTitle(scanner.nextLine());
                System.out.print("Enter new author: ");
                book.setAuthor(scanner.nextLine());
                System.out.print("Enter new year: ");
                book.setYear(scanner.nextInt());
                scanner.nextLine(); 

                
                operationHistory.add("UPDATE:" + id);
                System.out.println("Book updated successfully!");
                return;
            }
        }
        System.out.println("Book not found!");
    }

    private static void deleteBook(Scanner scanner) {
        System.out.print("Enter book ID to delete: ");
        String id = scanner.nextLine();
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(id)) {
                books.remove(i);

                operationHistory.add("DELETE:" + id);
                System.out.println("Book deleted successfully!");
                return;
            }
        }
        System.out.println("Book not found!");
    }

    private static void searchBook(Scanner scanner) {
        System.out.print("Enter title or author to search: ");
        String query = scanner.nextLine().toLowerCase();
        boolean found = false;
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query) || book.getAuthor().toLowerCase().contains(query)) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    private static boolean validateTitleAndAuthor(String title, String author) {
        if (title.length() < 3 || author.length() < 3) {
            System.out.println("Title and Author must be at least 3 characters long.");
            return false;
        }
        return true;
    }

    private static void addMultipleBooks(Scanner scanner) {
    System.out.println("Enter the path to the file containing books: ");
    String filePath = scanner.nextLine();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] bookData = line.split(",");
            if (bookData.length == 3) {  
                String title = bookData[0].trim();
                String author = bookData[1].trim();
                int year = Integer.parseInt(bookData[2].trim());

                System.out.println("Adding book: " + title + " by " + author);

                if (!validateTitleAndAuthor(title, author)) {
                    System.out.println("Skipping book: " + title + " - " + author);
                    continue;
                }

                String id = "B" + String.format("%06d", nextId++);
                books.add(new Book(id, title, author, year));
                operationHistory.add("ADD:" + id);
                System.out.println("Added book: " + title);
            } else {
                System.out.println("Skipping invalid line: " + line);
            }
        }
        System.out.println("Books added successfully from the file!");
    } catch (IOException e) {
        System.out.println("Error reading file: " + e.getMessage());
    }
}


    private static void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("\nNo books available.");
        } else {
            Collections.sort(books, Comparator.comparing(Book::getTitle));
            System.out.println("\n===========================================");
            System.out.println("          Books in the Library            ");
            System.out.println("===========================================");
            for (Book book : books) {
                System.out.println(book);
            }
            System.out.println("===========================================");
        }
    }

    private static void saveBooksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.txt"))) {
            for (Book book : books) {
                writer.write(book.getId() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.getYear());
                writer.write("," + String.join(";", book.getTags()));
                writer.newLine();
            }
            System.out.println("Books saved to file successfully!");
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    private static void loadBooksFromFile() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter the path to the file: ");
    String filePath = scanner.nextLine(); 

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                String id = parts[0];
                String title = parts[1];
                String author = parts[2];
                int year = Integer.parseInt(parts[3]);
                Book book = new Book(id, title, author, year);
                if (parts.length > 4) {
                    String[] tags = parts[4].split(";");
                    for (String tag : tags) {
                        book.addTag(tag);
                    }
                }
                books.add(book);
            }
        }
        System.out.println("Books loaded from file successfully!");
    } catch (IOException e) {
        System.out.println("Error loading from file: " + e.getMessage());
    }
}


    private static void undoLastOperation() {
        if (operationHistory.isEmpty()) {
            System.out.println("No operations to undo.");
            return;
        }

        String lastOperation = operationHistory.remove(operationHistory.size() - 1);
        String[] parts = lastOperation.split(":");
        String action = parts[0];
        String id = parts[1];

        switch (action) {
            case "ADD":
                books.removeIf(book -> book.getId().equals(id));
                System.out.println("Undo: Book with ID " + id + " removed.");
                break;
            case "UPDATE":
                System.out.println("Undo: Update operation not implemented.");
                break;
            case "DELETE":
                System.out.println("Undo: Delete operation not implemented.");
                break;
            default:
                System.out.println("Unknown operation.");
        }
    }

    private static void displayTotalBooks() {
        System.out.println("\nTotal number of books in the library: " + books.size());
    }

    private static void addTagsToBook(Scanner scanner) {
        System.out.print("Enter book ID to add tags: ");
        String id = scanner.nextLine();
        for (Book book : books) {
            if (book.getId().equals(id)) {
                System.out.print("Enter tags (comma separated): ");
                String[] tags = scanner.nextLine().split(",");
                for (String tag : tags) {
                    book.addTag(tag.trim());
                }
                System.out.println("Tags added successfully!");
                return;
            }
        }
        System.out.println("Book not found!");
    }

    private static void viewBooksByTag(Scanner scanner) {
        System.out.print("Enter tag to search for: ");
        String tag = scanner.nextLine().trim();
        boolean found = false;
        for (Book book : books) {
            if (book.getTags().contains(tag)) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books found with tag: " + tag);
        }
    }

    private static void viewBooksAfterYear(Scanner scanner) {
        System.out.print("Enter year to see books published after that year: ");
        int year = scanner.nextInt();
        scanner.nextLine(); 

        boolean found = false;
        System.out.println("Books published after " + year + ":");
        for (Book book : books) {
            if (book.getYear() > year) {
                System.out.println(book);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No books found published after " + year + ".");
        }
    }

    private static boolean exitWithConfirmation(Scanner scanner) {
        System.out.print("Are you sure you want to exit? (Y/N): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Y");
    }

    private static void exportBooksToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("books.csv"))) {
            writer.write("ID,Title,Author,Year,Tags\n");  
            for (Book book : books) {
                writer.write(book.getId() + "," +
                        book.getTitle() + "," +
                        book.getAuthor() + "," +
                        book.getYear() + "," +
                        String.join(";", book.getTags()) + "\n");
            }
            System.out.println("Books exported to CSV successfully!");
        } catch (IOException e) {
            System.out.println("Error exporting to CSV: " + e.getMessage());
        }
    }

    private static void recommendRandomBook() {
    if (books.isEmpty()) {
        System.out.println("No books available for recommendation.");
        return;
    }

    int randomIndex = (int) (Math.random() * books.size());
    Book randomBook = books.get(randomIndex);

    System.out.println("\nRandom Book Recommendation:");
    System.out.println(randomBook);
}
}