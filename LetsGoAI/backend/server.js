import express from "express";
import cors from "cors";
import chat from "./api/chat.route.js"

const app = express();

app.use(cors());
app.use(express.json());

app.use("/api/v1/chat", chat);
app.use("*", (req, res) =>
    res.status(404).json({ error: "not found" })
);

export default app;
