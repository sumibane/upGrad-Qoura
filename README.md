# upGrad-Qoura
Trello Quora assignment solutions

# External Configurations
* Download the PostgreSQL driver and add to project dependencies
  * Under File select Project Structure
  * Under Dependencies Tab, click + and select JARs and Repositories
  * Locate the Jar File downloaded initially
  * Goto localhost.properties in DB folder and add to main path by clicking ALT+ENTER

# DB configuration
DB Name: qoura

Configure Password in:
  1. quora-api/src/main/resources/application.yaml
  2. quora-db/src/main/resources/config/localhost.properties

# NO NO
1. Do Not add any additonal library for commit
2. Do Not add the Target folders in commit

# Upgrades made in Version
1. Spring Boot - V 2.3.2.Release
2. Database Platform - PostgreSQL10Dialect
