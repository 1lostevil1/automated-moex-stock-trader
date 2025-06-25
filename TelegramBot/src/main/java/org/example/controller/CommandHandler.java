package org.example.controller;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import org.example.command.*;
import com.pengrad.telegrambot.model.Update;
import org.example.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;
import java.util.Map;

@Component
public class CommandHandler {

    private UserRepository repository;
    private Map<String, Command> commands;

    @Autowired
    public CommandHandler(UserRepository repository) {
        this.repository = repository;
        this.commands = Map.of(
                "/start", new Start(),
                "/stop", new Stop()
        );
    }

    public List<BaseRequest<?, ? extends BaseResponse>> handle(Update update) {
        Long id = update.message().chat().id();
        return executeCommand(update);
    }

    public List<BaseRequest<?, ? extends BaseResponse>> executeCommand(Update update) {
        Long id = update.message().chat().id();
        String message = update.message().text();
        Command command = null;
        if (message != null) {
            command = commands.get(message);
        }
        if (command != null) {
            return command.apply(update,repository);
        }
        return List.of(new SendMessage(id, "неверная команда!"));
    }

}
