# RPG 119474 — Wasteland (duelli a turni)

Progetto per il corso Metodologie di Programmazione (UNICAM, A.A. 2025/26).

Gioco di ruolo in Java ambientato in un mondo post-apocalittico: si crea un
sopravvissuto, lo si equipaggia con il bottino e si affrontano i nemici in
duelli a turni.

Il codice è diviso in strati (core → engine → persistence → ui) per tenere
separati dominio, logica di gioco, salvataggi e interfaccia.

## Requisiti
- JDK 21

## Compilare ed eseguire
    ./gradlew build
    ./gradlew run

Se mancano `gradlew`/`gradle/wrapper`, generali una volta con:
`gradle wrapper --gradle-version 8.7`

## Uso di strumenti di AI
Durante lo sviluppo ho usato l'assistente AI Claude (Anthropic) come supporto
alla scrittura del codice e della documentazione. Le scelte di progetto sono
mie e ho rivisto e compreso il codice. La descrizione dettagliata è nella Wiki.
