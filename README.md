# voting_system
college project
# voting_system
collage project


🗳️ Voting System - Java Swing + PostgreSQL
This is a simple Java Swing-based Voting System project with a PostgreSQL backend. It includes admin and voter login, vote submission per section (A or B), and vote viewing by the admin.




📁 Features
Voter Login (Section A and B)

Admin Login

Vote Casting (one per voter)

Admin can view vote counts

Admin can register new voters





🛠️ Technologies Used
Java (Swing GUI)

PostgreSQL

JDBC (PostgreSQL JDBC Driver)






🚀 How to Run the Project
1. 📦 Prerequisites
Java JDK installed

PostgreSQL installed

postgresql-42.7.5.jar JDBC driver (place it in the project folder)

2. 🛠️ Set Up PostgreSQL Database
Create a database:
votingsystem






>Create necessary tables:

sql
CREATE TABLE voters (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    section CHAR(1) CHECK (section IN ('A', 'B')),
    has_voted BOOLEAN DEFAULT FALSE
);

CREATE TABLE votes (
    id SERIAL PRIMARY KEY,
    section CHAR(1),
    candidate_name VARCHAR(100)
);

CREATE TABLE admin (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);
Insert sample admin:

sql
INSERT INTO admin (username, password) VALUES ('admin', 'admin123');





3. 🧑‍💻 Compile Java Files
In PowerShell or terminal (from project folder):
javac -cp ".;.\postgresql-42.7.5.jar" *.java


4. ▶️ Run the Application

java -cp ".;.\postgresql-42.7.5.jar" LoginPage
