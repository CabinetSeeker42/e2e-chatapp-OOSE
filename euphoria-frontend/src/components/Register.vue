<template>
  <div class="register-wrapper">
    <h1 id="register">Registreren</h1>

    <div class="register-input-wrapper" v-on:keyup.enter="sendMessage">
      <div class="jwt-mocker">
        <div class="field-wrapper">
          <input
            type="text"
            placeholder="Voornaam, Achternaam"
            v-model="username"
          />
          <input type="text" placeholder="Gebruiker ID" v-model="userID" />
          <input type="text" placeholder="Bedrijf ID" v-model="companyID" :disabled="isSupport" />

          <input type="password" placeholder="Wachtwoord" v-model="password" />
          <input
            type="password"
            placeholder="Wachtwoord herhalen"
            v-model="confirmPassword"
          />
          <div><input :value="isSupport" name="isSupport" type="checkbox" v-model="isSupport" /> Support?</div>
        </div>
        <div class="field-wrapper companies" v-if="isSupport">
          <div>
            <p>Bedrijf ID's voor support</p>
            <div class="company-list">
              <div class="company-tick" v-for="company in companies">
                {{ company }}
                <input :value="company" name="company" type="checkbox" v-model="checkedCompanies" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="register-button-wrapper">
      <a @click="sendMessage" id="register-button">Registreren</a>
    </div>

    <a @click="setLoginState" id="backToLogin">Terug naar login</a>
  </div>
</template>

<script>
import { generatePrivateKey, generatePublicKey } from "../helper/Encrypter.js";

export default {
  name: "Register",
  data: function () {
    return {
      password: "",
      confirmPassword: "",
      username: "",
      userID: "",
      companyID: "",
      companies: [1,2,3,4,5,6,7,8,9,10],
      checkedCompanies: [],
      isSupport: false,
    };
  },
  methods: {
    setLoginState: function () {
      this.$parent.state = "LOGIN";
    },
    generateJWTToken: function (userID, accountName) {
      const data = {
        details: {
          userId: userID,
          accountName: accountName,
          supportIDs: this.checkedCompanies,
        },
      };
      //All three of these are a mock of the JWTToken
      const header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
      const dataBase64 = window.btoa(JSON.stringify(data));
      const signature = "nLozvCQZva8GlWzezLiv-tRvWwSDO9isfvAeGvavZ0g";

      return `${header}.${dataBase64}.${signature}`;
    },
    sendMessage: function () {
      const { password, confirmPassword, username, userID, companyID, isSupport } = this;
      if (password.length < 12) {
        alert("Wachtwoord moet minimaal 12 tekens lang zijn.");
        return;
      }

      if (
        password.trim() === "" ||
        confirmPassword.trim() === "" ||
        username.trim() === "" ||
        userID.trim() === "" ||
        (isSupport ? false : companyID.trim() === "")
      ) {
        alert("Vul alle velden in.");
        return;
      }
      if (password.trim() !== confirmPassword.trim()) {
        alert("Wachtwoorden komen niet overeen.");
        return;
      }
      const regex =
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?\^&\-=])(?=.*\d)[A-Za-z@$!%*?\^&\-=\d]{12,}$/; //NOSONAR SonarQube wanted to make us think this is a security issue, but it's not. It's just a regex.
      if (!regex.test(password)) {
        alert(
          "Wachtwoord moet minimaal één getal, één hoofdletter, één kleine letter en één speciaal karakter hebben."
        );
        return;
      }
      this.$parent.privateKey = generatePrivateKey(password);
      this.$parent.openConnection(userID);
    },
    register: function () {
      const { password, username, userID, companyID, isSupport } = this;
      const jwtToken = this.generateJWTToken(userID, username);
      const privateKey = generatePrivateKey(password);
      const publicKey = generatePublicKey(
        privateKey,
        this.$parent.g,
        this.$parent.n
      );
      const messageObject = {
        type: "REGISTER",
        publicKey,
        jwtToken,
        companyID,
        isSupport,
      };
      const message = JSON.stringify(messageObject, (key, value) =>
        typeof value === "bigint" ? value.toString() : value
      );
      this.$parent.privateKey = privateKey;
      this.$parent.publicKey = publicKey;
      this.$parent.sendMessage(message);
    },
  },
};
</script>

<style>
@import "../assets/styles/Register.css";
</style>
