import { createApp } from "vue";
import App from "./App.vue";
import "./assets/main.css";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faPaperclip, faPaperPlane, faBullhorn, faDownload, faTrash } from "@fortawesome/free-solid-svg-icons";

library.add(faPaperclip);
library.add(faPaperPlane);
library.add(faBullhorn);
library.add(faDownload);
library.add(faTrash);
const app = createApp(App);
app.component("font-awesome-icon", FontAwesomeIcon);
app.mount("#app");
