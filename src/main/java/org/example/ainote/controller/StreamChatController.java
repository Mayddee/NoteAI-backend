package org.example.ainote.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
//@RequiredArgsConstructor
public class StreamChatController {
    private final ChatClient chatClient;

    public StreamChatController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @GetMapping(path = "/string", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam String prompt,
                       @RequestParam(required = false, defaultValue = "en") String lang) {

        String formattedPrompt = String.format("""
            You are a helpful assistant that summarizes user's notes.
            Here's the note content:

            %s

            Please summarize this note in **the same language** it's written in.
            If you can't detect the language, default to %s.
            Keep it concise, no introductions or conclusions.
        """, prompt, lang);

        return chatClient.prompt()
                .user(formattedPrompt)
                .call()
                .content();
    }
}

