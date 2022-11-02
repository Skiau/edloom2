# edloom2

This is a demo for a distributed application inspired by Moodle, not intended for commercial use. 

Source code attempts to connect to {url: "jdbc:mysql://127.0.0.1:3306/userdb", user: "root", password: ""}, changing the parameters can only be done manually, look at lines marked with //TODO.

If you have a firewall active, it will most likely prevent it for running. This happens because of the RMI implementation.

In order for the application to run as intended, please create a new MySQL connection, then copy & paste the code in the COPYME file in this repository.

You will be able to log into the admin account you created in the database, or create a new non-admin account.
The other account in the database is only there to showcase a banned account. The user interface does not provide a way to ban/unban accounts, you have to manually change the database entry under the table column named "banned".  Analogous for promoting/demoting admins.


Some features of this project:
- RMI for client-server communication
- GUI done with Java Swing & AWT
- MySQL database for user data
- Serialization using GSON
- Java mail for sending emails (with Mailtrap)
- Guava for writing bytestream into a file 
- Email validation with Regex
- Multithreading
- Exceptions treated
- Unit Testing
- Documentation 

What it is lacking is a well-defined architecture. This is partly because I started coding without a pre-determined UML diagram or any kind of blueprint. It was originally supposesd to be a MVC. Final result is unpolished.
