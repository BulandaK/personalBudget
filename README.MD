**Projekt do zarządzania budżetem**

Wydaję mi się że są spełnione kryteria oceny
natomiast jesli nie to proszę o notę.
Zrobiłem osobnego brancha aby zrobić pull requesta na którym Państwo możecie nanieść poprawki odnośnie kodu,
byłbym wdzięczny za code review.

Przepraszam też z góry za brak conventional commits.

Z wstępu chciałem dodać jeszcze, że dodany został plik AI_NOTES.md w którym zamieściłem 
zagadnienia przy których wspierałem się narzędziami AI. To co nie znalazło się w AI_NOTES zrobiłem ręcznie 
lub powieliłem z moich poprzednich projektów.  

z wymagań technicznych podsyłam tu screena z spring initalizera
![springIntializr.png](springIntializr.png)
reszta rzeczy znajduję się oczywiście w pom.xml

baza danych to PostgreSQL, chociaz moża aplikacje odpalić bez kontenera, lokalnie na H2
![databaseERD.png](databaseERD.png)

Testy są zarówno jednostkowe i integracyjne

JAK ODPALIC APLIKACJE
1. robimy  ```git clone https://github.com/BulandaK/personalBudget.git``` następnie ```cd personalBudget```
2. robimy ```git fetch origin``` później checkout na brancha feat/account ```git switch feat/account```
3. nastepnie w zależności od systemu operacyjnego robimy:
    - macos/linux : ```./mvnw clean install```
    - windows: ```.\mvnw clean install```
4. po zbudowaniu artefaktu, uruchamiamy ```docker compose up --build```
5. następnie wchodzimy do naszej przeglądarki i wpisujemy ```http://localhost:80```
6. powinien nam sie ukazac vibekodzony prosty tester do endpointow, 
w bazie danych dodałem kilka testowych rekordów ale najlepiej przetestować samemu działanie
7. nasze api powinno działać pod adresem ```http://localhost:8080/api/v1/``` 

dodany został swagger / openAPI można go sprawdzić ```http://localhost:8080/swagger-ui/index.html#/```
dodałem też folder bruno [bruno](bruno) gdzie znajdują się gotowe yamle z zapytaniami wystarczy je sobie zaimportować, jesli GUI nie będzie działać


