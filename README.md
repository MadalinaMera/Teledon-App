# Teledon - Java-based Client-Server Application

## Descriere generală
Această aplicație Java client-server este dezvoltată pentru a sprijini organizarea unui teledon caritabil. În cadrul evenimentului, voluntarii înregistrează donațiile efectuate telefonic. Sistemul facilitează autentificarea voluntarilor, gestiunea donațiilor și actualizarea în timp real a sumelor donate pentru fiecare caz caritabil.

## Tehnologii utilizate
* Java
* JavaFX – Interfața grafică (Client desktop)
* REST Services
* JSON – Formatul de schimb de date
* Gradle – Managementul dependențelor

## Funcționalități principale
* Login
  * Voluntarii se autentifică cu username și parolă.
  * După autentificare, se deschide o fereastră cu lista cazurilor caritabile și suma totală donată pentru fiecare.
* Înregistrare donație
  * Se alege un caz caritabil.
  * Se introduc datele donatorului: nume, adresă, telefon, sumă donată.
  * După salvare, lista cazurilor și sumele donate sunt actualizate în timp real pentru toți voluntarii.
* Căutare donator
  * Donatorii pot dona pentru mai multe cazuri.
  * Voluntarii pot căuta donatori după o parte din nume.
  * Rezultatele sunt afișate într-o listă, iar selectarea unui donator autocompletează automat câmpurile relevante.
* Logout
  * Voluntarii se pot deloga din aplicație în orice moment.

## Web Client
Proiectul implementează și servicii REST. Un client web care utilizează aceste servicii poate fi găsit aici: [Teledon-Web-Client](https://github.com/MadalinaMera/Teledon-Web-Client.git)
