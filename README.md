# CashFLO-PRO
Simple banking app

Currently there only exists a table of bank accounts with unique keys working as ids and pins as well as balance.
A user with a bank account can deposit, withdraw and transfer to another user.

<h2>Future developments</h2>
<ul>
<li>Hash and sand pins;</li>
<li>OAuth2;</li>
<li>Login, logout, sign-up;</li>
<li>Frontend.</li>
</ul>

<h2>Dependencies</h2>
<p>Project was created using the Intellij IDEA community IDE, using the Maven automation tool, set up with a local MariaDB database.
Other dependencies are noted in the pom file.</p>
<p>Backend is Spring Boot.</p>
<p>Frontend...</p>

<h2>To start project</h2>
<p>Set up database as noted in application properties.</p>
<p>Run using java 21 SDK.</p>
<p>Worth a note (Windows users) to make sure that JAVA_HOME environment variable is not outdated</p>
to build project:

```
mvn wrapper:wrapper
```
