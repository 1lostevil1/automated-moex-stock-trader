package org.example.controller;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import org.example.command.*;
import org.example.command.Action.GetTickerForSubscribe;
import com.pengrad.telegrambot.model.Update;
import org.example.command.Action.GetTickerForUnSubscribe;
import org.example.command.Action.GetToken;
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
    private Map<String, Command> actions;

    @Autowired
    public CommandHandler(UserRepository repository) {
        this.repository = repository;
        this.commands = Map.of(
                "/start", new Start(),
                "/subscribe", new Subscribe(),
                "/unsubscribe", new UnSubscribe(),
                "/register_token", new Register()
        );
        this.actions = Map.of(
                "SUBSCRIBE_TICKER", new GetTickerForSubscribe(),
                "UNSUBSCRIBE_TICKER", new GetTickerForUnSubscribe(),
                "WAIT_TOKEN", new GetToken()
        );
    }

    public List<BaseRequest<?, ? extends BaseResponse>> handle(Update update) {
        Long id = update.message().chat().id();
        var state = repository.getState(id);
        if(state.isEmpty() || state.get().equals("NONE")) {
            return executeCommand(update);
        }
        else {
            return actions.get(state.get()).apply(update,repository);
        }
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
