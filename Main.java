package testing;

import java.io.IOException;
import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;

public class Main {

    public static void main(String[] args)
            throws IOException, LessThanThreeAnswersException, ClassNotFoundException, SQLException {
        Scanner sc = new Scanner(System.in);
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver"); // line 18
            String dbUrl = "jdbc:postgresql:TestCreation";
            connection = DriverManager.getConnection(dbUrl, "postgres", "shay0307");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Question");
            while (rs.next()) {
                System.out.println("- " + rs.getString("questionText"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //Subjects subjects = new Subjects();
//        File f = new File("Subjects.dat");
//        if (!f.exists())
//            f.createNewFile();
//        else if (f.length() > 0)
//            subjects.readFromBinaryFile();
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
                    testCreation(sc, connection);
                    break;
                case 2:
                    editOrNewMenu(sc, connection);
                    break;
                case 0:
                    System.out.println("Goodbye,have a good day:)");
                default:
                    if (mainChoice != 0)
                        System.out.println("Try one of the options below");
            }
        } while (mainChoice != 0);
        // subjects.writeToBinaryFile();
    }

    // test creation
    public static void testCreation(Scanner sc, Connection connection) throws
            IOException, LessThanThreeAnswersException, SQLException {
        int amountOfPools = Subjects.getAmountOfPools(connection);
        if (amountOfPools == 0) {
            System.out.println("There is no pool to make a test out of, create a pool first");
            return;
        } else if (amountOfPools == -1) {
            System.out.println("An error occurred, please try again");
            return;
        }

        Examable test;
        System.out.println(Subjects.toStringSubjectNames(connection));
        System.out.println("Enter the index of the subject which you would like your test to be in:");
        int index = sc.nextInt();
        String subjectName = Subjects.getPoolsAtIndex(index, connection);
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
        } while (numOfQuestions > 10 || numOfQuestions <= 0 || numOfQuestions > Pool.getAmountOfQuestionsInSubjectPool(connection, subjectName));
        System.out.println("Do you want to create an automatic test or making it manually?");
        System.out.println("Enter true for automatic, false for manual");
        boolean isAuto = sc.nextBoolean();
        if (isAuto) {
            test = new AutomaticExam();
        } else {
            test = new ManualExam();
        }
        if (test.createExam(subjectName, numOfQuestions, connection))
            System.out.println("Successfully created a test");
        else
            System.out.println("Wasn't able to create a test, try again after altering the pool");
    }

    public static void editOrNewMenu(Scanner sc, Connection connection) throws SQLException {
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
                    deletePool(sc, connection);
                    break;
                case 3:
                    alterPoolMenu(sc, connection);
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

        if (!Subjects.addPoolToArray(subject, connection)) {
            System.out.println("Unable to create a new pool, try again");
            return;
        }
        System.out.println("Successfully created a new pool for " + subject);

        System.out.println("Enter how many questions would you like there to be in the " + subject + " pool:");
        int numOfQuestions = sc.nextInt();


        while (numOfQuestions > 0) {
            System.out.println("Enter the question's text:");
            String qText = sc.next();
            String diff = defineDifficulty(sc, connection);
            if (diff == null) {
                System.out.println("An error occurred, try again");
                return;
            }
            System.out.println("Type true if you would like to add an open question, false for an American question:");
            boolean isOpen = sc.nextBoolean();
            if (isOpen) {
                handleOpenQuestion(subject, qText, diff, sc, connection);
            } else {
                handleAmericanQuestion(subject, qText, diff, sc, connection);
            }
            numOfQuestions--;
        }
    }

    private static void deletePool(Scanner sc, Connection connection) throws SQLException {
        int delIndex;
        System.out.println(Subjects.toStringSubjectNames(connection));
        do {
            System.out.println("Enter the index of the subject you want to delete");
            delIndex = sc.nextInt();
        } while (delIndex <= 0 || delIndex > Subjects.getAmountOfPools(connection));
        if (Subjects.deletePoolFromArray(delIndex, connection))
            System.out.println("Successfully deleted the pool");
        else
            System.out.println("An error occurred, try again");
    }

    // pool alteration menu
    public static void alterPoolMenu(Scanner sc, Connection connection) throws SQLException {
        int choice, poolIndex = -1;
        //idGenerator(subjects);
        do {
            System.out.println("Welcome to the mini menu where you can alter the pool");
            while (poolIndex <= 0 || poolIndex > Subjects.getAmountOfPools(connection)) {
                System.out.println(Subjects.toStringSubjectNames(connection));
                System.out.println("Enter the index of the pool you would like to alter");
                poolIndex = sc.nextInt();
            }
            String subjectName = Subjects.getPoolsAtIndex(poolIndex, connection);
            System.out.println("To see the entire pool(questions and then answers) type 1");
            System.out.println("To add an answer to the pool type 2");
            System.out.println("To add a question to the pool type 3");
            System.out.println("To delete a question from the pool type 4");
            System.out.println("To get back to main menu type 0");
            System.out.println("Enter your choice");
            choice = sc.nextInt();
            switch (choice) { // alter the pool
                case 1: // seeing the entire pool, question and then answers
                    System.out.println(Pool.questionsSeperatedFromAnswers(connection, subjectName));
                    break;
                case 2: // add a new answer to pool
                    printPlusAddAnswerToArray(subjectName, sc, connection);
                    break;
                case 3: // add a new question to the pool
                    printPlusAddQuestionToArray(subjectName, sc, connection);
                    break;
                case 4: // delete question from the pool
                    printPlusDeleteQuestionFromArray(subjectName, connection, sc);
                    break;
                case 0: // exit back to main menu
                    System.out.println("You've decided to exit this menu");
                    break;
                default:
                    System.out.println("Wrong input, try one of the options");
            }
        } while (choice != 0);
    }

    public static void printPlusAddAnswerToArray(String subject, Scanner sc, Connection connection) throws SQLException {
        System.out.println("Enter your new answer(string)");
        String strA = sc.next();
        if (!AnswerText.InsertToTable(connection, strA)) {
            System.out.println("An error occurred, try again");
            return;
        }
        int check = Pool.addAnswerTextToPool(strA, subject, connection);
        if (check == 1)
            System.out.println("Successfully added a new answer to the pool");
        else if (check == 0)
            System.out.println("Answer is already in the pool, try again with a different answer");
        else
            System.out.println("An error occurred, try again");
    }

    public static void printPlusAddQuestionToArray(String subjectName, Scanner sc, Connection connection) throws SQLException {
        System.out.println("Enter your new question (string):");
        String strQuestion = sc.next();

        String diff = defineDifficulty(sc, connection);

        System.out.println("Type true if you would like to add an open question.");
        System.out.println("Type false if you would like to add an American question.");
        boolean isOpen = sc.nextBoolean();

        if (isOpen) {
            handleOpenQuestion(subjectName, strQuestion, diff, sc, connection);
        } else {
            handleAmericanQuestion(subjectName, strQuestion, diff, sc, connection);
        }
    }

    // Helper function
    private static void handleOpenQuestion(String subject, String strQuestion, String diff, Scanner sc, Connection connection) throws SQLException {
        int choice = 2;
        System.out.println(Pool.answerTextPoolToString(connection, subject));
        if (Pool.getAmountOfAnswersInSubjectPool(connection, subject) > 0)
            choice = getAnswerChoice(sc);
        //AnswerText at;
        String answer;
        if (choice == 1) {
            System.out.println(Pool.answerTextPoolToString(connection, subject));
            System.out.println("Please enter the index of the answer for your open question:");
            int solutionIndex = sc.nextInt();
            answer = Pool.getAnswerTextArrayAtIndex(connection, subject, solutionIndex);
        } else {
            System.out.println("Enter your new answer:");
            answer = sc.next();
            if (!AnswerText.InsertToTable(connection, answer)) { // if it already exists in the AnswerText table it's fine(rowsAffected >= 0),
                // if it was an exception, the user should try again.
                System.out.println("An error occurred, please try again");
                return;
            }

            int addAnswerToPoolCheck = Pool.addAnswerTextToPool(answer, subject, connection);
            if (addAnswerToPoolCheck == 1) {
                System.out.println("Successfully added a new answer to the pool");
            } else if (addAnswerToPoolCheck == 0) { // won't get kicked out, just sent with the answer already in DB
                System.out.println("Answer is already in the pool, you will now continue with that answer");
            } else { // exception happened, kicked out of the function
                System.out.println("An error occurred, please try again");
                return;
            }
        }

        int newId = OpenQuestion.InsertToTable(connection, strQuestion, diff, answer);
        if (newId == 0) { // if it already exists in the AnswerText table it's fine( > 0),
            // if it was an exception(==0), the user should try again.
            System.out.println("An error occurred, please try again");
            return;
        }

        if (Pool.addQuestionToPool(connection, newId, subject)) {
            System.out.println("Successfully added a new open question to the " + subject + " pool.");
        } else {
            System.out.println("Failed to add the open question, try again.");
        }
    }

    // Helper function
    private static void handleAmericanQuestion(String subject, String strQuestion, String diff, Scanner sc, Connection connection) throws SQLException {
        //Question aq = new AmericanQuestion(strQuestion, diff);
        int newId = AmericanQuestion.InsertToTable(connection, strQuestion, diff), returnValue, ansAmount;
        if (newId == 0) { // if it already exists in the AnswerText table it's fine( > 0),
            // if it was an exception(==0), the user should try again.
            System.out.println("An error occurred, please try again");
            return;
        }

        do {
            System.out.println("Enter how many answers do you want the question to have:");
            ansAmount = sc.nextInt(); // user input
        } while (ansAmount < 0);


        while (ansAmount > 0) {
            returnValue = addAnswerToAmericanQuestion(subject, newId, sc, connection);
            if (returnValue == -1) { // exception
                System.out.println("An error occurred, please try again");
            } else if (returnValue == 0) { // question already has that answer
                System.out.println("Answer already exists, please try again");
            } else {// new answer added
                ansAmount--;
                System.out.println("Answer was successfully added to the question");
            }
        }

        if (Pool.addQuestionToPool(connection, newId, subject)) {
            System.out.println("Successfully added a new American question to the " + subject + " pool.");
        } else {
            System.out.println("Failed to add the American question, try again.");
        }
    }

    public static int addAnswerToAmericanQuestion(String subjectName, int id, Scanner sc, Connection connection) throws SQLException {
        System.out.println(Pool.answerTextPoolToString(connection, subjectName));
        int choice = getAnswerChoice(sc);

        int checkAddAnswer;
        if (choice == 1)
            checkAddAnswer = addAnswerFromPoolToAmericanQuestion(subjectName, id, sc, connection);
        else
            checkAddAnswer = addNewAnswerToAmericanQuestion(subjectName, id, sc, connection);

        return checkAddAnswer;
    }

    private static int getAnswerChoice(Scanner sc) {
        int choice;
        do {
            System.out.println("Is your answer:\n1. From the pool\n2. A new answer");
            choice = sc.nextInt();
        } while (choice != 1 && choice != 2);
        return choice;
    }

    private static int addAnswerFromPoolToAmericanQuestion(String subject, int id, Scanner sc, Connection connection) throws SQLException {
        System.out.println(Pool.answerTextPoolToString(connection, subject));
        System.out.println("Enter the answer's index:");
        int ansIndex = sc.nextInt();
        String answerText = AnswerText.getAnswerTextByIndex(ansIndex, connection);
        System.out.println("Is the answer true or false (true/false)?");
        boolean isTrue = sc.nextBoolean();
        //= Answer.insertToTableByIndex(ansIndex, connection);
        return AmericanQuestion.addAnswerToQuestion(answerText, id, isTrue, connection);
    }

    private static int addNewAnswerToAmericanQuestion(String subject, int qId, Scanner sc, Connection connection) throws SQLException {
        System.out.println("Enter your new answer:");
        String answerText = sc.next();

        if (!AnswerText.InsertToTable(connection, answerText)) {
            System.out.println("An error occurred, please try again");
            return -1;
        }

        int checkAddAnswer = Pool.addAnswerTextToPool(answerText, subject, connection);
        if (checkAddAnswer == 1) {
            System.out.println("Successfully added a new answer to the pool");
        } else if (checkAddAnswer == 0) { // won't get kicked out, just sent with the answer already in DB
            System.out.println("Answer is already in the pool, you will continue with that answer");
        } else { // exception happened, kicked out of the function
            System.out.println("An error occurred, please try again");
            return -1;
        }

        System.out.println("Is the answer true or false (true/false)?");
        boolean isTrue = sc.nextBoolean();

        if (!Answer.insertToTable(answerText, isTrue, connection)) {
            System.out.println("An error occurred, please try again");
            return -1;
        }

        return AmericanQuestion.addAnswerToQuestion(answerText, qId, isTrue, connection);
    }

    public static void printPlusDeleteQuestionFromArray(String subjectName, Connection connection, Scanner sc) throws SQLException {
        System.out.println(Pool.questionPoolToString(subjectName, connection));
        System.out.println("Enter the index of the question you want to delete");
        int index = sc.nextInt();
        if (Pool.deleteQuestionFromArray(index, connection))
            System.out.println("Successfully deleted question number-" + index);
        else
            System.out.println("Failed to delete question from array, try with a different index");
    }

    public static String defineDifficulty(Scanner sc, Connection connection) throws SQLException {
        String options[] = Question.printDifficulty(connection);
        System.out.println(options[options.length - 1]);
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

        // Validate the choice
        while (choice < 0 || choice >= options.length - 1) {
            System.out.println("Try again to enter the index");
            choice = sc.nextInt();
        }
        return options[choice];
    }


//        System.out.println("Enter how difficult is your question:(Easy, Medium, Hard)");
//        System.out.println("For Easy enter " + Difficulty.Easy.ordinal());
//        System.out.println("For Medium enter " + Difficulty.Medium.ordinal());
//        System.out.println("For Hard enter " + Difficulty.Hard.ordinal());
//        int index = sc.nextInt();
//        while (index < Difficulty.Easy.ordinal() || index > Difficulty.Hard.ordinal()) {
//
//        }
//        if (index == Difficulty.Easy.ordinal())
//            return Difficulty.Easy;
//        else if (index == Difficulty.Medium.ordinal())
//            return Difficulty.Medium;
//        return Difficulty.Hard;


//    public static void idGenerator(Subjects ss) {
//        int idCount = 0;
//        for (int i = 1; i <= ss.getPools().size(); i++) {
//            for (int j = 1; j <= ss.getPoolsAtIndex(i).getQuestionArray().size(); j++) {
//                if (ss.getPoolsAtIndex(i).getQuestionArrayAtIndex(j).getId() > idCount)
//                    idCount = ss.getPoolsAtIndex(i).getQuestionArrayAtIndex(j).getId();
//            }
//        }
//
//        OpenQuestion q1 = new OpenQuestion(null, null, null);
//        q1.setStaticId(idCount + 1);
//
//    }

}