<script>
import Register from "./components/Register.vue";
import ContactList from "./components/ContactList.vue";
import WsController from "./components/WsController.vue";
import Login from "./components/Login.vue";

export default {
  name: "App",
  components: {
    Register,
    Login,
    ContactList,
    WsController,
  },
  data() {
    return {
      components: this.components,
      n: undefined,
      g: undefined,
      userID: undefined,
      loggedIn: false,
      socketConnection: undefined,
      privateKey: undefined,
      state: "LOGIN",
      isSupport: false,
      supportCompanies: [],
    };
  },
  methods: {
    openConnection: function (userID) {
      this.userID = userID;
      this.$refs.wsController.createConnection();
    },
    saveGenerator: function (g, n) {
      this.g = g;
      this.n = n;
    },
    addContact: function(user) {
      this.$refs.contactList.addContact(user);
    },
    setBroadcastMessages: function (messages) {
      this.$refs.contactList.setBroadcastMessages(messages);
    },
    startRegister: function () {
      this.$refs.register.register();
    },
    sendMessage: function (message) {
      this.$refs.wsController.sendMessage(message);
    },
    setContacts: function (contacts) {
      this.$refs.contactList.setContacts(contacts);
    },
    incomingMessage: function (message) {
      this.$refs.contactList.incomingMessage(message);
    },
    setFullChat: function (messages) {
      this.$refs.contactList.setFullChat(messages);
    },
    startLogin: function () {
      this.$refs.login.callLogin();
    },
    setNotifications: function (notifications) {
      this.$refs.contactList.setNotifications(notifications);
    },
    readNotifications: function (receiverID) {
      this.$refs.wsController.readNotifications(receiverID);
    },
    incomingAttachment: function (attachment) {
      this.$refs.contactList.incomingAttachment(attachment);
    }
  },
};
</script>

<template>
  <div class="app-wrapper">
    <wsController ref="wsController" />
    <ContactList ref="contactList" v-if="loggedIn" />
    <Login v-else-if="state === 'LOGIN'" ref="login" />
    <Register v-else ref="register" />
  </div>
</template>
