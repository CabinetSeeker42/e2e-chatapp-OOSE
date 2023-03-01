<template>
  <div class="register-wrapper">
    <h1 id="login">Login</h1>

    <div class="register-input-wrapper" v-on:keyup.enter="login">
      <input type="text" placeholder="Gebruiker ID" v-model="userID" />
      <input type="password" placeholder="Wachtwoord" v-model="password" />
    </div>
    <div class="register-button-wrapper">
      <a @click="login">Inloggen</a>
    </div>

    <a @click="setRegisterState" id="goToRegister">Geen account klik hier om te registeren</a>
  </div>
</template>

<script>
import { generatePrivateKey } from "../helper/Encrypter.js";

export default {
  name: "Login",
  data: function () {
    return {
      password: "",
      userID: "",
    };
  },
  methods: {
    setRegisterState: function () {
      this.$parent.state = "REGISTER";
    },
    login: function () {
      const { password, userID } = this;

      if (password.trim() === "" || userID.trim() === "") {
        alert("Vul alle velden in.");
        return;
      }
      this.$parent.privateKey = generatePrivateKey(password);
      this.$parent.openConnection(userID);
    },
    callLogin: function () {
      const { password, userID } = this;
      const privateKey = generatePrivateKey(password);

      this.$parent.privateKey = privateKey;
      this.$parent.userID = userID;
      this.$parent.sendMessage({
        type: "LOGIN",
      });
    },
  },
};
</script>

<style>
@import "../assets/styles/Register.css";
</style>
