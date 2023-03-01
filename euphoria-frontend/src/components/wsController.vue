<template></template>
<script>
import {
  decryptMessage,
  encryptMessage,
  generateSharedKey,
} from "../helper/Encrypter.js";

export default {
  name: "WsController",
  data() {
    return {
      serverIP: `ws://${document.location.host.split(":")[0]}:5000/chat`,
    };
  },
  methods: {
    createConnection: function () {
      this.$parent.socketConnection = new WebSocket(
        `${this.serverIP}/${this.$parent.userID}`
      );
      this.$parent.socketConnection.onmessage = (event) => {
        const message = JSON.parse(event.data);
        switch (message.type) {
          case "CHALLENGE":
            this.solveChallenge(message.challenge, message.serverPublicKey);
            break;
          case "GENERATOR":
            this.$parent.saveGenerator(message.g, message.n);
            if (this.$parent.state === "REGISTER") {
              this.$parent.startRegister();
            }
            if (this.$parent.state === "LOGIN") {
              this.$parent.startLogin();
            }
            break;
          case "ERROR_MESSAGE":
            this.$parent.loggedIn = false;
            alert(message.error);
            this.closeConnection();
            break;
          case "LOGIN_ACCEPTED":
            this.$parent.loggedIn = true;
            this.$parent.isSupport = message.user.isSupport;
            this.sendMessage({ type: "GET_CONTACTS" });
            this.sendMessage({ type: "GET_BROADCASTS" });
            break;
          case "CONTACTS":
            this.$parent.setContacts(message.users);
            this.$parent.supportCompanies = message.supportCompanies;
            break;
          case "CONTACT_UPDATE":
            this.$parent.addContact(message.user);
            break;
          case "REGISTER_SUCCESS":
            this.$parent.loggedIn = true;
            this.sendMessage({ type: "LOGIN" });
            break;
          case "INCOMING_MESSAGE":
            this.$parent.incomingMessage(message.message);
            break;
          case "FULL_CHAT":
            this.$parent.setFullChat(message.messages);
            break;
          case "NOTIFICATIONS":
            this.$parent.setNotifications(message.notifications);
            break;
          case "BROADCASTS":
            this.$parent.setBroadcastMessages(message.messages);
            break;
          case "ATTACHMENT":
            this.$parent.incomingAttachment(message.attachment);
            break;
          default:
          // undefined message type
        }
      };
    },
    readNotifications: function (receiverID) {
      this.sendMessage({
        type: "READ_CHAT",
        receiverID: receiverID,
      });
    },
    isConnected: function () {
      return this.$parent.socketConnection.readyState === WebSocket.OPEN;
    },
    sendMessage: function (message) {
      if (this.isConnected()) {
        message =
          message instanceof String || typeof message === "string"
            ? message
            : JSON.stringify(message);
        this.$parent.socketConnection.send(message);
      } else {
        alert("Not connected with server!");
      }
    },
    closeConnection: function () {
      if (!this.$parent.socketConnection) {
        return;
      }

      this.$parent.socketConnection.close();
      this.$parent.socketConnection = null;
    },

    solveChallenge: function (challenge, publicKeyFromServer) {
      if (!this.$parent.privateKey) {
        return;
      }

      const sharedKey = generateSharedKey(
        publicKeyFromServer,
        this.$parent.privateKey,
        this.$parent.n
      );
      const solution = `SOLVED_${decryptMessage(challenge, sharedKey)}`;
      const encryptedSolution = encryptMessage(solution, sharedKey);

      this.sendMessage({
        type: "SOLVE_CHALLENGE",
        challengeSolution: encryptedSolution,
      });
    },
  },
};
</script>
