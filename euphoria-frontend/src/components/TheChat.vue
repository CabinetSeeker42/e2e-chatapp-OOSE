<template>
  <div class="chat-wrapper">
    <div class="chat">
      <div class="message-loading">
        {{ everLoaded ? "Geen bericht meer..." : "Aan het laden..." }}
      </div>
      <div v-for="message in messages" v-bind:key="message.id">
        <div class="message-container">
          <div
            class="message-item"
            :class="[
              String(message.senderID) === this.senderID
                ? 'message-item-sender'
                : 'message-item-receiver',
            ]"
          >
            <div
              @click="deleteMessage(message.id)"
              v-if="message.senderID === this.senderID"
              class="delete-icon"
            >
              <font-awesome-icon icon="fa-solid fa-trash" />
            </div>
            <div>
              <span v-html="message.content"></span>
              <div
                :class="[
                  String(message.senderID) === this.senderID
                    ? 'attachment-sender'
                    : 'attachment-receiver',
                ]"
                v-if="message.attachmentID !== undefined"
                @click="downloadAttachment(message.attachmentID, message.id)"
              >
                <span v-html="parseAttachment(message.id)"></span>
                <font-awesome-icon
                  icon="download"
                  class="download"
                  v-if="
                    message.attachmentID !== undefined &&
                    message.attachmentContent === undefined &&
                    message.isDownloading == false
                  "
                />
                <img
                  v-if="message.isDownloading == true"
                  src="../assets/loading.gif"
                  alt="Loading"
                  class="loading"
                />
              </div>
            </div>
          </div>
        </div>
        <div
          class="name"
          :class="[
            String(message.senderID) === this.senderID ? 'name-sender' : '',
          ]"
        >
          {{ this.getDisplayDate(message.sendDate) }}
        </div>
      </div>
    </div>
    <div class="input-wrapper">
      <span class="charCount" v-if="inputText.length < 1001">
        {{ inputText.length }}/1000
      </span>
      <span class="alert" v-else> {{ inputText.length }}/1000 </span>
      <input
        type="file"
        ref="file"
        style="display: none"
        v-on:change="sendFile($event)"
      />
      <a class="send-button" @click="$refs.file.click()">
        <font-awesome-icon icon="paperclip" />
      </a>
      <input
        class="input"
        type="text"
        placeholder="Bericht"
        v-model="inputText"
        @keyup.enter="sendMessage"
      />
      <a id="send" class="send-button" @click="sendMessage">
        <font-awesome-icon icon="fa-solid fa-paper-plane" />
      </a>
    </div>
  </div>
</template>

<script scoped>
import {
  decryptMessage,
  encryptMessage,
  generateSharedKey,
} from "../helper/Encrypter.js";
import dayjs from "dayjs";

export default {
  name: "TheChat",
  props: {
    receiverID: String,
    receiverPublicKey: String,
    senderPrivateKey: BigInt,
    senderID: String,
    n: String,
  },
  data: function () {
    return {
      messages: [],
      inputText: "",
      sharedKey: undefined,
      attachments: {},
      everLoaded: false,
    };
  },
  mounted() {
    this.setSharedKey();
    this.$parent.sendGetMessages(this.receiverID);
  },
  methods: {
    setMessage: function (messageID) {
      let message;
      this.messages.forEach((msg) => {
        if (msg.id === messageID) {
          message = msg;
        }
      });
      return message;
    },
    downloadAttachment: function (attachmentID, messageID) {
      const message = this.setMessage(messageID);
      if (attachmentID === undefined) {
        return;
      }
      if (message.attachmentContent !== undefined) {
        this.downloadBase64(
          message.attachmentType,
          message.attachmentContent,
          message.attachmentName
        );
        return;
      }
      message.isDownloading = true;
      this.$parent.sendMessage({
        type: "GET_ATTACHMENT",
        attachmentID,
      });
    },
    parseAttachment: function (messageID) {
      const message = this.setMessage(messageID);
      if (message === undefined) {
        return "";
      }
      if (!("attachmentID" in message)) {
        return "";
      }
      const attachmentName = message.attachmentName;
      return `<div class='attachment'>${attachmentName}</div>`;
    },
    dataURItoBlob: function (dataURI) {
      const byteString = atob(dataURI.split(",")[1]);
      const mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];
      const arrayBuffer = new ArrayBuffer(byteString.length);
      const intArray = new Uint8Array(arrayBuffer);
      for (let index = 0; index < byteString.length; index++) {
        intArray[index] = byteString.charCodeAt(index);
      }
      return new Blob([arrayBuffer], { type: mimeString });
    },
    sendFile: function (event) {
      const files = event.target.files;
      if (files.length === 0) {
        alert("Selecteer een bestand!");
        return;
      }
      const file = files[0];
      const fileReader = new FileReader();
      fileReader.readAsDataURL(file);
      if (file.size > 15000000) {
        alert("Selecteer een bestand dat kleiner is dan 15MB!");
        return;
      }
      fileReader.onload = () => {
        const dataURI = fileReader.result.toString().split(",")[1];
        const encryptedDataURI = encryptMessage(dataURI, this.sharedKey);
        const encryptedFileType = encryptMessage(file.type, this.sharedKey);
        const encryptedFileName = encryptMessage(file.name, this.sharedKey);
        this.$parent.sendMessage({
          type: "MESSAGE",
          receiverID: this.receiverID,
          attachmentContent: encryptedDataURI,
          attachmentType: file.type === "" ? undefined : encryptedFileType,
          attachmentName: encryptedFileName,
        });
        this.$refs.file.value = null;
      };
    },
    getDisplayDate: function (date) {
      return dayjs(date).format("(DD/MM) HH:mm");
    },
    incomingAttachment(attachment) {
      const datatype =
        attachment.type !== undefined
          ? decryptMessage(attachment.type, this.sharedKey)
          : "";
      const name =
        attachment.name !== undefined
          ? decryptMessage(attachment.name, this.sharedKey)
          : "";
      const content =
        attachment.content !== undefined
          ? decryptMessage(attachment.content, this.sharedKey)
          : "";
      this.messages.forEach((message) => {
        if (message.attachmentID === attachment.id.toString()) {
          message.isDownloading = false;
          message.attachmentContent = content;
          message.attachmentType = datatype;
          message.attachmentName = name;
        }
      });
      this.downloadBase64(datatype, content, name);
    },
    downloadBase64(datatype, content, name) {
      const aTag = document.createElement("a"); //Create <a>
      aTag.href = `data:${datatype};base64,${content}`; //Image Base64 goes here
      aTag.download = name;
      aTag.click();
    },
    addMessage: function (message) {
      this.messages.push(this.$parent.parseMessage(message, this.sharedKey));
      this.updateChatScroll();
    },
    deleteMessage: function (messageID) {
      const popup = confirm("Weet u zeker dat u dit bericht wilt verwijderen?");
      if (popup) {
        this.$parent.sendMessage({ type: "DELETE_MESSAGE", messageID });
      }
    },
    setFullChat: function (messages) {
      this.messages = [];
      messages.forEach((message) => {
        this.messages.push(this.$parent.parseMessage(message, this.sharedKey));
      });
      this.updateChatScroll();
      this.everLoaded = true;
    },
    sendMessage: function () {
      const { receiverID, inputText, sharedKey } = this;
      if (this.g === 0 || this.n === 0 || this.sharedKey === 0) {
        alert("Geen generator. Hoe ben je uberhaupt hier gekomen?");
        return;
      }
      if (inputText.trim() === "") {
        alert("Kan geen lege tekst versturen!");
        return;
      }
      if (inputText.length > 1000) {
        alert("Bericht is te lang!");
        return;
      }
      const content = encryptMessage(inputText.trim(), sharedKey);
      this.$parent.sendMessage({
        type: "MESSAGE",
        receiverID,
        content: content.toString(),
      });
      this.inputText = "";
    },
    setSharedKey: function () {
      this.sharedKey = generateSharedKey(
        this.receiverPublicKey,
        this.senderPrivateKey,
        this.n
      );
    },
    updateChatScroll: function () {
      this.$nextTick(function () {
        const chat = this.$el.querySelector(".chat");
        chat.scrollTop = chat.scrollHeight;
      });
    },
  },
  watch: {
    receiverID: {
      handler(newVal, oldVal) {
        this.setSharedKey();
        this.$parent.sendGetMessages(this.receiverID);
      },
    },
  },
};
</script>

<style>
@import "../assets/styles/TheChat.css";
</style>
