
# backend  - java springboot


# Implementat:
- <b>Modele</b>: bikes_type.java: clasa care reprezinta tipul bicicletelor, bikes.java: clasa care reprezinta bicicletele existente, cu tipul acestora si locatia lor; Checkoutpayment.java - model pentru checkout, va fi folosit cand un user va vrea sa rezerve o bicicleta de pe harta,
fiind trimis id-ul bicicletei

- <b>Repozitorii</b>: bikes_typeRepository si bikesRepository, la fel ca la modele
- <b>Servicii</b>: bikes_typeService si bikesService, serviciile pentru adaugarea/modificarea/obtinerea detaliilor tipurilor de biciclete, respectiv bicicletelor
  
- <b>Controllers</b>: la fel ca pana acum, si <b>StripeController</b> pentru a procesa o tranzactie. Atunci cand un user apasa butonul de rezervare pentru o bicicleta, id ul acesteia este trimis, iar mai apoi prin serviciul <b>bikesService</b> se obtin tipul si pretul bicicletei.

# TODO:
- User cu model, repo, serviciu si controller
- Login/signup page 
- Session
- Adaugare securitate






