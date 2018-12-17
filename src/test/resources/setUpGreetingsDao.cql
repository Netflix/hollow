CREATE TABLE greetings(
    userEmail TEXT,
    firstName TEXT,
    message TEXT,
    PRIMARY KEY(userEmail));

INSERT INTO greetings(userEmail, firstName, message) values('yodle@yodle.net','Yodle', 'Yo');
