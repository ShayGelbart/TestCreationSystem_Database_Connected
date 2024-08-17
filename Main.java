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
            Class.forName("org.postgresql.Driver"); // line 16
            String dbUrl = "jdbc:postgresql:TestCreation";
            connection = DriverManager.getConnection(dbUrl, "postgres", "shay0307");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        int mainChoice;
        do {
            System.out.println("Welcome to the main menu");
            System.out.println("Enter your choice\n" + "1.Create a test and pick question and answers\n"
                    + "2.Alter a pool or create a new pool\n" +
                    "3.Print All Information in our Database\n" +
                    "3.EXIT");

            mainChoice = readInRange(0, 3, sc);
            switch (mainChoice) {
                case 1:
                    testCreation(sc, connection);
                    break;
                case 2:
                    editOrNewMenu(sc, connection);
                    break;
                case 3:
                    printAllDataBase(connection);
                case 0:
                    System.out.println("Goodbye,have a good day:)");
                default:
                    if (mainChoice != 0)
                        System.out.println("Try one of the options below");
            }
        } while (mainChoice != 0);
    }

    // test creation
    public static void testCreation(Scanner sc, Connection connection) throws
            IOException, SQLException {
        int amountOfPools = Subjects.getAmountOfPools(connection);
        if (amountOfPools == 0) {
            System.out.println("There is no pool to make a test out of, create a pool first");
            return;
        } else if (amountOfPools == -1) {
            System.out.println("An error occurred, please try again");
            return;
        }

        System.out.println(Subjects.toStringSubjectNames(connection));
        System.out.println("Enter the index of the subject which you would like your test to be in:");
        int index = readInRange(1, amountOfPools, sc);
        String subjectName = Subjects.getPoolsAtIndex(index, connection);

        int numOfQuestions = 0;
        int amountOfQuestionsInSubject = Pool.getAmountOfQuestionsInSubjectPool(connection, subjectName);
        if (amountOfQuestionsInSubject <= 0) {
            System.out.println("There's no questions in the subject.Try adding new questions");
            return;
        }

        do {
            try {
                System.out.println("Enter how many question do you want in the test");
                numOfQuestions = readInRange(1, amountOfQuestionsInSubject, sc);
                if (numOfQuestions > 10)
                    throw new AmountOfQuestionsException("The number of questions must be below or equal 10.");
            } catch (AmountOfQuestionsException e) {
                System.out.println(e.getMessage());
            }
        } while (numOfQuestions > 10);

        System.out.println("Do you want to create an automatic test or making it manually?");
        System.out.println("Enter true for automatic, false for manual");
        boolean check = false, isAuto = sc.nextBoolean();
        sc.nextLine();

        if (isAuto) {
            if (Pool.getAmountOfAnswersInSubjectPool(connection, subjectName) < 4) {
                System.out.println("There is no enough answers in the subject.Try adding new answers");
            } else
                check = AutomaticExam.createExam(subjectName, numOfQuestions, connection, sc);
        } else {
            check = ManualExam.createExam(subjectName, numOfQuestions, connection, sc);
        }
        if (check)
            System.out.println("Successfully created a test");
        else
            System.out.println("Wasn't able to create a test, try again after altering the pool");


    }

    public static void editOrNewMenu(Scanner sc, Connection connection) throws SQLException {
        int editOrNewChoice;
        do {
            System.out.println("Welcome to the mini menu");
            System.out.println(
                    "1.Create a new pool and pick question and answers for that subject\n" + "2.Delete a pool\n" + "3.Alter an existing pool\n" +
                            "0.BACK TO MAIN MENU\n" + "Enter your choice:");
            editOrNewChoice = readInRange(0, 3, sc);
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
                default:
                    if (editOrNewChoice != 0)
                        System.out.println("Try one of the options below");
            }
        } while (editOrNewChoice != 0);
    }

    public static void createAndDefineNewPool(Scanner sc, Connection connection) throws SQLException {
        System.out.println("You've decided to create a new pool");
        System.out.println("Enter your new subject:");
        String subject = checkString(sc);

        if (!Subjects.insertToTable(subject, connection)) {
            System.out.println("Unable to create a new pool, try again");
            return;
        }
        System.out.println("Successfully created a new pool for " + subject);

        System.out.println("Enter how many questions would you like there to be in the " + subject + " pool:");
        int numOfQuestions = readInRange(0, 10000000, sc);


        while (numOfQuestions > 0) {
            System.out.println("Enter the question's text:");
            String qText = checkString(sc);
            if (Question.isQuestionTextInTable(connection, qText, subject)) {
                System.out.println("Question " + qText + " already exists in the" + subject + " pool");
            } else {
                String diff = defineDifficulty(sc, connection);
                if (diff == null) {
                    System.out.println("An error occurred, try again");
                    return;
                }
                System.out.println("Type true if you would like to add an open question, false for an American question:");
                boolean isOpen = sc.nextBoolean();
                sc.nextLine();

                if (isOpen) {
                    handleOpenQuestion(subject, qText, diff, sc, connection);
                } else {
                    handleAmericanQuestion(subject, qText, diff, sc, connection);
                }
                numOfQuestions--;
            }
        }
    }

    private static void deletePool(Scanner sc, Connection connection) throws SQLException {
        int delIndex;
        System.out.println(Subjects.toStringSubjectNames(connection));

        System.out.println("Enter the index of the subject you want to delete");
        delIndex = readInRange(0, Subjects.getAmountOfPools(connection), sc);

        if (Subjects.deletePoolFromArray(delIndex, connection))
            System.out.println("Successfully deleted the pool");
        else
            System.out.println("An error occurred, try again");
    }

    // pool alteration menu
    public static void alterPoolMenu(Scanner sc, Connection connection) throws SQLException {
        int choice, poolIndex = -1;
        //idGenerator(subjects);
        System.out.println(Subjects.toStringSubjectNames(connection));
        System.out.println("Enter the index of the pool you would like to alter");
        poolIndex = readInRange(0, Subjects.getAmountOfPools(connection), sc);

        do {
            String subjectName = Subjects.getPoolsAtIndex(poolIndex, connection);

            System.out.println("Welcome to the mini menu where you can alter the pool");
            System.out.println("1.See the entire pool(questions and then answers)\n" +
                    "2.Add an answer to the pool\n" +
                    "3.Add a question to the pool\n" +
                    "4.Delete a question from the pool\n" +
                    "5.Delete an answer from the pool\n"+
                    "0.BACK TO MAIN MENU\n" +
                    "Enter your choice\n");

            choice = readInRange(0, 5, sc);
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
                case 5:
                    deleteAnswerFromPool(subjectName, sc, connection);
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
        String strA = checkString(sc);
        if (!AnswerText.isAnswerTextInTable(connection, strA)) // if not in table insert to table
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
        System.out.println("Type true if you would like to add an open question.");
        System.out.println("Type false if you would like to add an American question.");
        boolean isOpen = sc.nextBoolean();
        sc.nextLine();

        System.out.println("Enter your new question (string):");
        String strQuestion = checkString(sc);
        if (Question.isQuestionTextInTable(connection, strQuestion, subjectName)) {
            System.out.println("Question is already in the pool, try again");
            return;
        }

        String diff = defineDifficulty(sc, connection);

        if (isOpen) {
            handleOpenQuestion(subjectName, strQuestion, diff, sc, connection);
        } else {
            handleAmericanQuestion(subjectName, strQuestion, diff, sc, connection);
        }
    }

    // Helper function
    private static void handleOpenQuestion(String subject, String strQuestion, String diff, Scanner sc, Connection connection) throws SQLException {
        int choice = 2;
        String answer;

        System.out.println(Pool.answerTextPoolToString(connection, subject));
        if (Pool.getAmountOfAnswersInSubjectPool(connection, subject) > 0)
            choice = getAnswerChoice(sc);

        if (choice == 1) {
            System.out.println(Pool.answerTextPoolToString(connection, subject));
            System.out.println("Please enter the index of the answer for your open question:");
            int solutionIndex = readInRange(0, Pool.getAmountOfAnswersInSubjectPool(connection, subject), sc);
            answer = Pool.getAnswerTextArrayAtIndex(connection, subject, solutionIndex);
        } else {
            System.out.println("Enter your new answer:");
            answer = checkString(sc);
            if (!AnswerText.isAnswerTextInTable(connection, answer))
                if (!AnswerText.InsertToTable(connection, answer)) { // if it already exists in the AnswerText table it's fine(rowsAffected >= 0),
                    // if it was an exception, the user should try again.
                    System.out.println("An error occurred, please try again");
                    return;
                }

            int addAnswerToPoolCheck;
            if (!Pool.isAnswerInSubjectPool(connection, answer, subject)) {
                addAnswerToPoolCheck = Pool.addAnswerTextToPool(answer, subject, connection);
                if (addAnswerToPoolCheck == 1) {
                    System.out.println("Successfully added a new answer to the pool");
                } else { // exception happened, kicked out of the function
                    System.out.println("An error occurred, please try again");
                    return;
                }
            }
        }
        int newId;

        newId = OpenQuestion.InsertToTable(connection, strQuestion, subject, diff, answer);
        if (newId <= 0) { // if it was an exception(==0), the user should try again.
            System.out.println("An error occurred, please try again");
        }
        System.out.println("Successfully added a new open question to the " + subject + " pool.");
    }

    // Helper function
    private static void handleAmericanQuestion(String subject, String strQuestion, String diff, Scanner sc, Connection connection) throws SQLException {
        //Question aq = new AmericanQuestion(strQuestion, diff);
        int newId = AmericanQuestion.InsertToTable(connection, strQuestion, subject, diff), returnValue, ansAmount;
        if (newId <= 0) { // if it was an exception(==0), the user should try again.
            System.out.println("An error occurred, please try again");
            return;
        }

        System.out.println("Enter how many answers do you want the question to have:");
        ansAmount = readInRange(0, 10, sc); // user input


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
        System.out.println("Successfully added a new American question to the " + subject + " pool.");

    }

    public static int addAnswerToAmericanQuestion(String subjectName, int id, Scanner sc, Connection connection) throws SQLException {
        int choice = 2;
        System.out.println(Pool.answerTextPoolToString(connection, subjectName));
        if (Pool.getAmountOfAnswersInSubjectPool(connection, subjectName) > 0)
            choice = getAnswerChoice(sc);

        int checkAddAnswer;
        if (choice == 1)
            checkAddAnswer = addAnswerFromPoolToAmericanQuestion(subjectName, id, sc, connection);
        else
            checkAddAnswer = addNewAnswerToAmericanQuestion(subjectName, id, sc, connection);

        return checkAddAnswer;
    }

    private static int getAnswerChoice(Scanner sc) {
        int choice;
        System.out.println("Is your answer:\n1. From the pool\n2. A new answer");
        choice = readInRange(1, 2, sc);

        return choice;
    }

    private static int addAnswerFromPoolToAmericanQuestion(String subject, int id, Scanner sc, Connection connection) throws SQLException {
        System.out.println(Pool.answerTextPoolToString(connection, subject));
        System.out.println("Enter the answer's index:");
        int ansIndex = readInRange(0, Pool.getAmountOfAnswersInSubjectPool(connection, subject), sc);
        String answerText = Pool.getAnswerTextArrayAtIndex(connection, subject, ansIndex);

        System.out.println("Is the answer true or false (true/false)?");
        boolean isTrue = sc.nextBoolean();

        if (!AmericanQuestion.isAnswerTextInAmericanQuestion(id, answerText, connection))
            return AmericanQuestion.addAnswerToQuestion(answerText, id, isTrue, connection);
        else {
            return 0;
        }
    }

    private static int addNewAnswerToAmericanQuestion(String subject, int qId, Scanner sc, Connection connection) throws SQLException {
        System.out.println("Enter your new answer:");
        String answerText = checkString(sc);

        if (!AnswerText.isAnswerTextInTable(connection, answerText) && !AnswerText.InsertToTable(connection, answerText)) {
            System.out.println("An error occurred, please try again");
            return -1;
        }
        int checkAddAnswer;
        if (!Pool.isAnswerInSubjectPool(connection, answerText, subject)) {
            checkAddAnswer = Pool.addAnswerTextToPool(answerText, subject, connection);
            if (checkAddAnswer == 1) {
                System.out.println("Successfully added a new answer to the pool");
            } else { // exception happened, kicked out of the function
                System.out.println("An error occurred, please try again");
                return -1;
            }
        }

        System.out.println("Is the answer true or false (true/false)?");
        boolean isTrue = sc.nextBoolean();
        sc.nextLine();

        if (!AmericanQuestion.isAnswerTextInAmericanQuestion(qId, answerText, connection)) {
            return AmericanQuestion.addAnswerToQuestion(answerText, qId, isTrue, connection);
        } else {
            return 0;
        }
    }

    public static void printPlusDeleteQuestionFromArray(String subjectName, Connection connection, Scanner sc) throws SQLException {
        System.out.println(Pool.questionPoolToString(subjectName, connection));
        int amountOfQuestion = Pool.getAmountOfQuestionsInSubjectPool(connection, subjectName);
        if (amountOfQuestion <= 0) {
            System.out.println("Try adding a question\n");
            return;
        }
        System.out.println("Enter the index of the question you want to delete");
        int index = readInRange(0, amountOfQuestion, sc);

        if (Pool.deleteQuestionFromArray(index, subjectName, connection))
            System.out.println("Successfully deleted question number-" + index);
        else
            System.out.println("Failed to delete question from array, try with a different index");
    }

    private static void deleteAnswerFromPool(String subjectName, Scanner sc, Connection connection) throws SQLException {
        System.out.println(Pool.answerTextPoolToString(connection, subjectName));
        int amountOfQuestion = Pool.getAmountOfAnswersInSubjectPool(connection, subjectName);
        if (amountOfQuestion <= 0) {
            System.out.println("Try adding a question\n");
            return;
        }
        System.out.println("Enter the index of the question you want to delete");
        int index = readInRange(1, amountOfQuestion, sc);

        if (Pool.deleteAnswerByIndexFromPool(connection, subjectName, index))
            System.out.println("Successfully deleted question number-" + index);
        else
            System.out.println("Failed to delete question from array, try with a different index");
    }

    public static String defineDifficulty(Scanner sc, Connection connection) throws SQLException {
        String options[] = Question.printDifficulty(connection);
        System.out.println(options[options.length - 1]);
        int choice = readInRange(0, options.length - 1, sc);
        return options[choice];
    }

    private static void printAllDataBase(Connection connection) {
        System.out.println(AnswerText.getAnswerTextTable(connection));
        System.out.println(Question.getQuestionTable(connection));
        System.out.println(Pool.getPoolTable(connection));
        System.out.println(Test.getTestTable(connection));
    }

    public static <T extends Number> T readInRange(T min, T max, Scanner scanner) {

        T value = null;
        boolean finished = false;
        while (!finished) {
            try {
                if (min instanceof Integer) {
                    value = (T) Integer.valueOf(scanner.nextInt());
                } else if (min instanceof Double) {
                    value = (T) Double.valueOf(scanner.nextDouble());
                } else if (min instanceof Long) {
                    value = (T) Long.valueOf(scanner.nextLong());
                } else {
                    System.out.println("I don't know what type of number this is...");
                }

                if (min.doubleValue() <= value.doubleValue() && value.doubleValue() <= max.doubleValue()) {
                    finished = true;
                } else {
                    System.out.println("Please enter a valid number: ");
                    scanner.nextLine();
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number: ");
                scanner.nextLine();
            }

        }
        scanner.nextLine();
        return value;
    }

    public static String checkString(Scanner scanner) {
        String text = null;

        text = scanner.nextLine();

        while (text == null || text.isEmpty()) {
            System.out.println("Please enter a valid string: ");
            text = scanner.nextLine();
        }
        return text;
    }


}