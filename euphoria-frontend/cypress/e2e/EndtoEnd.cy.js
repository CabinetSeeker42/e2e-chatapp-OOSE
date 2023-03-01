const generateRandomNumber = (min, max) => Math.random() * (max - min) + min;
const userID = generateRandomNumber(10000, 9999999999).toFixed(0);
const userID2 = generateRandomNumber(10000, 9999999999).toFixed(0);
const userID3 = generateRandomNumber(10000, 9999999999).toFixed(0);
const userName = "Jan Jansen";
const userName2 = "Piet Pietersen";
const userName3 = "Support user";
const password = "W@chtwoord1234";
const wrongPassword = "Wachtwoord1234";
const companyID = generateRandomNumber(0, 10).toFixed(0);
const textMessage = "Tekst bericht";

context("Window", () => {
  beforeEach(() => {
    cy.visit("http://localhost:4173");
  });

  describe("Register screen", () => {
    beforeEach(() => {
      cy.get("#goToRegister").click();
    });

    it("Happy flow register test - Check if the user can register.", () => {
      cy.get("#register");
      cy.get(".register-input-wrapper")
        .find("[placeholder='Voornaam, Achternaam']")
        .type(userName);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Gebruiker ID']")
        .type(userID);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Bedrijf ID']")
        .type(companyID);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord']")
        .type(password);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord herhalen']")
        .type(password);
      cy.get("#register-button").click();

      cy.get(".grid-container");
    });

    it("Register user with support test - Check if the user can register with a support.", () => {
      cy.get("h1").contains("Registreren");
      cy.get(".register-input-wrapper")
          .find("[placeholder='Voornaam, Achternaam']")
          .type(userName3);
      cy.get(".register-input-wrapper")
          .find("[placeholder='Gebruiker ID']")
          .type(userID3);
      cy.get(".register-input-wrapper")
          .find("[placeholder='Bedrijf ID']")
          .type(companyID);
      cy.get(".register-input-wrapper")
          .find("[placeholder='Wachtwoord']")
          .type(password);
      cy.get(".register-input-wrapper")
          .find("[placeholder='Wachtwoord herhalen']")
          .type(password);
      cy.get(".register-input-wrapper")
        .find("[name='isSupport']")
        .click();
      cy.get(".company-list")
          .find("[type='checkbox']")
          .click({multiple: true});

      cy.get(".register-button-wrapper").find("#register-button").click({force: true});

      cy.get(".grid-container");
    });

    it("Submit with missing fields register test - Check if the user can register with only submitting one field.", () => {
      cy.get("#register");
      cy.get(".register-input-wrapper")
        .find("[placeholder='Voornaam, Achternaam']")
        .type(userName);
      cy.get("#register-button").click();

      cy.on("window:alert", (t) => {
        expect(t).to.contains("Wachtwoord moet minimaal 12 tekens lang zijn.");
      });
    });

    it("Submit with non matching password register test - Check if the user can register with submitting a non matching password.", () => {
      cy.get("#register");
      cy.get(".register-input-wrapper")
        .find("[placeholder='Voornaam, Achternaam']")
        .type(userName);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Gebruiker ID']")
        .type(userID);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Bedrijf ID']")
        .type(companyID);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord']")
        .type(password);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord herhalen']")
        .type(`${password}Wrong`);
      cy.get("#register-button").click();

      cy.on("window:alert", (t) => {
        expect(t).to.contains("Wachtwoorden komen niet overeen.");
      });
    });

    it("Submit with non correct password register test - Check if the user can register by submitting a password without a lowercase, uppercase, special character or number.", () => {
      cy.get("#register");
      cy.get(".register-input-wrapper")
        .find("[placeholder='Voornaam, Achternaam']")
        .type(userName);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Gebruiker ID']")
        .type(userID);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Bedrijf ID']")
        .type(companyID);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord']")
        .type(wrongPassword);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord herhalen']")
        .type(wrongPassword);
      cy.get("#register-button").click();

      cy.on("window:alert", (t) => {
        expect(t).to.contains(
          "Wachtwoord moet minimaal één getal, één hoofdletter, één kleine letter en één speciaal karakter hebben."
        );
      });
    });

    it("Go back to login page test - Check if the user can go back to the login page.", () => {
      cy.get("#register");
      cy.get("#backToLogin").click();
      cy.get("#login");
    });

    it("Register second user register test - Check if a second user can be registered.", () => {
      cy.get("#register");
      cy.get(".register-input-wrapper")
        .find("[placeholder='Voornaam, Achternaam']")
        .type(userName2);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Gebruiker ID']")
        .type(userID2);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Bedrijf ID']")
        .type(companyID);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord']")
        .type(password);
      cy.get(".register-input-wrapper")
        .find("[placeholder='Wachtwoord herhalen']")
        .type(password);
      cy.get("#register-button").click();

      cy.get(".grid-container");
    });
  });

  describe("Login screen", () => {
    it("Happy flow login test - Check if the user can login with the credentials.", () => {
      cy.get(".register-input-wrapper")
        .find('[type="text"]')
        .type(userID)
        .type("{enter}");
      cy.get(".register-input-wrapper")
        .find('[type="password"]')
        .type(password)
        .type("{enter}");

      cy.get(".grid-container");
    });

    it("Submit only userID login test - Check if the user can login with only the userID.", () => {
      cy.get(".register-input-wrapper")
        .find('[type="text"]')
        .type(userID)
        .type("{enter}");

      cy.on("window:alert", (t) => {
        expect(t).to.contains("Vul alle velden in");
      });
    });

    it("Submit only password login test - Check if the user can login with only the userID.", () => {
      cy.get(".register-input-wrapper")
        .find('[type="password"]')
        .type(password)
        .type("{enter}");

      cy.on("window:alert", (t) => {
        expect(t).to.contains("Vul alle velden in.");
      });
    });
  });

  describe("Chat screen", () => {
    beforeEach(() => {
      cy.get(".register-input-wrapper")
        .find('[type="text"]')
        .type(userID)
        .type("{enter}");
      cy.get(".register-input-wrapper")
        .find('[type="password"]')
        .type(password)
        .type("{enter}", { delay: 1000 });
      cy.get(".username").contains(userName2).click({ force: true });
    });

    it("Submit a message test - Check if a message can be send to a user with encrypting and decrypting.", () => {
      cy.get(".input-wrapper")
        .find('[type="text"]')
        .type(textMessage)
        .type("{enter}", { delay: 1000 });

      cy.get(".chat").contains(textMessage);
    });

    it("Click button test - Click on the button to submit a message", () => {
      cy.get(".input-wrapper").find('[type="text"]').type(textMessage);

      cy.get("#send").click();

      cy.get(".chat").contains(textMessage);
    });

    it("Delete a message test - Deletes a message with a test", () => {
      cy.get(".input-wrapper").find('[type="text"]').type(textMessage);

      cy.get("#send").click();

      cy.get(".fa-trash").click({ multiple: true });

      cy.on("window:confirm", (s) => {
        return true;
      });

      cy.get(".chat").not(textMessage);
    });

    it("Delete a message cancel test - Cancels the deletes", () => {
      cy.get(".input-wrapper").find('[type="text"]').type(textMessage);

      cy.get("#send").click();

      cy.get(".fa-trash").click({ multiple: true });

      cy.on("window:confirm", (s) => {
        return false;
      });

      cy.get(".chat").contains(textMessage);
    });
    it("Check if a user gets a notification test - The first user logs in and sends a message to the second user. After that, the second user logs in to see if there is a notification.", () => {
      cy.get(".input-wrapper")
        .find('[type="text"]')
        .type(textMessage)
        .type("{enter}", { delay: 1000 });

      cy.get(".chat").contains(textMessage);

      cy.reload();

      cy.get(".register-input-wrapper")
        .find('[type="text"]')
        .type(userID2)
        .type("{enter}");
      cy.get(".register-input-wrapper")
        .find('[type="password"]')
        .type(password)
        .type("{enter}", { delay: 1000 });

      cy.get(".unread");
    });

    it("Send attachment test - Clicks the attachment button and sends an attachment to user 2 ", () => {
      cy.get(".username").contains(userName2).click({ force: true });

      cy.get("input[type=file]").selectFile(
        {
          contents: Cypress.Buffer.from("file contents"),
          fileName: "IMG_3204.png",
          mimeType: "image/png",
          lastModified: Date.now(),
        },
        { force: true }
      );

      cy.get("#send").click();

      cy.get(".attachment").contains("IMG_3204.png");
    });
  });
  

  describe("Broadcasting", () => {
    beforeEach(() => {
      cy.get(".register-input-wrapper")
        .find('[type="text"]')
        .type(userID3)
        .type("{enter}");
      cy.get(".register-input-wrapper")
        .find('[type="password"]')
        .type(password)
        .type("{enter}", { delay: 1000 });
    });

    it("Broadcast a message test", () => {
      // cy.get(".broadcast").click();
      cy.get(".contact-list")
        .find("#broadcastButton")
        .click({force: true});
      cy.get(".broadcast-wrapper")
        .find('[type="text"]')
        .type(textMessage)
      cy.get(".company-tick")
          .find("[type='checkbox']")
          .click({multiple: true});
      cy.get(".broadcast-wrapper")
          .find("#sendBroadcast")
          .click({force: true});
    });

    it("Broadcast a message test - No content given error", () => {
      // cy.get(".broadcast").click();
      cy.get(".contact-list")
        .find("#broadcastButton")
        .click({force: true});
      cy.get(".company-tick")
          .find("[type='checkbox']")
          .click({multiple: true});
      cy.get(".broadcast-wrapper")
          .find("#sendBroadcast")
          .click({force: true});
      cy.on("window:alert", (t) => {
        expect(t).to.contains("Kan geen lege tekst sturen!");
      });
    });

    it("Broadcast a message test - No companies given", () => {
      // cy.get(".broadcast").click();
      cy.get(".contact-list")
        .find("#broadcastButton")
        .click({force: true});
      cy.get(".broadcast-wrapper")
        .find('[type="text"]')
        .type(textMessage)
      cy.get(".broadcast-wrapper")
          .find("#sendBroadcast")
          .click({force: true});
      cy.on("window:alert", (t) => {
        expect(t).to.contains("Selecteer bedrijven!");
      });
    });
  });
});
