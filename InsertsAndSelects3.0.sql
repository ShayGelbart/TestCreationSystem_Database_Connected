-- Inserts into Pool
INSERT INTO Pool (subjectName) VALUES ('Math'), ('History'), ('Science');

-- Inserts into AnswerText
INSERT INTO AnswerText (answerText) VALUES ('True'), ('False'), ('Yes'), ('No'),('Jupiter'), ('Earth'), ('4');


-- Insert into Question for OpenQuestion
INSERT INTO Question (questionText, subjectName, difficulty) VALUES ('What is 2+2?', 'Math', 'Easy');

INSERT INTO OpenQuestion (questionId, schoolSolution) VALUES (1, '4');

-- Insert into Question for AmericanQuestion
INSERT INTO Question (questionText, subjectName, difficulty) VALUES ('Which is the largest planet?', 'Science', 'Medium');

INSERT INTO AmericanQuestion (questionId) VALUES (2);

-- Insert into AnswersPool
INSERT INTO AnswersPool (answerText, subjectName) VALUES ('True', 'Math'), ('False', 'Math'), ('4', 'Math'), ('4', 'Science'), ('True', 'Science'),
('Jupiter', 'Science'), ('Earth', 'Science');


-- Insert into QuestionAnswer for AmericanQuestion
INSERT INTO QuestionAnswer (questionId, answerText, trueness) VALUES (2, 'Jupiter', true), (2, 'Earth', false), (2, '4', false), (2, 'True', false);


--Insert into Test
--INSERT INTO Test (subjectName) VALUES ('Math');

--Insert into TestQuestions
--INSERT INTO TestQuestions (testId, questionId, isAmerican) VALUES (1, 1, FALSE), (1, 2, TRUE);


--INSERT INTO TestQuestionAnswer VALUES (1, '4', TRUE), (2, 'Jupiter', TRUE), (2, 'Earth', FALSE);
-- Select queries to check the data


-- -- Check all Question entries
--SELECT * FROM Question;

-- -- Check all OpenQuestion entries
-- SELECT * FROM OpenQuestion;

-- -- Check all AmericanQuestion entries
-- SELECT * FROM AmericanQuestion;

-- -- Check if QuestionAnswer entries exist
-- SELECT * FROM QuestionAnswer;

-- -- Check AnswerText pool
-- SELECT * FROM AnswerText;

-- -- Check AnswersPool
-- SELECT * FROM AnswersPool;

-- SELECT * FROM Pool

-- -- Check Test and TestQuestions
-- SELECT * FROM Test;
-- SELECT * FROM TestQuestions;