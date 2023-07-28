import ChatDAO from "../dao/chatDAO.js"

export default class ChatController {
    static async apiPostChat(req, res, next) {
        try {
            const messages = req.body.messages
            const status = req.body.status

            const chat = await ChatDAO.addChat(
                status,
                messages
            )
            res.json({ status: "success", chat: chat })
        } catch (e) {
            res.status(500).json({ error: e.message })
        }
    }

    static async apiGetChat(req, res, next) {
        try {
            let id = req.params.id || {}
            let chat = await ChatDAO.getChat(id)
            if (!chat) {
                res.status(404).json({ error: "Not found" })
                return
            }
            res.json(chat)
        } catch (e) {
            console.log(`api, ${e}`)
            res.status(500).json({ error: e })
        }
    }

    static async apiUpdateChat(req, res, next) {
        try {
            let id = req.params.id || {}

            const messages = req.body.messages
            const status = req.body.status

            const chatResponse = await ChatDAO.updateChat(
                status,
                messages,
                id
            )

            var { error } = chatResponse
            if (error) {
                res.status(400).json({ error })
            }

            if (chatResponse.modifiedCount === 0) {
                throw new Error(
                    "unable to update Chat",
                )
            }

            res.json({ status: "success"})
        } catch (e) {
            res.status(500).json({ error: e.message })
        }
    }

    static async apiDeleteChat(req, res, next) {
        try {
            const chatId = req.params.id
            const chatResponse = await ChatDAO.deleteChat(chatId)
            res.json({ status: "success" })
        } catch (e) {
            res.status(500).json({ error: e.message })
        }
    }

    static async apiGetAllChats(req, res, next) {
        try {
            let chats = await ChatDAO.getAllChats()
            if (!chats) {
                res.status(404).json({ error: "Not found" })
                return
            }
            res.json(chats)
        } catch (e) {
            console.log(`api, ${e}`)
            res.status(500).json({ error: e })
        }
    }

}