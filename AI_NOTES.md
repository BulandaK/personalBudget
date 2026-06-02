w tym pliku zawieram prompty oraz rzeczy do których znacząco wykorzystałem nardzędzia AI, 


https://gemini.google.com/share/c99e08915dde
link do mojej rozmowy z chatem 

TLDR;
jak zaprojektowac rozwiazanie z budzetem na daną kategorie; co może się znaleźć, co powinno w którym dto;czy zrobić osobny controller do danego rozwiazania

myślę, że w zdecydowanej większości moje obecne rozwiązanie dotyczące noty ostrzegawczej o przekroczeniu budżetu na daną kategorie
(które jest tak naprawdę boolean'em do dto transakcji) jest zrobione przy konsultacji z gemini 3.5 flash, co nie oznacza że go nie rozumiem.

przyznaje także że wspomagałem się przy budowaniu customowych query w repozytoriach

również zapytałem jak zapewnić odpowiednią izolacje w transakcjach co wydawało mi się ważę w temacie api do zarządania budżetem
 ```@Lock(LockModeType.PESSIMISTIC_WRITE)``` 