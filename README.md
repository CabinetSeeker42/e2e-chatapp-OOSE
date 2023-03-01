# e2e-chatapp-OOSE
Fully end-to-end encrypted chat application, created at the end of the OOSE-semester. This was a project in which I had to work with five other students. Documentation with UML-standards, frontend in VueJS, backend in Java (SpringBoot) in combination with Hibernate (MySQL) for database. Marked by teachers with a 9.2/10.

## 1. Frontend
Voor het runnen van de frontend op een lokale machine moet eerst de bovenstaande zip gedownload en uitgepakt worden.

### 1.1. NodeJS
Voor het lokaal runnen van de frontend heeft de gebruiker NodeJS nodig. Wij gaan ervan uit dat de developer dit al op zijn device heeft staan. Zo niet dan zou hij deze bijvoorbeeld via een tutorial kunnen installeren.

### 1.2. Yarn
Bij het developen van de frontend is yarn gebruikt. Deze is op de volgende manier te installeren:

Ga naar de terminal van het project.
Vul het volgende in de terminal in.
```
npm install --global yarn
```

Installeer de dependencies met yarn.
```
yarn install
```

 Als dit klaar is, kan de frontend gerund worden:

```
yarn start
```
### 1.3. E2E-testen
Alle E2E-testen zijn te vinden in de directory cypress>e2e>EndtoEnd.cy.js. Voordat de testen gedraaid kunnen worden, moet de backend wel volledig draaien. De testen kunnen allemaal uitgevoerd worden met de volgende line in de terminal:

```
yarn test:e2e:dev
```

## 2. Belangrijke informatie
In dit hoofdstuk staat andere belangrijke technische informatie die handig is bij de verdere ontwikkeling van de ChatOffice.

### 2.1. Afhankelijkheden
#### 2.1.1. Backend
Alle afhankelijkheden zijn gemakkelijk te vinden in de file 'pom.xml'. Hieronder volgt een kort rijtje met alle dependencies en plug-ins.

##### 2.1.1.1. Code
```
org.springframework.boot (spring-boot-starter-web & spring-boot-starter-websocket, spring-boot-starter-tomcat);
javax.websocket (javax.websocket-api, 1.1)
javax.servlet (jstl)
com.google.code.gson (gson, 2.8.9)
com.google.guava (guava, 31.0.1-jre)
org.projectlombok (lombok)
org.apache.tomcat.embed (tomcat-embed-jasper)
mysql (mysql-connector-java, 8.0.31)
com.mysql (mysql-connector-j)
org.hibernate.orm (hibernate-core, 6.0.0.Alpha8)
4.1.1.2. Testen
org.springframework.boot (spring-boot-starter-test)
junit (junit, 4.13.2)
org.junit.vintage (junit-vintage-engine)
exclusion: org.hamcrest (hamcrest-core)
org.mockito (mockito-inline, 2.13.0)
org.jacoco (jacoco-maven-plugin, 0.8.8)
com.h2database (h2)
```
#### 2.1.2. Frontend
In de frontend wordt er gebruik gemaakt van het JavaScript framework Vue. Voor alle icoontjes wordt er gebruik gemaakt van Font Awesome (Fort Awesome). Om gegevens op te kunnen vragen over de huidige datum en tijd, wordt er gebruik gemaakt van 'dayjs'.

Voor het encrypten en decrypten van alle data worden ook een aantal imports gebruikt. Dit zijn:
```
'big-integer'
'js-sha512'
'crypto-js'
```
De big-integer import wordt gebruikt omdat er bij onze encryptie en decryptie wordt gerekend met erg grote getallen. Deze getallen passen niet binnen de bestaande waarden binnen JavaScript, maar wel in een big-integer.

### 2.3. JWT-token
De registratiepagina zal bij het integreren met de BackOffice haar nut verliezen. Via de registratiepagina worden nu namelijk alle gegevens verzameld waarmee een JWT-token gecreëerd kan worden. Zodra de ChatOffice zich binnen de BackOffice bevindt, kan de token die binnen de BackOffice gebruikt wordt ook als authenticatie binnen de ChatOffice fungeren. De JWT-token wordt al wel door de chatservice ontcijferd.

Binnen de JWT-token, die wordt aangemaakt na het registreren, bevindt zich een companyID. Dit ID zorgt ervoor dat de chatservice kan bepalen met wie de chatter mag chatten. Er staat nog geen companyID in de JWT-token die binnen de BackOffice gebruikt wordt, dus dit zal binnen de token toegevoegd moeten worden.
