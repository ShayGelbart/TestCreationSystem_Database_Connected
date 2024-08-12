package testing;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;

import testing.Question.Difficulty;

public class Main {

    public static void main(String[] args)
            throws IOException, LessThanThreeAnswersException, AmountOfQuestionsException, ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver"); // line 18
            String dbUrl = "jdbc:postgresql:TestCreation";
            connection = DriverManager.getConnection(dbUrl, "postgres", "shay0307");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Actions");
            while (rs.next()) {
                System.out.println("- " + rs.getString("subjectName"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Subjects subjects = new Subjects();
        File f = new File("Subjects.dat");
        if (!f.exists())
            f.createNewFile();
        else if (f.length() > 0)
            subjects.readFromBinaryFile();
        int mainChoice;
        do {
            System.out.println("Welcome to the main menu");
            System.out.println("If you would like to create a test and pick question and answers,type 1");
            System.out.println("If you would like to alter a pool or create a new pool type 2");
            System.out.println("If you would like to exit the program(all data will be lost) type 0");
            System.out.println("Enter your choice");
            System.out.println("Notice than once you've finished altering the pool you cannot go back to it!");
            mainChoice = sc.nextInt();
            switch (mainChoice) {
                case 1:
                    testCreation(sc, subjects, connection);
                    break;
                case 2:
                    editOrNewMenu(sc, subjects, connection);
                    break;
                case 0:
                    System.out.println("Goodbye,have a good day:)");
                default:
                    if (mainChoice != 0)
                        System.out.println("Try one of the options below");
            }
        } while (mainChoice != 0);
        subjects.writeToBinaryFile();
    }

    // test creation
    public static void testCreation(Scanner sc, Subjects subjects, Connection connection) throws
            IOException, LessThanThreeAnswersException, AmountOfQuestionsException {
        if (subjects.getPools().isEmpty()) {
            System.out.println("There is no pool to make a test out of, create a pool first");
            return;
        }

        Examable test;
        System.out.println(subjects.toStringSubjectNames());
        System.out.println("Enter the index of the subject which you would like your test to be in:");
        int index = sc.nextInt();
        Actions a = subjects.getPoolsAtIndex(index);
        int numOfQuestions = 0;
        do {
            try {
                System.out.println("Enter how many question do you want in the test");
                numOfQuestions = sc.nextInt();
                if (numOfQuestions > 10)
                    throw new AmountOfQuestionsException("The number of questions must be below or equal 10.");
            } catch (AmountOfQuestionsException e) {
                System.out.println(e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.next(); // Clear the invalid input from the scanner
            }
        } while (numOfQuestions > 10 || numOfQuestions <= 0 || numOfQuestions > a.getQuestionArray().size());
        System.out.println("Do you want to create an automatic test or making it manually?");
        System.out.println("Enter true for automatic, false for manual");
        boolean check, isAuto = sc.nextBoolean();
        if (isAuto) {
            test = new AutomaticExam();
            check = test.createExam(a, numOfQuestions);
        } else {
            test = new ManualExam();
            check = test.createExam(a, numOfQuestions);
        }
        if (check)
            System.out.println("Successfully created a test");
        else
            System.out.println("Wasn't able to create a test, try again after altering the pool");
    }

    public static void editOrNewMenu(Scanner sc, Subjects subjects, Connection connection) throws SQLException {
        int editOrNewChoice;
        do {
            System.out.println("Welcome to the mini menu");
            System.out.println(
                    "If you would like to create a new pool and pick question and answers for that subject,type 1");
            System.out.println("If you would like to delete a pool type 2");
            System.out.println("If you would like to alter an existing pool type 3");
            System.out.println("Type 0 in order to go back to main menu");
            System.out.println("Enter your choice");
            editOrNewChoice = sc.nextInt();
            switch (editOrNewChoice) {
                case 1:
                    createAndDefineNewPool(sc, connection);
                    break;
                case 2:
                    deletePool(sc, subjects);
                    break;
                case 3:
                    alterPoolMenu(sc, subjects, connection);
                    break;
                case 0:
                    System.out.println("Goodbye,have a good day:)");
                default:
                    if (editOrNewChoice != 0)
                        System.out.println("Try one of the options below");
            }
        } while (editOrNewChoice != 0);
    }

    public static void createAndDefineNewPool(Scanner sc, Connection connection) throws SQLException {
        System.out.println("You've decided to create a new pool");
        System.out.println("Enter your new subject:");
        String subject = sc.next();
        //Actions actions = new Actions(subject);

        if (!Subjects.addPoolToArray(subject, connection)) {
            System.out.println("Unable to create a new pool, try again");
            return;
        }
        System.out.println("Successfully created a new pool for " + subject);

        System.out.println("Enter how many questions would you like there to be in the " + subject + " pool:");
        int numOfQuestions = sc.nextInt();

        while (Actions.getAmountOfQuestionsInSubjectPool(connection, subject) < numOfQuestions) {
            System.out.println("Enter the question's text:");
            String qText = sc.next();
            Difficulty diff = defineDifficulty(sc);

            System.out.println("Type true if you would like to add an open question, false for an American question:");
            boolean isOpen = sc.nextBoolean();
            if (isOpen) {
                handleOpenQuestion(subject, qText, diff, sc, connection);
            } else {
                handleAmericanQuestion(subject, qText, diff, sc, connection);
            }
        }
    }

    private static void deletePool(Scanner sc, Subjects subjects) {
        int delIndex;
        System.out.println(subjects.toStringSubjectNames());
        System.out.println("Enter which subject do you want to delete");
        do {
            delIndex = sc.nextInt();
        } while (delIndex <= 0 || delIndex > subjects.getPools().size());
        subjects.deletePoolFromArray(delIndex);
        System.out.println("Successfully deleted the pool");
    }

    // pool alteration menu
    public static void alterPoolMenu(Scanner sc, Subjects subjects, Connection connection) throws SQLException {
        int choice, poolIndex = -1;
        idGenerator(subjects);
        do {
            System.out.println("Welcome to the mini menu where you can alter the pool");
            while (poolIndex <= 0 || poolIndex > subjects.getPools().size()) {
                System.out.println(subjects.toStringSubjectNames());
                System.out.println("Enter the index of the pool you would like to alter");
                poolIndex = sc.nextInt();
            }
            Actions a = subjects.getPoolsAtIndex(poolIndex);
            System.out.println("To see the entire pool(questions and then answers) type 1");
            System.out.println("To add an answer to the pool type 2");
            System.out.println("To add a question to the pool type 3");
            System.out.println("To delete a question from the pool type 4");
            System.out.println("To get back to main menu type 0");
            System.out.println("Enter your choice");
            choice = sc.nextInt();
            switch (choice) { // alter the pool
                case 1: // seeing the entire pool, question and then answers
                    System.out.println(a.questionsSeperatedFromAnswers());
                    break;
                case 2: // add a new answer to pool
                    printPlusAddAnswerToArray(a, sc, connection);
                    break;
                case 3: // add a new question to the pool
                    printPlusAddQuestionToArray(a, sc, connection);
                    break;
                case 4: // delete question from the pool
                    printPlusDeleteQuestionFromArray(a, sc);
                    break;
                case 0: // exit back to main menu
                    System.out.println("You've decided to exit this menu");
                    break;
                default:
                    System.out.println("Wrong input, try one of the options");
            }
        } while (choice != 0);
    }

    public static void printPlusAddAnswerToArray(Actions a, Scanner sc, Connection connection) throws SQLException {
        System.out.println("Enter your new answer(string)");
        String strA = sc.next();

        boolean check = a.addAnswerTextToPool(strA, a.getSubName(), connection);
        if (check)
            System.out.println("Successfully added a new answer to the pool");
        else
            System.out.println("Failed to add the answer, try again with a different answer");
    }

    public static void printPlusAddQuestionToArray(Actions a, Scanner sc, Connection connection) throws SQLException {
        System.out.println("Enter your new question (string):");
        String strQuestion = sc.next();

        Difficulty diff = defineDifficulty(sc);

        System.out.println("Type true if you would like to add an open question.");
        System.out.println("Type false if you would like to add an American question.");
        boolean isOpen = sc.nextBoolean();

        if (isOpen) {
            handleOpenQuestion(a, strQuestion, diff, sc, connection);
        } else {
            handleAmericanQuestion(a, strQuestion, diff, sc, connection);
        }
    }

    // Helper function
    private static void handleOpenQuestion(String subject, String strQuestion, Difficulty diff, Scanner sc, Connection connection) throws SQLException {
        System.out.println(Actions.answerTextToString(connection, subject));
        int choice = getAnswerChoice(sc);
        //AnswerText at;
        String answer;
        if (choice == 1) {
            System.out.println(Actions.answerTextToString(connection, subject));
            System.out.println("Please enter the index of the answer for your open question:");
            int solutionIndex = sc.nextInt();
            answer = Actions.getAnswerTextArrayAtIndex(connection, subject, solutionIndex);
        } else {
            System.out.println("Enter your new answer:");
            answer = sc.next();
            if(!AnswerText.InsertToTable(connection, answer)) { // if it already exists in the AnswerText table it's fine(rowsAffected >= 0),
                // if it was an exception, the user should try again.
                System.out.println("An error occurred, please try again");
                return;
            }
            if (Actions.addAnswerTextToPool(answer, subject, connection))
                System.out.println("Successfully added a new answer to the pool");
            else
                System.out.println("Failed to add the answer, try again with a different answer");
        }

        int newId = OpenQuestion.InsertToTable(connection, strQuestion, diff, answer);
        if(newId == 0) { // if it already exists in the AnswerText table it's fine( > 0),
                // if it was an exception(==0), the user should try again.
                System.out.println("An error occurred, please try again");
                return;
        }

        if (Actions.addQuestionToPool(connection, newId, subject)) {
            System.out.println("Successfully added a new open question to the " + subject + " pool.");
        } else {
            System.out.println("Failed to add the open question, try again.");
        }
    }

    // Helper function
    private static void handleAmericanQuestion(String subject, String strQuestion, Difficulty diff, Scanner sc, Connection connection) throws SQLException {
        Question aq = new AmericanQuestion(strQuestion, diff);

        System.out.println("Enter how many answers do you want the question to have:");
        int ansAmount = sc.nextInt();

        while (aq.getAnswerCount() < ansAmount) {
            addAnswerToAmericanQuestion(subject, aq, sc, connection);
        }

        boolean check = subject.addQuestionToArray(aq);
        if (check) {
            System.out.println("Successfully added a new American question to the " + subject.getSubName() + " pool.");
        } else {
            System.out.println("Failed to add the American question, try again.");
        }
    }

    public static void addAnswerToAmericanQuestion(Actions a, Question aq, Scanner sc, Connection connection) throws SQLException {
        System.out.println(a.answerTextToString());
        int choice = getAnswerChoice(sc);

        boolean checkAddAnswer;
        if (choice == 1)
            checkAddAnswer = addAnswerFromPoolToAmericanQuestion(a, aq, sc);
        else
            checkAddAnswer = addNewAnswerToAmericanQuestion(a, aq, sc, connection);

        if (checkAddAnswer) {
            System.out.println("Successfully added your answer to the question.");
        } else {
            System.out.println("Failed to add your answer, try again.");
        }
    }

    private static int getAnswerChoice(Scanner sc) {
        int choice;
        do {
            System.out.println("Is your answer:\n1. From the pool\n2. A new answer");
            choice = sc.nextInt();
        } while (choice != 1 && choice != 2);
        return choice;
    }

    private static boolean addAnswerFromPoolToAmericanQuestion(Actions a, Question aq, Scanner sc) {
        System.out.println(a.answerTextToString());
        System.out.println("Enter the answer's index:");
        int ansIndex = sc.nextInt();

        System.out.println("Is the answer true or false (true/false)?");
        boolean isTrue = sc.nextBoolean();

        return aq.addAnswerToQuestion(a.getAnswerTextArrayAtIndex(ansIndex), isTrue);
    }

    private static boolean addNewAnswerToAmericanQuestion(Actions a, Question aq, Scanner sc, Connection connection) throws SQLException {
        System.out.println("Enter your new answer:");
        String strA = sc.next();

        if (a.addAnswerTextToPool(strA, a.getSubName(), connection))
            System.out.println("Successfully added a new answer to the pool");
        else
            System.out.println("Failed to add the answer, try again with a different answer");

        System.out.println("Is the answer true or false (true/false)?");
        boolean isTrue = sc.nextBoolean();

        return aq.addAnswerToQuestion(new AnswerText(strA), isTrue);
    }

    public static void printPlusDeleteQuestionFromArray(Actions a, Scanner sc) {
        System.out.println(a.questionArrayToString());
        System.out.println("Enter the number of the question you want to delete");
        int index = sc.nextInt();
        boolean check = a.deleteQuestionFromArray(index);
        if (check)
            System.out.println("Successfully deleted question number-" + index);
        else
            System.out.println("Failed to delete question from array, try with a different index");
    }

    public static Difficulty defineDifficulty(Scanner sc) {
        System.out.println("Enter how difficult is your question:(Easy, Medium, Hard)");
        System.out.println("For Easy enter " + Difficulty.Easy.ordinal());
        System.out.println("For Medium enter " + Difficulty.Medium.ordinal());
        System.out.println("For Hard enter " + Difficulty.Hard.ordinal());
        int index = sc.nextInt();
        while (index < Difficulty.Easy.ordinal() || index > Difficulty.Hard.ordinal()) {
            System.out.println("Try again to enter the index");
            index = sc.nextInt();
        }
        if (index == Difficulty.Easy.ordinal())
            return Difficulty.Easy;
        else if (index == Difficulty.Medium.ordinal())
            return Difficulty.Medium;
        return Difficulty.Hard;
    }

    public static void idGenerator(Subjects ss) {
        int idCount = 0;
        for (int i = 1; i <= ss.getPools().size(); i++) {
            for (int j = 1; j <= ss.getPoolsAtIndex(i).getQuestionArray().size(); j++) {
                if (ss.getPoolsAtIndex(i).getQuestionArrayAtIndex(j).getId() > idCount)
                    idCount = ss.getPoolsAtIndex(i).getQuestionArrayAtIndex(j).getId();
            }
        }

        OpenQuestion q1 = new OpenQuestion(null, null, null);
        q1.setStaticId(idCount + 1);

    }

}