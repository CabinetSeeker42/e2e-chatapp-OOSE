<template>
  <div class="canvas">
    <div class="contact-list">
      <div class="broadcast-popup" v-for="broadcast in broadcasts">
        ⚠️ Broadcast
        {{ broadcast.sendDate }}
        {{ broadcast.content }}
      </div>
      <div class="grid-container">
        <div class="broadcast" @click="setBroadcast()" v-if="$parent.isSupport">
          <div class="grid-item broadcast">
            <div class="username" id="broadcastButton">
              Broadcast <font-awesome-icon icon="fa-bullhorn" />
            </div>
          </div>
        </div>
        <div class="no-list" v-if="contacts.length == 0">No contacts...</div>
        <div
          v-for="contact in contacts"
          v-bind:key="contact.id"
          class="grid-item"
          @click="selectChat(contact.id)"
          :style="{
            backgroundColor: contact.isSupport
              ? '#ffaa66'
              : contact.id !== receiverID
              ? '#fff'
              : '#aaa',
            color: contact.id !== receiverID ? '#000' : '#fff',
          }"
        >
          <div class="flex">
            <div
              class="username"
              :style="{
                fontWeight: contact.id !== receiverID ? 'normal' : 'bold',
              }"
            >
              {{ formatName(contact.username) }}
              {{ ($parent.isSupport && contact.companyID !== undefined) ? ` - ${contact.companyID}` : "" }}
            </div>
            <div
              class="unread"
              :style="{
                display:
                  notifications[contact.id] === undefined ? 'none' : 'flex',
              }"
            >
              {{ notifications[contact.id] }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="chat-window">
      <TheChat
        v-if="isShown"
        :receiverID="receiverID"
        :receiverPublicKey="receiverPublicKey"
        :n="$parent.n"
        :senderPrivateKey="$parent.privateKey"
        :senderID="$parent.userID"
        ref="theChat"
      />
      <TheBroadcast
        v-if="showBroadcast"
        :n="$parent.n"
        :senderPrivateKey="$parent.privateKey"
        :senderID="$parent.userID"
        :companies="$parent.supportCompanies"
        ref="theBroadcast"
      />
    </div>
  </div>
</template>

<script>
import TheBroadcast from "../components/TheBroadcast.vue";
import TheChat from "../components/TheChat.vue";
import { decryptMessage, generateSharedKey } from "../helper/Encrypter.js";

export default {
  name: "ContactList",
  components: {
    TheChat,
    TheBroadcast,
  },
  data() {
    return {
      isShown: false,
      contacts: [],
      receiverPublicKey: 0,
      receiverID: 0,
      notifications: {},
      showBroadcast: false,
      broadcasts: [],
    };
  },
  methods: {
    formatName: function (name) {
      if (!name.includes(",")) {
        return name;
      }
      const [firstName, lastName] = String(name).split(",");
      return `${firstName[0].toUpperCase()}. ${lastName
        .charAt(0)
        .toUpperCase()}${lastName.slice(1)}`;
    },
    parseMessage(message, sharedKey) {
      message.content =
        message.content === undefined
          ? ""
          : this.setLinks(decryptMessage(message.content, sharedKey));
      if (message.attachmentID !== undefined) {
        message.attachmentName = decryptMessage(
          message.attachmentName,
          sharedKey
        );
        message.isDownloading = false;
      }
      return message;
    },
    setLinks(text) {
      const map = {
        // These are values that should be replaced to prevent HTML injection
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;",
      };
      const linkTemplate = (clickableVar) => {
        return String(clickableVar).startsWith("http")
          ? clickableVar
          : `https://${clickableVar}`;
      };
      return text
        .replace(/[&<>"']/g, function (m) {
          return map[m];
        })
        .replace(
          /([^\s]+[.]+[^\s]+)/gi, //NOSONAR SonarQube wanted to make us think this is a security issue, but it's not. It's just a regex.
          (
            _,
            $1 // This regex is to find links in the string (it looks for "." in every word) and make them clickable
          ) =>
            `<a class="clickable" href="${linkTemplate(
              $1
            )}" target="_blank">${$1}</a>`
        );
    },
    setBroadcastMessages: function (messages) {
      this.broadcasts = [];
      messages.forEach((message) => {
        const sharedKey = generateSharedKey(
          message.publicKey,
          this.$parent.privateKey,
          this.$parent.n
        );
        this.broadcasts.push(this.parseMessage(message.message, sharedKey));
      });
    },
    setBroadcast: function () {
      this.isShown = false;
      this.showBroadcast = !this.showBroadcast;
      this.receiverID = 0;
    },
    setNotifications: function (notifications) {
      this.notifications = {};
      notifications.map((notification) => {
        this.notifications[notification.userID] = notification.unreadCount;
      });
      this.sortContacts();
    },
    incomingAttachment: function (attachment) {
      this.$refs.theChat.incomingAttachment(attachment);
    },
    sendGetMessages: function (receiverID) {
      this.$parent.sendMessage({
        type: "GET_MESSAGES",
        receiverID: receiverID,
      });
    },
    sendMessage: function (message) {
      this.$parent.sendMessage(message);
    },
    sortContacts: function () {
      this.contacts.sort(
        (a, b) =>
          (this.notifications[b.id] === undefined
            ? 0
            : this.notifications[b.id]) +
          b.isSupport * 10000 -
          ((this.notifications[a.id] === undefined
            ? 0
            : this.notifications[a.id]) +
            a.isSupport * 10000)
      );
    },
    addContact: function (contact) {
      this.contacts.push(contact);
      this.sortContacts();
    },
    setContacts: function (contacts) {
      this.contacts = contacts;
      this.sortContacts();
    },
    selectChat: function (receiverID) {
      this.receiverID = receiverID;
      this.receiverPublicKey = this.contacts.find(
        (contact) => contact.id === receiverID
      ).publicKey;
      this.isShown = true;
      this.showBroadcast = false;
    },
    incomingMessage: function (message) {
      if (this.$refs.theChat === null || this.$refs.theChat === undefined) {
        return;
      }
      this.$refs.theChat.addMessage(message);
    },
    setFullChat: function (messages) {
      if (this.$refs.theChat === null || this.$refs.theChat === undefined) {
        return;
      }
      this.$refs.theChat.setFullChat(messages);
      this.$parent.readNotifications(this.receiverID);
    },
  },
};
</script>

<style>
@import "../assets/styles/ContactList.css";
</style>
