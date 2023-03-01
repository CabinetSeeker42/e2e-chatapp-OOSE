<template>
  <div class="broadcast-wrapper">
    <div class="broadcast-view">
      <h1>Nieuwe broadcast</h1>
      <input type="text" placeholder="Formuleer nieuw bericht" v-model="inputText" />
      <div v-for="company in companies" class="company-tick">
        <span>{{ company }}</span>
        <input
          :value="company"
          name="company"
          type="checkbox"
          v-model="checkedCompanies"
        />
      </div>
      <div v-if="isLoading">
        <img src="../assets/loading.gif" alt="Loading" class="loading" />
        Aan het versturen...
      </div>
      <div
        @click="sendBroadcast"
        class="send-broadcast"
        v-if="!isLoading"
        ref="button"
        id="sendBroadcast"
      >
        Versturen
      </div>
    </div>
  </div>
</template>

<script scoped>
import { encryptMessage, generateSharedKey } from "../helper/Encrypter.js";

export default {
  name: "TheBroadcast",
  props: {
    receiverID: String,
    receiverPublicKey: String,
    senderPrivateKey: BigInt,
    senderID: String,
    n: String,
    companies: Object,
  },
  data: function () {
    return {
      inputText: "",
      checkedCompanies: [],
      isLoading: false,
    };
  },
  methods: {
    sendBroadcast: function () {
      this.isLoading = true;
      setTimeout(() => this.sendMessage(), 0);
    },
    sendMessage: function () {
      if (this.inputText.trim() === "") {
        alert("Kan geen lege tekst sturen!");
        this.isLoading = false;
        return;
      }
      this.$parent.contacts.map((contact) => {
        if (this.checkedCompanies.indexOf(contact.companyID) < 0) {
          alert("Selecteer bedrijven!");
          return;
        }
        const sharedKey = generateSharedKey(
          contact.publicKey,
          this.senderPrivateKey,
          this.n
        );
        const message = encryptMessage(this.inputText, sharedKey);
        this.$parent.sendMessage({
          type: "BROADCAST_MESSAGE",
          receiverID: contact.id,
          content: message,
        });
      });
      this.inputText = "";
      this.checkedCompanies = [];
      this.isLoading = false;
      this.$parent.showBroadcast = false;
    },
  },
};
</script>

<style>
@import "../assets/styles/TheBroadcast.css";
</style>
