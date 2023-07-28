import express from "express";
import ChatController from './chat.controller.js'

const router = express.Router();

router.route("/").get(ChatController.apiGetAllChats)
router.route("/new").post(ChatController.apiPostChat)
router.route("/:id")
    .get(ChatController.apiGetChat)
    .put(ChatController.apiUpdateChat)
    .delete(ChatController.apiDeleteChat)



export default router;
