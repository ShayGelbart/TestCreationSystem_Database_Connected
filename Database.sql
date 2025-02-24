CREATE TABLE AnswerText (
	answerText varchar(255)	PRIMARY KEY NOT NULL
);

--CREATE TYPE difficulty AS ENUM ('Easy', 'Medium', 'Hard');

CREATE TABLE Pool (
	subjectName VARCHAR(255) PRIMARY KEY NOT NULL
);

CREATE TABLE Question (
    questionId SERIAL PRIMARY KEY,
    questionText VARCHAR(255) NOT NULL,
	subjectName VARCHAR(255) NOT NULL,
    difficulty difficulty NOT NULL,
	CONSTRAINT Dup_Question UNIQUE (questionText, subjectName),
	FOREIGN KEY (subjectName) REFERENCES Pool(subjectName) ON DELETE CASCADE
);

CREATE TABLE OpenQuestion (
	questionId INT PRIMARY KEY,
    schoolSolution VARCHAR(255),
	FOREIGN KEY (questionId) REFERENCES Question(questionId) ON DELETE CASCADE
);


CREATE TABLE AmericanQuestion (
	questionId INT PRIMARY KEY,
	FOREIGN KEY (questionId) REFERENCES Question(questionId) ON DELETE CASCADE
);

CREATE TABLE QuestionAnswer (
    questionId INT,
    answerText VARCHAR(255),
	trueness BOOLEAN,
    PRIMARY KEY (questionId, answerText),
    FOREIGN KEY (questionId) REFERENCES AmericanQuestion(questionId) ON DELETE CASCADE,
    FOREIGN KEY (answerText) REFERENCES AnswerText(answerText) ON DELETE CASCADE
);

CREATE TABLE AnswersPool (
	answerText VARCHAR(255) NOT NULL,
	subjectName VARCHAR(255) NOT NULL,
	PRIMARY KEY(answerText, subjectName),
	FOREIGN KEY (subjectName) REFERENCES Pool(subjectName) ON DELETE CASCADE
);

CREATE TABLE Test (
    testId SERIAL PRIMARY KEY,
    subjectName VARCHAR(255) NOT NULL,
	FOREIGN KEY (subjectName) REFERENCES Pool(subjectName) ON DELETE CASCADE
);

CREATE TABLE TestQuestions (
    testId INT NOT NULL,
    questionId INT NOT NULL,
	isAmerican BOOLEAN NOT NULL,
	FOREIGN KEY (testId) REFERENCES Test(testId) ON DELETE CASCADE,
    FOREIGN KEY (questionId) REFERENCES Question(questionId) ON DELETE CASCADE,
    PRIMARY KEY (testId, questionId)
);

CREATE TABLE TestQuestionAnswer (
	testId INT,
	questionId INT,
    answerText VARCHAR(255),
	trueness BOOLEAN,
	PRIMARY KEY (testId, questionId, answerText),
	FOREIGN KEY (testId) REFERENCES Test(testId) ON DELETE CASCADE,
    FOREIGN KEY (questionId) REFERENCES Question(questionId) ON DELETE CASCADE,
    FOREIGN KEY (answerText) REFERENCES AnswerText(answerText) ON DELETE CASCADE
);

SELECT * FROM TestQuestionAnswer