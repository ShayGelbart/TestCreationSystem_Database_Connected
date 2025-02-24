# Test Creation and Management System
## Submitters
-Shay Gelbart 
-Arina Kuprina

## Overview

The Test Creation and Management System is a Java-based application designed to manage subjects, question pools, and create tests. It offers users the ability to create and manage pools of questions, generate tests either automatically or manually, and handle multiple-choice and open-ended questions. The application provides a menu-driven interface, allowing users to interact with the system through simple console commands.

## Features

### 1. Main Menu
- **Create a Test:** Users can create a test by selecting a subject and determining the number of questions. The system supports both automatic and manual test creation.
- **Manage Pools:** Users can create new pools, delete existing ones, or alter existing pools by adding or removing questions and answers.
-**Print All essential information from Database:** Users can print Answers Tables, Question Table, Pool Table, Test Table . 
- **Exit:** Users can exit the program, with all data being saved to a binary file for future use.

### 2. Test Creation
- **Automatic Test Creation:** Automatically selects questions from the pool to generate a test.
- **Manual Test Creation:** Allows users to manually select questions and answers from the pool.

### 3. Pool Management
- **Create New Pool:** Users can define a new subject and add questions and answers.
- **Delete Pool:** Users can delete an existing pool by selecting the corresponding subject.
- **Alter Pool:** Users can view, add, or delete questions and answers within an existing pool.
Users can delete only answers that are related to American questions.

### 4. Question Types
- **Open-Ended Questions:** Questions that require a text-based answer.
- **American Questions:** Questions that present multiple answers, with none, one or more being correct.

### 5. Difficulty Levels
- Questions can be categorized based on difficulty: Easy, Medium, or Hard.

###Data Persistence
-PostgreSQL Database:
The program stores and manages all data using a PostgreSQL database. This ensures that all             subjects, question pools, tests, and related data are securely saved and can be easily retrieved. The database structure supports efficient data management, scalability, and security. When the program starts, it connects to the PostgreSQL database to load the previously saved data, ensuring continuity across sessions.

## Usage

### Creating a Test
1. Choose the option to create a test from the main menu.
2. Select a subject from the list of available subjects.
3. Enter the number of questions for the test (between 1 and 10).
4. Choose whether to create the test automatically or manually.
5. If manual, select the specific questions and answers to include in the test.
6. Upon successful creation, the test details and its solution are displayed within a text file with the date and time of creation.

### Managing Pools
1. Choose the option to manage pools from the main menu.
2. Select from creating a new pool, deleting a pool, or altering an existing pool.
3. Follow the prompts to add or remove questions and answers, or to view the current pool.



## Exception Handling
- **AmountOfQuestionsException:** Thrown when the user attempts to create a test with an invalid number of questions.
- **LessThanThreeAnswersException:** Thrown when attempting to create a multiple-choice question with less than three possible answers.


1. Pool
•	Description: The Pool entity represents the collection of subjects available in the system. Each subject is uniquely identified by subjectName.
•	Attributes:
o	subjectName: The name of the subject. This is the primary key.
•	Relationships:
o	One-to-Many with Question: Each subject in Pool can have multiple associated questions, but each question is linked to one subject. This is represented by the subjectName in the Question table, which is a foreign key referencing Pool(subjectName).
o	One-to-Many with AnswersPool: Each subject can have multiple associated answers in the AnswersPool, where the subjectName in the AnswersPool table references Pool(subjectName).
o	One-to-Many with Test: Each subject can have multiple associated tests, where the subjectName in the Test table references Pool(subjectName).
2. Question
•	Description: The Question entity represents the questions available in the system. Each question is uniquely identified by questionId and is associated with a specific subject and difficulty level.
•	Attributes:
o	questionId: A unique identifier for each question.
o	questionText: The text of the question.
o	subjectName: The subject to which the question belongs, linked to Pool.
o	difficulty: The difficulty level of the question, which could be one of Easy, Medium, or Hard.
•	Relationships:
o	Many-to-One with Pool: Each question is associated with one subject from the Pool.
o	One-to-One with OpenQuestion: An OpenQuestion is a subtype of Question and shares the same questionId, creating a one-to-one relationship.
o	One-to-One with AmericanQuestion: An AmericanQuestion is another subtype of Question, with a similar one-to-one relationship.
o	Many-to-One with TestQuestions: Multiple TestQuestions can reference a single Question, forming a many-to-one relationship.
3. OpenQuestion
•	Description: The OpenQuestion entity represents open-ended questions in the system. It is a specific type of Question with an optional school solution.
•	Attributes:
o	questionId: A unique identifier for the open question, linked to Question.
o	schoolSolution: The suggested answer or solution for the open-ended question, which references AnswerText.
•	Relationships:
o	One-to-One with Question: Each OpenQuestion is associated with exactly one Question.
o	Many-to-One with AnswerText: Each OpenQuestion can have a schoolSolution that references an answer in the AnswerText entity.
4. AmericanQuestion
•	Description: The AmericanQuestion entity represents multiple-choice questions in the system. It is another specific type of Question.
•	Attributes:
o	questionId: A unique identifier for the American question, linked to Question.
•	Relationships:
o	One-to-One with Question: Each AmericanQuestion is associated with exactly one Question.
o	One-to-Many with QuestionAnswer: Each AmericanQuestion can have multiple possible answers, represented by QuestionAnswer.
5. Test
•	Description: The Test entity represents a test or exam in the system. Each test is associated with a specific subject and contains multiple questions.
•	Attributes:
o	testId: A unique identifier for the test.
o	subjectName: The subject of the test, linked to Pool.
•	Relationships:
o	Many-to-One with Pool: Each test is associated with one subject from the Pool.
o	One-to-Many with TestQuestions: Each Test can include multiple questions, as defined in TestQuestions.
o	One-to-Many with TestQuestionAnswer: Each test can have multiple answers for the questions it contains, as represented in TestQuestionAnswer.
Relationships Summary:
•	Pool-Question: One Pool can have many Questions, but each Question belongs to one Pool.
•	Question-OpenQuestion: One Question can be an OpenQuestion, creating a one-to-one relationship.
•	Question-AmericanQuestion: One Question can be an AmericanQuestion, creating a one-to-one relationship.
•	Test-Question: One Test can have many Questions, and one Question can appear in many Tests.
•	TestQuestion-AnswerText: One TestQuestion can have many answers (AnswerText), each of which is associated with a question.


















AnswerText Table
Attribute	Data Type	Primary Key	Constraints
answerText	VARCHAR(255)	Yes	NOT NULL

Pool Table
Attribute	Data Type	Primary Key	Constraints
subjectName	VARCHAR(255)	Yes	NOT NULL

Question Table
Attribute	Data Type	Primary Key	Constraints
questionId	SERIAL	Yes	PRIMARY KEY
questionText	VARCHAR(255)	No	NOT NULL, UNIQUE (questionText, subjectName)
subjectName	VARCHAR(255)	No	NOT NULL, UNIQUE, FOREIGN KEY (references Pool(subjectName)) ON DELETE CASCADE
difficulty	ENUM ('Easy', 'Medium', 'Hard')	No	NOT NULL

OpenQuestion Table
Attribute	Data Type	Primary Key	Constraints
questionId	INT	Yes	PRIMARY KEY, FOREIGN KEY (references Question(questionId)) ON DELETE CASCADE
schoolSolution	VARCHAR(255)	No	FOREIGN KEY (references AnswersPool(answerText))



AmericanQuestion Table
Attribute	Data Type	Primary Key	Constraints
questionId	INT	Yes	PRIMARY KEY, FOREIGN KEY (references Question(questionId)) ON DELETE CASCADE

QuestionAnswer Table
Attribute	Data Type	Primary Key	Constraints
questionId	INT	Yes	PRIMARY KEY (questionId, answerText), FOREIGN KEY (references AmericanQuestion(questionId)) ON DELETE CASCADE
answerText	VARCHAR(255)	Yes	PRIMARY KEY (questionId, answerText), FOREIGN KEY (references AnswersPool(answerText)) ON DELETE CASCADE
trueness	BOOLEAN	No	
AnswersPool Table
Attribute	Data Type	Primary Key	Constraints
answerText	VARCHAR(255)	Yes	NOT NULL, PRIMARY KEY 
subjectName	VARCHAR(255)	Yes	NOT NULL, PRIMARY KEE, FOREIGN KEY (references Pool(subjectName)) ON DELETE CASCADE

Test Table
Attribute	Data Type	Primary Key	Constraints
testId	SERIAL	Yes	PRIMARY KEY
subjectName	VARCHAR(255)	No	NOT NULL, FOREIGN KEY (references Pool(subjectName)) ON DELETE CASCADE
TestQuestions Table
Attribute	Data Type	Primary Key	Constraints
testId	INT	Yes	NOT NULL, PRIMARY KEY, FOREIGN KEY (references Test(testId)) ON DELETE CASCADE
questionId	INT	Yes	NOT NULL, PRIMARY , FOREIGN KEY (references Question(questionId)) ON DELETE CASCADE
isAmerican	BOOLEAN	No	NOT NULL
TestQuestionAnswer Table
Attribute	Data Type	Primary Key	Constraints
testId	INT	Yes	PRIMARY KEY (testId, questionId, answerText), FOREIGN KEY (references Test(testId)) ON DELETE CASCADE
questionId	INT	Yes	PRIMARY KEY (testId, questionId, answerText), FOREIGN KEY (references Question(questionId)) ON DELETE CASCADE
answerText	VARCHAR(255)	Yes	PRIMARY KEY (testId, questionId, answerText), FOREIGN KEY (references AnswerText(answerText)) ON DELETE CASCADE
trueness	BOOLEAN	No	


