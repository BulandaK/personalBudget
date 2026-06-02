**Projekt do zarządzania budżetem**

Wydaję mi się że są spełnione kryteria oceny
natomiast jesli nie to proszę o notę

z wymagań technicznych podsyłam tu screena z spring initalizera
![springIntializr.png](springIntializr.png)
reszta rzeczy znajduję się oczywiście w pom.xml

baza danych to PostgreSQL, chociaz moża aplikacje odpalić lokalnie na H2
![databaseERD.png](databaseERD.png)

Testy są zarówno jednostkowe i integracyjne

JAK ODPALIC APLIKACJE
1. W intelij -> New -> Project from version control -> https://github.com/BulandaK/personalBudget.git
2. robimy checkout na brancha feat/account
3. nastepnie wchodzimy w maven -> mvn clean install 
4. po zbudowaniu artefaktu, wchodzimy w terminal do naszego projektu i uruchamiamy ```docker compose up --build```
5. następnie wchodzimy do naszej przeglądarki i wpisujemy ```http://localhost:80```
6. powinien nam sie ukazac vibekodzony prosty tester do endpointow, 
w bazie danych dodałem kilka testowych rekordów ale najlepiej przetestować samemu działanie
7. nasze api powinno działać pod adresem ```http://localhost:8080/api/v1/``` 

dodany został swagger / openAPI można go sprawdzić ```http://localhost:8080/swagger-ui/index.html#/```
dodałem też folder bruno [bruno](bruno) gdzie znajdują się gotowe yamle z zapytaniami wystarczy je sobie zaimportować, jesli GUI nie będzie działać


