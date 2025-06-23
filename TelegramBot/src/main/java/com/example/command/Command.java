package com.example.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.postgres.repository.UserRepository;

public interface Command {
    SendMessage apply(Update update, UserRepository repository);
}
